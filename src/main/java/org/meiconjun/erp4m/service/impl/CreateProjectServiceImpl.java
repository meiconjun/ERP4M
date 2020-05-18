package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CreateProjectDao;
import org.meiconjun.erp4m.service.CreateProjectService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
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
        } else if ("createProject".equals(operType)) {
            createProjectOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 发起立项申请，创建项目信息，推送消息等
     * @param requestBean
     * @param responseBean
     */
    private void createProjectOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        //项目编号-流水号
        String project_no = SerialNumberGenerater.getInstance().generaterNextNumber();
        //项目名称-4码
        String project_name = (String) paramMap.get("project_name");
        //中文名称
        String chn_name = (String) paramMap.get("chn_name");
        // 负责人
        String principal = CommonUtil.getLoginUser().getUser_no();
        // 规格说明书文件存放路径
        String specifications = (String) paramMap.get("product_doc_path");
        // 项目开始日期
        String begin_date = (String) paramMap.get("begin_date");
        // 成员列表
        String project_menbers = (String) paramMap.get("member");
        // 项目状态 1-立项中
        String project_state = "1";

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("project_no", project_no);
        condMap.put("project_name", project_name);
        condMap.put("chn_name", chn_name);
        condMap.put("principal", principal);
        condMap.put("specifications", specifications);
        condMap.put("begin_date", begin_date);
        condMap.put("project_menbers", project_menbers);
        condMap.put("project_state", project_state);
        condMap.put("create_state", "1");
        int effect = createProjectDao.insertNewProjectMain(condMap);
        if (effect == 0) {
            throw new RuntimeException("新增项目主表信息失败");
        }

        List<Map<String, Object>> stageList = (List<Map<String, Object>>) paramMap.get("stageList");
        for (Map<String, Object> stageMap : stageList) {
            condMap = new HashMap<String, Object>();
            condMap.put("project_no", project_no);
            condMap.put("principal", stageMap.get("principal"));
            condMap.put("stage_num", stageMap.get("stageCount"));
            condMap.put("stage", stageMap.get("stage_type"));
            condMap.put("begin_date", stageMap.get("stage_begin"));
            condMap.put("end_date", stageMap.get("stage_end"));
            condMap.put("doc_serial", SerialNumberGenerater.getInstance().generaterNextNumber());
            condMap.put("is_end", "0");
            effect = createProjectDao.insertNewProjectStage(condMap);
            if (effect == 0) {
                logger.error("插入项目[" + project_name + "]阶段" + stageMap.get("stageCount") + "信息失败");
                throw new RuntimeException("新增项目阶段信息失败");
            }
        }
        // TODO 推送消息给项目成员进行会签

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
