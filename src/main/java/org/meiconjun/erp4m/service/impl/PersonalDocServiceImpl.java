package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.DocBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.PersonalDocDao;
import org.meiconjun.erp4m.service.PersonalDocService;
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
 * @date 2020/6/11 21:20
 */
@Service
@Transactional
public class PersonalDocServiceImpl implements PersonalDocService {

    private Logger logger = LoggerFactory.getLogger(PersonalDocServiceImpl.class);

    @Resource
    private PersonalDocDao personalDocDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        }
        return responseBean;
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
        condMap.put("upload_user", bean.getUpload_user());
        condMap.put("doc_type", bean.getDoc_type());

        Page page = PageHelper.startPage(curPage, limit);
        List<DocBean> list = personalDocDao.selectPersonalDocInfo(condMap);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }


}
