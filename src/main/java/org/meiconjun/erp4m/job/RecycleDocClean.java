package org.meiconjun.erp4m.job;

import org.meiconjun.erp4m.config.CustomConfigProperties;
import org.meiconjun.erp4m.dao.RecycleStationDao;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.FileUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 回收站文档定期清理
 * @date 2020/7/7 21:50
 */
public class RecycleDocClean implements Job {
    private Logger logger = LoggerFactory.getLogger(RecycleDocClean.class);

    @Resource
    private RecycleStationDao recycleStationDao;
    @Resource
    private CustomConfigProperties customConfigProperties;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 注入bean
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        long startTime = System.currentTimeMillis();
        logger.info("---------------------[job:文档回收站清理]执行开始------------------------");
        String curTime = CommonUtil.getCurrentTimeStr();// 当前时间
        String root_path = customConfigProperties.getFileSavePath();// 文件存储根路径
        HashMap<String, Object> condMap = new HashMap<>();
        condMap.put("expire_time", curTime);
        List<HashMap<String, Object>> docList = recycleStationDao.selectPersonalRecycleDocInfo(condMap);
        if (!docList.isEmpty()) {
            for (HashMap<String, Object> map : docList) {
                logger.info("------------------------删除文档，编号[{}]-----------------------", map.get("doc_no"));
                //  删除文档文件，更新状态为已删除
                String file_root_path = root_path + map.get("file_root_path");
                FileUtil.deleteFolder(file_root_path);

                condMap.put("last_modi_time", CommonUtil.getCurrentTimeStr());
                condMap.put("doc_serial_no", map.get("doc_serial_no"));
                recycleStationDao.updateRecycleInfo(condMap);
            }
        }

        long entTime = System.currentTimeMillis();
        logger.info("---------------------[job:文档回收站清理]执行结束,耗时" + (entTime - startTime) + "毫秒------------------------");

    }
}
