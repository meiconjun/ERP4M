package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.*;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.PersonalDocDao;
import org.meiconjun.erp4m.dao.PublicDocDao;
import org.meiconjun.erp4m.service.PersonalDocService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.meiconjun.erp4m.util.WebsocketMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private PublicDocDao publicDocDao;
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
        } else if ("judgeDenied".equals(operType)) {
            judgeDenidOperation(requestBean, responseBean);
        } else if ("judgePass".equals(operType)) {
            judgePassOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 裁决通过，发行文档
     * @param requestBean
     * @param responseBean
     */
    private void judgePassOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String doc_serial_no = (String) paramMap.get("doc_serial_no");
        String judge_user = (String) paramMap.get("judge_user");

        HashMap<String, Object> condMap = new HashMap();
        // 更新文档审阅信息
        condMap.put("doc_serial_no", doc_serial_no);
        condMap.put("judge_user", judge_user);
        condMap.put("judge_time", CommonUtil.getCurrentTimeStr());
        condMap.put("review_state", "3");
        personalDocDao.updateDocReviewInfo(condMap);
        // 获取文档信息,插入公共文档主表
        DocBean docBean = personalDocDao.selectPersonalDocInfo(condMap).get(0);
        docBean.setLast_modi_time(CommonUtil.getCurrentTimeStr());
        docBean.setLast_modi_user(CommonUtil.getLoginUser().getUser_no());
        publicDocDao.insertNewPublicDocInfo(docBean);
        // 更新文档版本信息
        condMap.put("doc_version", docBean.getDoc_version());

        if (!CommonUtil.isStrBlank((String) paramMap.get("remarks"))) {
            condMap.put("update_desc", (String) paramMap.get("remarks"));
        }
        personalDocDao.updateDocVersionInfo(condMap);
        // 删除个人文档库数据
        personalDocDao.deletePersonalDocInfo(doc_serial_no);

        // 推送提醒文档创建者文档已成功发行
        MessageBean messageBean = new MessageBean();
        messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
        messageBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
        messageBean.setMsg_content("您提交的文档[" + docBean.getDoc_no() + "]已审核通过，成功发行到公共文档库");
        messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_DOC_PASS);
        messageBean.setMsg_param(new HashMap<>());
        List<String> userList = Arrays.asList(docBean.getUpload_user());
        String deal_type = "1";
        String end_time = "";
        String msg_no = CommonUtil.addMessageAndSend(userList, null, messageBean, deal_type, end_time);
        messageBean.setMsg_no(msg_no);
        WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 裁决人驳回文档
     * @param requestBean
     * @param responseBean
     */
    private void judgeDenidOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String doc_serial_no = (String) paramMap.get("doc_serial_no");
        String judge_user = (String) paramMap.get("judge_user");
        String opinion = (String) paramMap.get("opinion");
        String doc_no = (String) paramMap.get("doc_no");
        String upload_user = (String) paramMap.get("upload_user");

        // 更新文档状态和失败原因
        HashMap<String, Object> condMap = new HashMap<>();
        condMap.put("review_state", "4");// 驳回
        condMap.put("judge_user", judge_user);
        condMap.put("judge_reason", opinion);
        condMap.put("judge_time", CommonUtil.getCurrentTimeStr());
        condMap.put("doc_serial_no", doc_serial_no);
        personalDocDao.updateDocReviewInfo(condMap);

        // 提醒文档创建者文档已被驳回
        MessageBean messageBean = new MessageBean();
        messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
        messageBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
        messageBean.setMsg_content("您提交的文档[" + doc_no + "]已被驳回<br>驳回原因：" + opinion);
        messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_DOC_DENIED);
        messageBean.setMsg_param(new HashMap<>());
        List<String> userList = Arrays.asList(upload_user);
        String deal_type = "1";
        String end_time = "";
        String msg_no = CommonUtil.addMessageAndSend(userList, null, messageBean, deal_type, end_time);
        messageBean.setMsg_no(msg_no);
        WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
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
        Map<String, Object> docBean = (Map<String, Object>) paramMap.get("docBean");


        // 更新文档审阅信息
        HashMap<String, Object> reviewInfo = personalDocDao.selectDocReviewInfoBySerial(doc_serial_no);

        String review_detail = (String) reviewInfo.get("review_detail");
        Map<String, String> reviewDetailMap;
        if (CommonUtil.isStrBlank(review_detail)) {
            reviewDetailMap = new HashMap<String, String>();
        } else {
            reviewDetailMap = (Map<String, String>) CommonUtil.jsonToObj(review_detail, Map.class);
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
            // 创建裁决任务,推送裁决人
            String judge_user = (String) reviewInfo.get("judge_user");//裁决者用户号
            // 获取文档信息
            docBean.put("reviewDetail", reviewDetailMap);// 审阅详情

            TaskBean taskBean_judge = new TaskBean();
            taskBean_judge.setTask_no(SerialNumberGenerater.getInstance().generaterNextNumber());
            taskBean_judge.setCreate_time(CommonUtil.getCurrentTimeStr());
            taskBean_judge.setCreate_user(CommonUtil.getLoginUser().getUser_no());
            taskBean_judge.setDeal_type("1");
            taskBean_judge.setDeal_user("");
            taskBean_judge.setEnd_time("");
            taskBean_judge.setReceive_role("");
            taskBean_judge.setReceive_user(judge_user);
            taskBean_judge.setStatus("0");
            taskBean_judge.setTask_title("文档裁决，文档号[" + docBean.get("doc_no") + "]");
            taskBean_judge.setTask_type(SystemContants.FIELD_TASK_TYPE_DOC_JUDGE);
            taskBean_judge.setTask_param(CommonUtil.objToJson(docBean));

            int effect = commonDao.insertTaskInfo(taskBean_judge);
            if (effect == 0) {
                throw new RuntimeException("文档审阅失败！");
            }
            // 推送提醒
            MessageBean messageBean = new MessageBean();
            messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
            messageBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
            messageBean.setMsg_content("新增待裁决的文档，请在任务列表中处理");
            messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_DOC_JUDGE);
            messageBean.setMsg_param(new HashMap<>());
            List<String> userList = Arrays.asList(judge_user);
            String deal_type = "1";
            String end_time = "";
            String msg_no = CommonUtil.addMessageAndSend(userList, null, messageBean, deal_type, end_time);
            messageBean.setMsg_no(msg_no);
            WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);
        }
        personalDocDao.updateDocReviewInfo(reviewMap);
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
        String remarks = (String) paramMap.get("remarks");// 备注
        docBean.setRemarks(remarks);

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("doc_serial_no", docBean.getDoc_serial_no());
        condMap.put("review_user", reviewer);
        condMap.put("judge_user", adjudicator);
        condMap.put("remarks", remarks);
        condMap.put("review_state", "1");
        // update doc review_state
        int effect = personalDocDao.updateDocReviewInfo(condMap);

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
            taskBean.setTask_title("文档审阅，文档号[" + docBean.getDoc_no() + "]");
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
            condMap.put("last_modi_user", CommonUtil.getLoginUser().getUser_no());

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
        condMap.put("doc_desc", docBean.getDoc_desc());
        condMap.put("upload_time", upload_time);
        condMap.put("last_modi_time", last_modi_time);
        condMap.put("last_modi_user", upload_user);
        condMap.put("doc_writer", docBean.getDoc_writer());
        condMap.put("file_path", docBean.getFile_path());
        condMap.put("doc_version", docBean.getDoc_version());

        personalDocDao.updatePersonalDocInfo(condMap);
//        personalDocDao.insertDocVersionInfo(condMap); 修改时不改变文档版本，只有新增和检出时改变
        personalDocDao.updateDocVersionInfo(condMap);

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
        condMap.put("last_modi_user", bean.getLast_modi_user());
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
        condMap.put("last_modi_user", upload_user);

        personalDocDao.insertPersonalDocInfo(condMap);
        // 审批表新增数据
        personalDocDao.insertDocReviewInfo(condMap);
        // 新增版本信息
        personalDocDao.insertDocVersionInfo(condMap);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }
}
