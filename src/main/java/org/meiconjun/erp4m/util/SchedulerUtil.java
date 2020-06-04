package org.meiconjun.erp4m.util;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 任务调度工具类
 * @date 2020/6/4 22:13
 */
public class SchedulerUtil {
    private Logger logger = LoggerFactory.getLogger(SchedulerUtil.class);
    /** 调度器 */
    private Scheduler scheduler = null;
    /** 初始化 */
    public SchedulerUtil() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
    }
    public void addCronTrigger(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass, String corn) {
        // 创建jobDetail实例，绑定job实现类
        //指明job名称，所在组名称，以及绑定job类
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        // 定义调度触发规则
        // 使用cornTrigger规则
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(CronScheduleBuilder.cronSchedule(corn)).startNow().build();
        // 把作业和触发器注册到任务调度中
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("创建调度任务[" + jobName + "]失败：" + e.getMessage(), e);
        }
        logger.info("------------------------创建调度任务[" + jobName + "]成功------------------------");
    }

    public void startScheduler() throws SchedulerException {
        scheduler.start();
        logger.info("---------------------启动调度器成功------------------");
    }

    public void closeScheduler() throws SchedulerException {
        scheduler.shutdown();
        logger.info("---------------------停止调度器成功------------------");
    }
}
