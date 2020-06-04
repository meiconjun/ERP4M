package org.meiconjun.erp4m.job;

import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.common.SystemContants;
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

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
                    String doc_name = (String) stageInfo.get("doc_name");
                    String doc_writer = (String) stageInfo.get("doc_writer");
                    String principal = (String) stageInfo.get("principal");

                    MessageBean messageBean = new MessageBean();
                    messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
                    messageBean.setCreate_user("system");
                    messageBean.setMsg_content("项目[" + project_name + "]阶段[" + stage_num + "]开始<br>请在结束日期[" + CommonUtil.formatDateString(end_date, "yyyyMMdd", "yyyy-MM-dd") + "]" +
                            "前上传阶段文档<br>[" + doc_name + ",作者：" + doc_writer + "]");
                    messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_PROJECT_STAGE);
                    messageBean.setMsg_param(new HashMap<>());
                    List<String> userList = Arrays.asList(new String[]{principal});
                    String msgNo = CommonUtil.addMessageAndSend(userList, null, messageBean, "1", CommonUtil.getCurrentDateAfterDays(7));
                    messageBean.setMsg_no(msgNo);
                    WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);
                }
            } catch (ParseException e) {
                logger.error("执行项目阶段提醒Job异常:" + e.getMessage(), e);
            }
            long entTime = System.currentTimeMillis();
            logger.info("---------------------[job:项目阶段提醒]执行结束,耗时" + (entTime - startTime) + "毫秒------------------------");
        }
    }
}
