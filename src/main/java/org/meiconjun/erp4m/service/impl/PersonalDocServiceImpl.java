package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.*;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.PersonalDocDao;
import org.meiconjun.erp4m.service.PersonalDocService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.meiconjun.erp4m.util.WebsocketMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.print.Doc;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/11 21:20
 */
@Service("personalDocService")
@Transactional
public class PersonalDocServiceImpl implements PersonalDocService {

    private Logger logger = LoggerFactory.getLogger(PersonalDocServiceImpl.class);

    @Resource
    private PersonalDocDao personalDocDao;
    @Resource
    private CommonDao commonDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("add".equals(operType)){
            addOperation(requestBean, responseBean);
        } else if ("modify".equals(operType)) {
            modifyOperation(requestBean, responseBean);
        } else if ("delete".equals(operType)) {
            deleteOperation(requestBean, responseBean);
        } else if ("getDocHistory".equals(operType)) {
            getDocHistoryOperation(requestBean, responseBean);
        } else if ("reviewSubmit".equals(operType)) {
            reviewSubmitOperation(requestBean, responseBean);
        } else if ("userReview".equals(operType)) {
            userReviewOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 审阅人审阅提交
     * @param requestBean
     * @param responseBean
     */
    private void userReviewOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String doc_serial_no = (String) paramMap.get("doc_serial_no");
        String review_user = (String) paramMap.get("review_user");
        String opinion = (String) paramMap.get("opinion");
        String task_no = (String) paramMap.get("task_no");

        // 更新文档审阅信息
        HashMap<String, Object> reviewInfo = personalDocDao.selectDocReviewInfoBySerial(doc_serial_no);

        String review_detail = (String) reviewInfo.get("review_detail");
        Map<String, String> reviewDetailMap = null;
        if (CommonUtil.isStrBlank(review_detail)) {
            reviewDetailMap = new HashMap<String, String>();
        } else {
            reviewDetailMap = (HashMap<String, String>) CommonUtil.jsonToObj(review_detail, Map.class);
        }
        reviewDetailMap.put(review_user, opinion + SystemContants.DELIMITER + CommonUtil.getCurrentTimeStr());
        HashMap<String, Object> reviewMap = new HashMap<String, Object>();
        reviewMap.put("doc_serial_no", doc_serial_no);
        reviewMap.put("review_detail", CommonUtil.objToJson(reviewDetailMap));

        // 更新任务已处理人列表,若是所有审阅人已审阅完毕，推送任务到裁决人处
        TaskBean taskBean = commonDao.selectTaskInfo(task_no);
        String receive_user = taskBean.getReceive_user();
        String deal_user = taskBean.getDeal_user();
        if (CommonUtil.isStrBlank(deal_user)) {
            deal_user = review_user;
        } else {
            deal_user += "," + review_user;
        }
        int length1 = receive_user.split(",").length;
        int length2 = deal_user.split(",").length;
        TaskBean taskBean2 = new TaskBean();
        taskBean2.setTask_no(task_no);
        taskBean2.setDeal_user(deal_user);
        if (length2 >= length1) {
            reviewMap.put("review_state", "2");
            taskBean2.setStatus("1");
            //TODO 创建裁决任务,推送裁决人


        }
        personalDocDao.updateDocReviewState(reviewMap);
        commonDao.updateTaskInfo(taskBean2);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 提交评审，更新文档状态，推送消息等
     * @param requestBean
     * @param responseBean
     */
    private void reviewSubmitOperation(RequestBean requestBean, ResponseBean responseBean) {
        DocBean docBean = (DocBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        String reviewer = (String) paramMap.get("reviewer");// 审阅者
        String adjudicator = (String) paramMap.get("adjudicator");// 裁决者

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("doc_serial_no", docBean.getDoc_serial_no());
        condMap.put("review_user", reviewer);
        condMap.put("judge_user", adjudicator);

        // update doc review_state
        int effect = personalDocDao.updateDocReviewState(condMap);

        // send msg and task to reviewers
        if (effect > 0) {

            // 创建任务
            TaskBean taskBean = new TaskBean();
            taskBean.setTask_no(SerialNumberGenerater.getInstance().generaterNextNumber());
            taskBean.setCreate_time(CommonUtil.getCurrentTimeStr());
            taskBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
            taskBean.setDeal_type("2");
            taskBean.setDeal_user("");
            taskBean.setEnd_time("");
            taskBean.setReceive_role("");
            taskBean.setReceive_user(reviewer);
            taskBean.setStatus("0");
            taskBean.setTask_title("文档审阅:" + docBean.getDoc_no());
            taskBean.setTask_type(SystemContants.FIELD_TASK_TYPE_DOC_REVIEW);
            taskBean.setTask_param(CommonUtil.objToJson(docBean));

            effect = commonDao.insertTaskInfo(taskBean);
            if (effect > 0) {
                // 推送消息
                MessageBean messageBean = new MessageBean();
                messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
                messageBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
                messageBean.setMsg_content("新增待审阅的文档，请在任务列表中处理");
                messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_DOC_REVIEW);
                messageBean.setMsg_param(new HashMap<>());
                List<String> userList = Arrays.asList(reviewer.split(","));
                String deal_type = "2";
                String end_time = "";
                String msg_no = CommonUtil.addMessageAndSend(userList, null, messageBean, deal_type, end_time);
                messageBean.setMsg_no(msg_no);
                WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);

                responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
            } else {
                responseBean.setRetCode(SystemContants.HANDLE_FAIL);
                responseBean.setRetMsg("提交评审失败，请稍后重试");
            }
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("提交评审失败，请稍后重试");
        }

    }

