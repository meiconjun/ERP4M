package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CreateProjectDao;
import org.meiconjun.erp4m.service.CreateProjectService;
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
 * @date 2020/5/17 15:49
 */
@Service("createProjectService")
@Transactional
public class CreateProjectServiceImpl implements CreateProjectService {
    private Logger logger = LoggerFactory.getLogger(CreateProjectServiceImpl.class);

    @Resource
    private CreateProjectDao createProjectDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("getProjectDoc".equals(operType)) {
            getProjectDocOperation(requestBean, responseBean);
        } else if ("getUserList".equals(operType)) {
            getUserListOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 根据文档负责部门获取用户列表
     * @param requestBean
     * @param responseBean
     */
    private void getUserListOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String docNo = (String) paramMap.get("docNo");// 文档编号

        // 查询文档的负责部门
        String department = createProjectDao.selectDocDepartment(docNo);
        // 根据部门获取用户列表
        List<HashMap<String, Object>> userList = createProjectDao.selectUserByDepartment(department);

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("userList", userList);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 获取项目文档列表
     * @param requestBean
     * @param responseBean
     */
    private void getProjectDocOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String stage = (String) paramMap.get("stage");// 阶段

        List<HashMap<String, Object>> docList = createProjectDao.selectProjectByStage(stage);

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("docList", docList);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }
}
