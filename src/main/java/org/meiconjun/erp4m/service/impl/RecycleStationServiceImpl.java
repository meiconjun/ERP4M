package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.DocBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.RecycleStationDao;
import org.meiconjun.erp4m.service.RecycleStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 文档回收站业务逻辑类
 * @date 2020/7/6 22:08
 */
@Service("recycleStationService")
@Transactional
public class RecycleStationServiceImpl implements RecycleStationService {
    private Logger logger = LoggerFactory.getLogger(RecycleStationServiceImpl.class);

    @Resource
    private RecycleStationDao recycleStationDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("revertDoc".equals(operType)) {
            revertDocOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 还原文档至个人文档库
     * @param requestBean
     * @param responseBean
     */
    private void revertDocOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        List<Map<String, Object>> docList = (List<Map<String, Object>>) paramMap.get("doc_list");
        HashMap<String, Object> condMap = new HashMap<>();
        for (Map<String, Object> map : docList) {
            // 数据移入个人文档库
            condMap.put("doc_serial_no", map.get("doc_serial_no"));
            condMap.put("doc_no", map.get("doc_no"));
            condMap.put("doc_name", map.get("doc_name"));
            condMap.put("doc_language", map.get("doc_language"));
            condMap.put("secret_level", map.get("secret_level"));
            condMap.put("doc_type", map.get("doc_type"));
            condMap.put("doc_writer", map.get("doc_writer"));
            condMap.put("doc_desc", map.get("doc_desc"));
            condMap.put("create_user", map.get("create_user"));
            condMap.put("upload_user", map.get("upload_user"));
            condMap.put("upload_time", map.get("upload_time"));
            condMap.put("last_modi_user", map.get("last_modi_user"));
            condMap.put("last_modi_time", map.get("last_modi_time"));
            condMap.put("doc_version", map.get("doc_version"));
            condMap.put("file_root_path", map.get("file_root_path"));

            recycleStationDao.insertPersonalDocInfo(condMap);
            // 删除回收站数据
            recycleStationDao.deleteRecycleInfo((String) map.get("doc_serial_no"));
        }
    }

    /**
     * 查询个人文档回收站数据
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
        List<HashMap<String, Object>> list = recycleStationDao.selectPersonalRecycleDocInfo(condMap);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
