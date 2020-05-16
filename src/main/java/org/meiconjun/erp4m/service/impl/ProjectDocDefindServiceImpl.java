package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.ProjectDocBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.ProjectDocDefindDao;
import org.meiconjun.erp4m.service.ProjectDocDefindService;
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
 * @Description: 项目文档定义服务类
 * @date 2020/5/11 22:17
 */
@Service("projectDocDefindService")
@Transactional
public class ProjectDocDefindServiceImpl implements ProjectDocDefindService {
    private Logger logger = LoggerFactory.getLogger(ProjectDocDefindServiceImpl.class);

    @Resource
    private ProjectDocDefindDao projectDocDefindDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("getDocTree".equals(operType)) {
            getDocTreeOperation(requestBean, responseBean);
        } else if ("add".equals(operType)) {
            addOperation(requestBean, responseBean);
        } else if ("modify".equals(operType)) {
            modifyOperation(requestBean, responseBean);
        } else if ("delete".equals(operType)) {
            deleteOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    private void deleteOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String doc_no = (String) paramMap.get("doc_no");
        projectDocDefindDao.deleteDoc(doc_no);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    private void modifyOperation(RequestBean requestBean, ResponseBean responseBean) {
        ProjectDocBean bean = (ProjectDocBean) requestBean.getBeanList().get(0);
        int effect = projectDocDefindDao.updateDoc(bean);
        if (effect == 0) {
            throw new RuntimeException("修改项目文档信息失败");
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        ProjectDocBean bean = (ProjectDocBean) requestBean.getBeanList().get(0);
        int effect = projectDocDefindDao.insertDoc(bean);
        if (effect == 0) {
            throw new RuntimeException("新增项目文档信息失败");
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询项目文档并构造树结构
     * @param requestBean
     * @param responseBean
     */
    private void getDocTreeOperation(RequestBean requestBean, ResponseBean responseBean) {
        List<ProjectDocBean> beanList = projectDocDefindDao.selectProjectDoc();

        List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> yuyanTree = new HashMap<String, Object>();
        yuyanTree.put("name", "预研阶段");
        yuyanTree.put("type", "1");
        yuyanTree.put("open", true);
        yuyanTree.put("children", new ArrayList());
        HashMap<String, Object> yanfaTree = new HashMap<String, Object>();
        yanfaTree.put("name", "研发阶段");
        yanfaTree.put("type", "2");
        yanfaTree.put("open", false);
        yanfaTree.put("children", new ArrayList());
        HashMap<String, Object> liangchanTree = new HashMap<String, Object>();
        liangchanTree.put("name", "量产阶段");
        liangchanTree.put("type", "3");
        liangchanTree.put("open", false);
        liangchanTree.put("children", new ArrayList());

        for (ProjectDocBean bean : beanList) {
            HashMap<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("name", bean.getDoc_name());
            tempMap.put("doc_no", bean.getDoc_no());
            tempMap.put("secret_level", bean.getSecret_level());
            tempMap.put("stage", bean.getStage());
            tempMap.put("department", bean.getDepartment());
            tempMap.put("description", bean.getDescription());
            if ("1".equals(bean.getStage())) {
                List tempList = (List) yuyanTree.get("children");
                tempList.add(tempMap);
                yuyanTree.put("children", tempList);
            } else if ("2".equals(bean.getStage())) {
                List tempList = (List) yanfaTree.get("children");
                tempList.add(tempMap);
                yanfaTree.put("children", tempList);
            } else if ("3".equals(bean.getStage())) {
                List tempList = (List) liangchanTree.get("children");
                tempList.add(tempMap);
                liangchanTree.put("children", tempList);
            }
        }

        dataList.add(yuyanTree);
        dataList.add(yanfaTree);
        dataList.add(liangchanTree);

        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("dataList", dataList);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }
}
