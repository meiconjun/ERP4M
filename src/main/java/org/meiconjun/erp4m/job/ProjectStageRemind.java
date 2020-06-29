package org.meiconjun.erp4m.job;

import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CreateProjectDao;
import org.meiconjun.erp4m.dao.ProjectDocDefindDao;
import org.meiconjun.erp4m.dao.ProjectManageDao;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.WebsocketMsgUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

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
 * @Description: 在阶段结束前三天提醒负责人上传文档
 * @date 2020/6/4 22:44
 */
public class ProjectStageRemind implements Job {
    private Logger logger = LoggerFactory.getLogger(ProjectStageRemind.class);

    @Autowired
    private ProjectManageDao projectManageDao;
    @Resource
    private ProjectDocDefindDao projectDocDefindDao;
    @Resource
    private CreateProjectDao createProjectDao;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 注入bean
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        long startTime = System.currentTimeMillis();
        logger.info("---------------------[job:项目阶段提醒]执行开始------------------------");
        // 执行具体逻辑
        // 查询所有项目进行中阶段信息
        List<HashMap<String, Object>> stageInfoList = projectManageDao.selectProjectCurStage();
        //依次判断是否已临近结束日期，并推送消息
        String curDate = CommonUtil.getCurrentDateStr();
        for (HashMap<String, Object> stageInfo : stageInfoList) {
            String end_date = (String) stageInfo.get("end_date");
            try {
                String remindDate = CommonUtil.getDateAfterDays(end_date, -3);
                if (Integer.parseInt(curDate) >= Integer.parseInt(remindDate)) {
                    String project_name = (String) stageInfo.get("project_name");
                    String stage_num = (String) stageInfo.get("stage_num");
                    String principal = (String) stageInfo.get("principal");
                    String unupload_doc = (String) stageInfo.get("unupload_doc");
                    for (String doc_no : unupload_doc.split(",")) {
                        HashMap<String, String> projectDocInfo = projectDocDefindDao.selectProjectDocInfoByDocNo(doc_no);
                        String department = projectDocInfo.get("department");
                        String duty_role = projectDocInfo.get("duty_role");
                        String doc_name = projectDocInfo.get("doc_name");
                        String writer = projectDocInfo.get("writer");
                        String project_menbers = projectDocInfo.get("project_menbers");

                        // 根据当前阶段各文档负责部门与项目成员判断需要推送的用户
                        HashMap<String, Object> condMap = new HashMap<>();
                        condMap.put("department", department);
                        condMap.put("duty_role", duty_role);
                        condMap.put("project_menbers", CommonUtil.addSingleQuo(project_menbers));
                        List<String> userList = createProjectDao.selectStageDocDutyUser(condMap);

                        String stage = (String) stageInfo.get("stage");
                        String stage_end_date = (String) stageInfo.get("end_date");//结束时间
                        String stage_name = CommonUtil.getFieldName(SystemContants.FIELD_STAGE, stage);

                        MessageBean messageBean = new MessageBean();
                        messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
                        messageBean.setCreate_user("");
                        messageBean.setMsg_content("项目[" + project_name + "]阶段[" + stage_name + "]临近预定截止日<br>请在结束日期[" + stage_end_date + "]" +
                                "前上传阶段文档<br>[" + doc_name + ",作者：" + writer + "]<br>已在任务列表中新增该任务");
                        messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_PROJECT_STAGE);//项目阶段提醒
                        Map<String, Object> msg_param = new HashMap<String, Object>();
                        // 填入前端所需要素 推送
                        messageBean.setMsg_param(msg_param);
                        String deal_type = "2";
                        String end_time = CommonUtil.getCurrentDateAfterDays(7);
                        String msg_no2 = CommonUtil.addMessageAndSend(userList, null, messageBean, deal_type, end_time);
                        messageBean.setMsg_no(msg_no2);
                        WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);
                    }
                }
            } catch (ParseException e) {
                logger.error("执行项目阶段提醒Job异常:" + e.getMessage(), e);
            }
            long entTime = System.currentTimeMillis();
            logger.info("---------------------[job:项目阶段提醒]执行结束,耗时" + (entTime - startTime) + "毫秒------------------------");
        }
    }
}