    /**
     * 查询文档版本历史
     * @param requestBean
     * @param responseBean
     */
    private void getDocHistoryOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String doc_serial_no = (String) paramMap.get("doc_serial_no");

        List<HashMap<String, Object>> docList = personalDocDao.selectDocHistory(doc_serial_no);

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("docList", docList);
        retMap.put("total", docList.size());
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 删除操作，将文档数据移到回收站表，保留90天
     * @param requestBean
     * @param responseBean
     */
    private void deleteOperation(RequestBean requestBean, ResponseBean responseBean) throws ParseException {
        List<DocBean> docBeanList = requestBean.getBeanList();
        String delete_time = CommonUtil.getCurrentTimeStr();
        String expire_time = CommonUtil.getTimeAfterDays(delete_time, 90);
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        for (DocBean docBean : docBeanList) {
            // 删除个人文档表数据
            personalDocDao.deletePersonalDocInfo(docBean.getDoc_serial_no());
            // 插入回收站表数据
            condMap.put("docBean", docBean);
            condMap.put("delete_time", delete_time);
            condMap.put("expire_time", expire_time);
            condMap.put("delete_state", "0");
            personalDocDao.insertRecycleInfo(condMap);
            logger.info("文档编号[" + docBean.getDoc_no() + "]移入回收站--------");
        }

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 修改操作
     * @param requestBean
     * @param responseBean
     */
    private void modifyOperation(RequestBean requestBean, ResponseBean responseBean) {
        DocBean docBean = (DocBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        String file_root_path = (String) paramMap.get("file_root_path");
        String doc_serial_no = docBean.getDoc_serial_no();
        String upload_user = CommonUtil.getLoginUser().getUser_no();
        String upload_time = CommonUtil.getCurrentTimeStr();
        String last_modi_time = upload_time;

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("docBean", docBean);
        condMap.put("file_root_path", file_root_path);
        condMap.put("doc_serial_no", doc_serial_no);
        condMap.put("upload_user", upload_user);
        condMap.put("upload_time", upload_time);
        condMap.put("last_modi_time", last_modi_time);

        personalDocDao.updatePersonalDocInfo(condMap);
        personalDocDao.insertDocVersionInfo(condMap);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询文档信息
     * @param requestBean
     * @param responseBean
     */
    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        DocBean bean = (DocBean) requestBean.getBeanList().get(0);
        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("doc_no", bean.getDoc_no());
        condMap.put("doc_name", bean.getDoc_name());
        condMap.put("upload_user", bean.getUpload_user());
        condMap.put("doc_type", bean.getDoc_type());

        Page page = PageHelper.startPage(curPage, limit);
        List<DocBean> list = personalDocDao.selectPersonalDocInfo(condMap);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 新增文档
     * @param requestBean
     * @param responseBean
     */
    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        DocBean docBean = (DocBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        String file_root_path = (String) paramMap.get("file_root_path");
        String doc_serial_no = SerialNumberGenerater.getInstance().generaterNextNumber();
        String upload_user = CommonUtil.getLoginUser().getUser_no();
        String upload_time = CommonUtil.getCurrentTimeStr();
        String last_modi_time = upload_time;

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("docBean", docBean);
        condMap.put("file_root_path", file_root_path);
        condMap.put("doc_serial_no", doc_serial_no);
        condMap.put("upload_user", upload_user);
        condMap.put("upload_time", upload_time);
        condMap.put("last_modi_time", last_modi_time);

        personalDocDao.insertPersonalDocInfo(condMap);
        // 审批表新增数据
        personalDocDao.insertDocReviewInfo(condMap);
        // 新增版本信息
        personalDocDao.insertDocVersionInfo(condMap);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }
}
