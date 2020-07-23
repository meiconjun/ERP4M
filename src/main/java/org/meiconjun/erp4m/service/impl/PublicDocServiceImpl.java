package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.DocBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.PersonalDocDao;
import org.meiconjun.erp4m.dao.PublicDocDao;
import org.meiconjun.erp4m.service.PublicDocService;
import org.meiconjun.erp4m.util.CommonUtil;
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
    @Resource
    private PersonalDocDao personalDocDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("getDocHistory".equals(operType)) {
            getDocHistoryOperation(requestBean, responseBean);
        } else if ("checkOut".equals(operType)) {
            checkOutOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 文档检出
     * @param requestBean
     * @param responseBean
     */
    private void checkOutOperation(RequestBean requestBean, ResponseBean responseBean) {
        DocBean docBean = (DocBean)requestBean.getBeanList().get(0);
        // 更新版本号！
        String doc_version = docBean.getDoc_version();
        double doc_version_n = Double.valueOf(doc_version);
        doc_version_n = doc_version_n + 0.1;
        doc_version = String.valueOf(doc_version_n);

        docBean.setDoc_version(doc_version);
        HashMap<String, Object> condMap = new HashMap<>();
        condMap.put("doc_serial_no", docBean.getDoc_serial_no());
        condMap.put("doc_no", docBean.getDoc_no());
        condMap.put("doc_name", docBean.getDoc_name());
        condMap.put("doc_language", docBean.getDoc_language());
        condMap.put("doc_type", docBean.getDoc_type());
        condMap.put("doc_writer", docBean.getDoc_writer());
        condMap.put("doc_desc", docBean.getDoc_desc());
        condMap.put("upload_user", docBean.getUpload_user());
        condMap.put("upload_time", docBean.getUpload_time());
        condMap.put("last_modi_time", CommonUtil.getCurrentTimeStr());
        condMap.put("doc_version", doc_version);
        condMap.put("file_root_path", docBean.getFile_root_path());
        condMap.put("create_user", docBean.getCreate_user());
        condMap.put("last_modi_user", CommonUtil.getLoginUser().getUser_no());
        condMap.put("docBean", docBean);

        int effect = personalDocDao.insertPersonalDocInfo(condMap);
        if (effect > 0) {
            personalDocDao.insertDocVersionInfo(condMap);
            publicDocDao.deletePublicDocInfo(docBean.getDoc_serial_no());
            // 更新文档状态为未提交审阅,清空审阅者，裁决者
            condMap.put("review_state", "0");
            condMap.put("review_detail", "");
            personalDocDao.updateDocReviewInfo(condMap);
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } else {
            logger.error("文档序列号[{}]插入个人文档库表失败", docBean.getDoc_serial_no());
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("检出文档失败，文档移入个人文档库失败");
        }
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
