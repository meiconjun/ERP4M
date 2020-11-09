package org.meiconjun.erp4m.common;

import org.meiconjun.erp4m.util.SchedulerUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 应用启动后需要执行的初始化操作
 * @date 2020/6/4 22:40
 */
@Component
public class SystemInit {

    private Logger logger = LoggerFactory.getLogger(SystemInit.class);

    // PostConstruct注解的方法会在Spring启动后自动执行
    @PostConstruct
    private void execute() throws Exception {
        logger.info("---------------------执行初始化操作----------------------");
        // do something
        initScheduler();
    }

    /**
     * 注册job，启动scheduler TODO 后续优化页面配置需要执行的job和时间，从库中读取
     */
    private void initScheduler() throws SchedulerException {
        SchedulerUtil schedulerUtil = new SchedulerUtil();
        // 项目阶段提醒 每天凌晨1点执行
        schedulerUtil.addCronTrigger("项目阶段提醒", "项目Job", "项目阶段提醒Trigger",
                "通用", org.meiconjun.erp4m.job.ProjectStageRemind.class, "0 0 1 * * ? *");
        // 文档回收站清理 每天林晨2点执行
        schedulerUtil.addCronTrigger("文档回收站清理", "文档Job", "文档回收站清理Trigger",
                "通用", org.meiconjun.erp4m.job.RecycleDocClean.class, "0 0 2 * * ? *");

        //启动
        schedulerUtil.startScheduler();
    }
}
