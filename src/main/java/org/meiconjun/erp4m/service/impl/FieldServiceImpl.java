package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.FieldBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.FieldDao;
import org.meiconjun.erp4m.service.FieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/22 21:09
 */
@Service("fieldService")
@Transactional
public class FieldServiceImpl implements FieldService {
    private Logger logger = LoggerFactory.getLogger(FieldServiceImpl.class);

    @Resource
    private FieldDao fieldDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("initFields".equals(operType)) {
            initFields(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 获取所有数据字典,用于初始化
     * @param requestBean
     * @param responseBean
     */
    private void initFields(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> retMap = new HashMap();
        try {
            List<FieldBean> fieldList = fieldDao.selectAllFields();
            Map<String, ArrayList<HashMap<String, String >>> parentMap = new HashMap<String, ArrayList<HashMap<String, String >>>();// 父节点编号-子节点列表
            for (FieldBean bean : fieldList) {
                if ("1".equals(bean.getIs_parent())) {
                    parentMap.put(bean.getField_no(), new ArrayList<HashMap<String, String >>());
                }
            }
            for (FieldBean bean : fieldList) {
                if (!"1".equals(bean.getIs_parent())) {
                    HashMap<String, String> tempMap = new HashMap<String, String>();
                    tempMap.put("value", bean.getField_value());
                    tempMap.put("name", bean.getField_name());
                    ArrayList<HashMap<String, String >> tempList = parentMap.get(bean.getParent_field());
                    tempList.add(tempMap);
                    parentMap.put(bean.getParent_field(), tempList);
                }
            }
            retMap.put("fieldMap", "parentMap");
            responseBean.setRetMsg("获取数据字典成功");
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg(e.getMessage());
        }
    }
}
