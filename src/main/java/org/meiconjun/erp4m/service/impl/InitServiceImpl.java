package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.config.CustomConfigProperties;
import org.meiconjun.erp4m.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 初始化相关操作
 * @date 2020/5/10 13:44
 */
@Service("initService")
@Transactional
public class InitServiceImpl implements InitService {
    private Logger logger = LoggerFactory.getLogger(InitServiceImpl.class);
    @Resource
    private CustomConfigProperties customConfigProperties;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("initFilePath".equals(operType)) {
            initFilePath(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 获取文件存储根目录
     * @param requestBean
     * @param responseBean
     */
    private void initFilePath(RequestBean requestBean, ResponseBean responseBean) {
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        String filePath = customConfigProperties.getFileSavePath();

        retMap.put("filePath", filePath);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }
}
