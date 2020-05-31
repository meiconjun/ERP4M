package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.RoleBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.ProjectManageDao;
import org.meiconjun.erp4m.service.ProjectManageService;
import org.meiconjun.erp4m.util.CommonUtil;
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
 * @date 2020/5/25 20:15
 */
@Service("projectManageService")
@Transactional
public class ProjectManageServiceImpl implements ProjectManageService {

    private Logger logger = LoggerFactory.getLogger(ProjectManageServiceImpl.class);

    @Resource
    private ProjectManageDao projectManageDao;
    @Resource
    private CommonDao commonDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("getStageInfo".equals(operType)) {
            getStageInfoOperation(requestBean, responseBean);
        } else if ("getStageDocInfo".equals(operType)) {
            getStageDocInfoOperation(requestBean, responseBean);
        } else if ("getStageDocVersion".equals(operType)) {
            getStageDocVersionOperation(requestBean, responseBean);
        } else if ("uploadStageFile".equals(operType)) {
            uploadStageFileOperation(requestBean, responseBean);
        } else if ("nextStage".equals(operType)) {
            nextStageOperation(requestBean, responseBean);
        } else if ("getDownRight".equals(operType)) {
            getDownRightOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 获取项目阶段文档下载权限
     * @param requestBean
     * @param responseBean
     */
    private void getDownRightOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = CommonUtil.getLoginUser().getUser_no();
        String principal = (String) paramMap.get("principal");

        if (user_no.equals(principal)) {
            logger.info("用户[" + user_no + "]为当前阶段负责人，有文档下载权限");
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
            return;
        }
        // 获取阶段负责人的部门经理用户号
        String manager = projectManageDao.selectStagePrincipalManager(principal);
        if (user_no.equals(manager)) {
            logger.info("用户[" + user_no + "]为当前阶段负责人的所属部门经理，有文档下载权限");
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
            return;
        }
        // 获取当前用户的角色职位
        RoleBean role = commonDao.selectUserRoleInfo(user_no);
        String position = role.getPosition();
        if ("4".equals(position)) {
            logger.info("用户[" + user_no + "]为文员，有文档下载权限");
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
            return;
        }
        if ("5".equals(position)) {
            logger.info("用户[" + user_no + "]为老板，有文档下载权限");
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
            return;
        }
        logger.info("当前用户[" + user_no + "]无下载权限");
        responseBean.setRetCode(SystemContants.HANDLE_FAIL);
    }

