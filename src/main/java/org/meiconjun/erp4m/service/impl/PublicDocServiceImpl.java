package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.DocBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.PublicDocDao;
import org.meiconjun.erp4m.service.PublicDocService;
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
 * @Description:
 * @date 2020/7/16 22:08
 */
@Service("publicDocService")
@Transactional
public class PublicDocServiceImpl implements PublicDocService {
    private Logger logger = LoggerFactory.getLogger(PublicDocServiceImpl.class);

    @Resource
    private PublicDocDao publicDocDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("getDocHistory".equals(operType)) {
            getDocHistoryOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 查询版本历史
     * @param requestBean
     * @param responseBean
     */
    private void getDocHistoryOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String doc_serial_no = (String) paramMap.get("doc_serial_no");

        List<HashMap<String, Object>> docList = publicDocDao.selectDocHistory(doc_serial_no);

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("docList", docList);
        retMap.put("total", docList.size());
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
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
        condMap.put("create_user", bean.getCreate_user());
        condMap.put("doc_type", bean.getDoc_type());

        Page page = PageHelper.startPage(curPage, limit);
        List<DocBean> list = publicDocDao.selectPublicDocInfo(condMap);

        long total = page.getTotal();
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
