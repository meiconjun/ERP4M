package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.DocBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.PersonalDocDao;
import org.meiconjun.erp4m.service.PersonalDocService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
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
@Service("personalDocService")
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
        } else if ("add".equals(operType)){
            addOperation(requestBean, responseBean);
        } else if ("modify".equals(operType)) {
            modifyOperation(requestBean, responseBean);
        } else if ("delete".equals(operType)) {
            deleteOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 删除操作，将文档数据移到回收站表，保留90天
     * @param requestBean
     * @param responseBean
     */
    private void deleteOperation(RequestBean requestBean, ResponseBean responseBean) throws ParseException {
        List<DocBean> docBeanList = requestBean.getBeanList();
        String delete_time = CommonUtil.getCurrentTimeStr();
        String expire_time = CommonUtil.getTimeAfterDays(delete_time, 90);
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        for (DocBean docBean : docBeanList) {
            // 删除个人文档表数据
            personalDocDao.deletePersonalDocInfo(docBean.getDoc_serial_no());
            // 插入回收站表数据
            condMap.put("docBean", docBean);
            condMap.put("delete_time", delete_time);
            condMap.put("expire_time", expire_time);
            condMap.put("delete_state", "0");
            personalDocDao.insertRecycleInfo(condMap);
            logger.info("文档编号[" + docBean.getDoc_no() + "]移入回收站--------");
        }

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 修改操作
     * @param requestBean
     * @param responseBean
     */
    private void modifyOperation(RequestBean requestBean, ResponseBean responseBean) {
        DocBean docBean = (DocBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        String file_root_path = (String) paramMap.get("file_root_path");
        String doc_serial_no = docBean.getDoc_serial_no();
        String upload_user = CommonUtil.getLoginUser().getUser_no();
        String upload_time = CommonUtil.getCurrentTimeStr();
        String last_modi_time = upload_time;

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("docBean", docBean);
        condMap.put("file_root_path", file_root_path);
        condMap.put("doc_serial_no", doc_serial_no);
        condMap.put("upload_user", upload_user);
        condMap.put("upload_time", upload_time);
        condMap.put("last_modi_time", last_modi_time);

        personalDocDao.updatePersonalDocInfo(condMap);
        personalDocDao.insertDocVersionInfo(condMap);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
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

    /**
     * 新增文档
     * @param requestBean
     * @param responseBean
     */
    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        DocBean docBean = (DocBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        String file_root_path = (String) paramMap.get("file_root_path");
        String doc_serial_no = SerialNumberGenerater.getInstance().generaterNextNumber();
        String upload_user = CommonUtil.getLoginUser().getUser_no();
        String upload_time = CommonUtil.getCurrentTimeStr();
        String last_modi_time = upload_time;

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("docBean", docBean);
        condMap.put("file_root_path", file_root_path);
        condMap.put("doc_serial_no", doc_serial_no);
        condMap.put("upload_user", upload_user);
        condMap.put("upload_time", upload_time);
        condMap.put("last_modi_time", last_modi_time);

        personalDocDao.insertPersonalDocInfo(condMap);
        // 审批表新增数据
        personalDocDao.insertDocReviewInfo(condMap);
        // 新增版本信息
        personalDocDao.insertDocVersionInfo(condMap);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }
}