    /**
     * 项目进入下一阶段
     * @param requestBean
     * @param responseBean
     */
    private void nextStageOperation(RequestBean requestBean, ResponseBean responseBean) throws ParseException {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String project_no = (String) paramMap.get("project_no");
        String stage_num = (String) paramMap.get("stage_num");
        String project_menbers = (String) paramMap.get("project_menbers");
        String project_name = (String)  paramMap.get("project_name");

        //当前阶段标记为已结束,若结束时间与预计不符，后面的都顺延或提前
        String cur_date = CommonUtil.getCurrentDateStr();
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("project_no", project_no);
        condMap.put("stage_num", stage_num);
        condMap.put("end_date", cur_date);
        condMap.put("is_end", "1");
        projectManageDao.updateStageInfo(condMap);
        // 获取当前阶段结束日期,计算提前时间或延后时间
        HashMap<String, String> stageInfo = projectManageDao.selectStageInfo(project_no, stage_num);
        String end_date = stageInfo.get("end_date");

        long betweenDay = CommonUtil.getBetweenDays(end_date, cur_date);

        if (betweenDay != 0) {//与预期有偏差
            List<HashMap<String, String>> stageList = projectManageDao.selectStageOfProject(project_no);
            HashMap<String, Object> condMap2 = new HashMap<String, Object>();
            condMap2.put("project_no", project_no);
            condMap2.put("stage_num", stage_num);
            for (HashMap<String, String> m : stageList) {
                String stage_num_cur = m.get("stage_num");
                if (Integer.valueOf(stage_num_cur) > Integer.valueOf(stage_num)) {// 当前阶段后的阶段
                    String begin_date_cur = m.get("begin_date");
                    String end_date_cur = m.get("end_date");
                    begin_date_cur = CommonUtil.getDateAfterDays(begin_date_cur, new Long(betweenDay).intValue());
                    end_date_cur = CommonUtil.getDateAfterDays(end_date_cur, new Long(betweenDay).intValue());
                    condMap2.put("begin_date", begin_date_cur);
                    condMap2.put("end_date", end_date_cur);
                    projectManageDao.updateStageInfo(condMap2);

                }
            }
        }
        //如果是已经是最后阶段结束，则更新项目为结项，并通知所有人员
        String lastStageNum = projectManageDao.selectMaxStageNumOfProject(project_no);
        HashMap<String, Object> mainMap = new HashMap<String, Object>();
        mainMap.put("project_no", project_no);
        if (stage_num.equals(lastStageNum)) {
            stage_num = String.valueOf(Integer.valueOf(stage_num) + 1);
            logger.info("===================项目编号[" + project_no + "]最后阶段完结，项目结项==============");
            mainMap.put("end_date", cur_date);
            mainMap.put("project_state", "4");//结项

            List<String> allUserList =commonDao.selectAllUserNo();
            MessageBean msgBean = new MessageBean();
            msgBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_PROJECT_END);
            msgBean.setMsg_param(new HashMap<>());
            msgBean.setCreate_time(CommonUtil.getCurrentTimeStr());
            msgBean.setCreate_user("system");
            msgBean.setMsg_content("项目[" + project_name + "]结项，结项日期" + CommonUtil.formatDateString(cur_date, "yyyyMMdd", "yyyy-MM-dd"));
            String msgNo = CommonUtil.addMessageAndSend(allUserList, null, msgBean, "2", CommonUtil.getCurrentDateAfterDays(365));
            msgBean.setMsg_no(msgNo);
            WebsocketMsgUtil.sendMsgToMultipleUser(allUserList, null, msgBean);
            responseBean.setRetCode("3");//结项
        } else {
            stage_num = String.valueOf(Integer.valueOf(stage_num) + 1);
            //OR项目主表阶段计数+1,推送消息给下一阶段负责人
            stageInfo = projectManageDao.selectStageInfo(project_no, stage_num);
            HashMap<String, String> stageDocInfo = projectManageDao.selectStageDocInfo(project_no, stage_num).get(0);
            String nextPrincipal = stageInfo.get("principal");
            String nextStage = stageInfo.get("stage");
            String nextStageName = CommonUtil.getFieldName(SystemContants.FIELD_STAGE, nextStage);
            String nextStageEnd = stageInfo.get("end_date");
            String nextDocName = stageDocInfo.get("doc_name");
            String nextDocWriter = stageDocInfo.get("doc_writer");
            MessageBean messageBean = new MessageBean();
            messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
            messageBean.setCreate_user("system");
            messageBean.setMsg_content("项目[" + project_name + "]阶段[" + nextStageName + "]开始<br>请在结束日期[" + nextStageEnd + "]" +
                    "前上传阶段文档<br>[" + nextDocName + ",作者：" + nextDocWriter + "]");
            messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_PROJECT_STAGE);//项目阶段提醒
            messageBean.setMsg_param(new HashMap<>());
            List<String> userList = Arrays.asList(new String[]{nextPrincipal});
            String msg_no = CommonUtil.addMessageAndSend(userList, null, messageBean, "1", CommonUtil.getCurrentDateAfterDays(7));
            messageBean.setMsg_no(msg_no);
            WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);
        }
        mainMap.put("stage_num", stage_num);
        projectManageDao.updateProjectMainInfo(mainMap);
    }

    /**
     * 更新阶段文档信息
     * @param requestBean
     * @param responseBean
     */
    private void uploadStageFileOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String project_no = (String) paramMap.get("project_no");
        String stage_num = (String) paramMap.get("stage_num");
        String doc_version = (String) paramMap.get("doc_version");
        String doc_serial = (String) paramMap.get("doc_serial");
        String filePath = (String) paramMap.get("filePath");
        if ("1.0".equals(doc_version)) {
            //更新1.0
            logger.info("更新阶段文档，阶段未结束，直接覆盖原文档");
            projectManageDao.updateStageDoc(CommonUtil.getCurrentDateStr(), filePath, doc_serial);
        } else {
            //新增版本
            logger.info("更新阶段文档，版本已结束，版本迭代为[" + doc_version + "]");
            List<HashMap<String, String>> docList = projectManageDao.selectStageDocInfo(project_no, stage_num);
            HashMap<String, String> docInfo = docList.get(0);
            String doc_no = docInfo.get("doc_no");
            String doc_name = docInfo.get("doc_name");
            String doc_writer = docInfo.get("doc_writer");

            HashMap<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("serial_no", doc_serial);
            condMap.put("doc_no", doc_no);
            condMap.put("doc_name", doc_name);
            condMap.put("doc_writer", doc_writer);
            condMap.put("doc_version", doc_version);
            condMap.put("upload_date", CommonUtil.getCurrentDateStr());
            condMap.put("file_path", filePath);
            projectManageDao.insertStageDocInfo(condMap);
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询阶段文档版本
     * @param requestBean
     * @param responseBean
     */
    private void getStageDocVersionOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String project_no = (String) paramMap.get("project_no");
        String stage_num = (String) paramMap.get("stage_num");

        String doc_version = projectManageDao.selectStageDocLastVersion(project_no, stage_num);
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("doc_version", doc_version);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询阶段文档信息
     * @param requestBean
     * @param responseBean
     */
    private void getStageDocInfoOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String project_no = (String) paramMap.get("project_no");
        String stage_num = (String) paramMap.get("stage_num");

        List<HashMap<String, String>> docList = projectManageDao.selectStageDocInfo(project_no, stage_num);

        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("list", docList);
        retMap.put("total", Long.valueOf(docList.size()));
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询项目阶段信息
     * @param requestBean
     * @param responseBean
     */
    private void getStageInfoOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String project_no = (String) paramMap.get("project_no");

        List<HashMap<String, String>> stageList = projectManageDao.selectStageOfProject(project_no);

        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("stageList", stageList);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询操作
     * @param requestBean
     * @param responseBean
     */
    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, String> beanMap = (Map<String, String>)requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();

        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        String project_name = beanMap.get("project_name");
        String chn_name = beanMap.get("chn_name");
        String principal = beanMap.get("principal");

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("project_name", project_name);
        condMap.put("chn_name", chn_name);
        condMap.put("principal", principal);

        Page page = PageHelper.startPage(curPage, limit);

        List<HashMap<String, String>> list = projectManageDao.selectProjectInfo(condMap);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
