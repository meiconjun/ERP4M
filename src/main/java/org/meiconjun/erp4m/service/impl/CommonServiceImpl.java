package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.TaskBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.MessageDao;
import org.meiconjun.erp4m.dao.TaskDao;
import org.meiconjun.erp4m.service.CommonService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.JedisUtil;
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
 * @date 2020/5/22 21:01
 */
@Service("commonService")
@Transactional
public class CommonServiceImpl implements CommonService {
    private Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Resource
    private CommonDao commonDao;
    @Resource
    private MessageDao messageDao;
    @Resource
    private TaskDao taskDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();

        if ("updateMessage".equals(operType)) {
            updateMessageOperation(requestBean, responseBean);
        } else if ("initUnReadMessage".equals(operType)) {
            initUnReadMessageOperation(requestBean, responseBean);
        } else if ("getAllUserNoAndName".equals(operType)) {
            getAllUserNoAndNameOperation(requestBean, responseBean);
        } else if ("initReadMessage".equals(operType)) {
            initReadMessageOperation(requestBean, responseBean);
        } else if ("getRoleList".equals(operType)) {
            getRoleListOperation(requestBean, responseBean);
        } else if ("updateTask".equals(operType)) {
            updateTaskStateOperation(requestBean, responseBean);
        } else if ("initTodoTask".equals(operType)) {
            getTodoTaskOperation(requestBean, responseBean);
        } else if ("initDoneTask".equals(operType)) {
            getDoneTaskOperation(requestBean, responseBean);
        } else if ("getProjectList".equals(operType)) {
            getProjectListOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 获取项目列表
     * @param requestBean
     * @param responseBean
     */
    private void getProjectListOperation(RequestBean requestBean, ResponseBean responseBean) {
        List<HashMap<String, String>> projectList = null;
        boolean flag = false;
        boolean redisFlag = JedisUtil.isRedisEnable();
        if (redisFlag) {
            if (JedisUtil.isContainsKey("projectList")) {
                String result = JedisUtil.getStrValue("projectList");
                if (!CommonUtil.isStrBlank(result)) {
                    logger.info("成功获取[projectList]Redis缓存");
                    flag = true;
                    projectList = (List)CommonUtil.jsonToObj(result, List.class);
                }
            }
        }
        if (!flag){
            projectList = commonDao.selectProjectList();
            if (redisFlag) {
                logger.info("缓存中没有projectList或数据为空，重新进行查询");
                JedisUtil.setStringValue("projectList", CommonUtil.objToJson(projectList), 360);
            }
        }
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("list", projectList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 获取已办任务列表
     * @param requestBean
     * @param responseBean
     */
    private void getDoneTaskOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");
        String role_no = (String) paramMap.get("role_no");

        List<TaskBean> taskList = taskDao.selectDoneTask(user_no, role_no);

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("taskList", taskList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 获取代办任务列表
     * @param requestBean
     * @param responseBean
     */
    private void getTodoTaskOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");
        String role_no = (String) paramMap.get("role_no");

        List<TaskBean> taskList = taskDao.selectTodoTask(user_no, role_no);

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("taskList", taskList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 更新任务处理状态
     * @param requestBean
     * @param responseBean
     */
    private void updateTaskStateOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap =requestBean.getParamMap();
        String task_no = (String) paramMap.get("task_no");
        String user_no = (String) paramMap.get("user_no");

        String retStr = CommonUtil.updateTaskStatus(task_no, user_no);
        if (!CommonUtil.isStrBlank(retStr)) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg(retStr);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        }
    }

    /**
     * 获取系统角色列表
     * @param requestBean
     * @param responseBean
     */
    private void getRoleListOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String position = (String) paramMap.get("position");
        String level = (String) paramMap.get("level");
        String department = (String) paramMap.get("department");

        List<HashMap<String, String>> roleList = commonDao.selectRoleList(position, level, department);

        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("roleList", roleList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 查询已阅消息
     * @param requestBean
     * @param responseBean
     */
    private void initReadMessageOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");
        String role_no = (String) paramMap.get("role_no");
        List<MessageBean> msgBeanList = new ArrayList<MessageBean>();
        List<HashMap<String, Object>> msgList = messageDao.selectReadMsg(user_no, role_no);
        for (HashMap<String, Object> msgMap : msgList) {
            MessageBean msgBean = new MessageBean();
            msgBean.setMsg_no((String) msgMap.get("msg_no"));
            msgBean.setMsg_type((String) msgMap.get("msg_type"));
            msgBean.setMsg_content((String) msgMap.get("msg_content"));
            msgBean.setCreate_user((String) msgMap.get("create_user"));
            msgBean.setCreate_time((String) msgMap.get("create_time"));
            msgBean.setMsg_param((Map)CommonUtil.jsonToObj((String) msgMap.get("msg_param"), Map.class));
            msgBeanList.add(msgBean);
        }
        retMap.put("msgBeanList", msgBeanList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 获取系统所有用户的 用户号：用户名  集合
     * @param requestBean
     * @param responseBean
     */
    private void getAllUserNoAndNameOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        HashMap<String, String> allUser = new HashMap<String, String>();
        List<HashMap<String, String>> selectUser = commonDao.selectAllUserNoAndName();
        for (HashMap<String, String> userMap : selectUser) {
            allUser.put(userMap.get("user_no"), userMap.get("user_name"));
        }
        retMap.put("allUser", allUser);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 获取用户未读消息
     * @param requestBean
     * @param responseBean
     */
    private void initUnReadMessageOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");
        String role_no = (String) paramMap.get("role_no");
        List<MessageBean> msgBeanList = new ArrayList<MessageBean>();
        List<HashMap<String, Object>> msgList = messageDao.selectUnReadMsg(user_no, role_no);
        for (HashMap<String, Object> msgMap : msgList) {
            MessageBean msgBean = new MessageBean();
            msgBean.setMsg_no((String) msgMap.get("msg_no"));
            msgBean.setMsg_type((String) msgMap.get("msg_type"));
            msgBean.setMsg_content((String) msgMap.get("msg_content"));
            msgBean.setCreate_user((String) msgMap.get("create_user"));
            msgBean.setCreate_time((String) msgMap.get("create_time"));
            msgBean.setMsg_param((Map)CommonUtil.jsonToObj((String) msgMap.get("msg_param"), Map.class));
            msgBeanList.add(msgBean);
        }
        retMap.put("msgBeanList", msgBeanList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }


    /**
     * 更新消息阅读状态
     * @param requestBean
     * @param responseBean
     */
    private void updateMessageOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String msg_no = (String) paramMap.get("msg_no");
        String retStr = CommonUtil.updateMsgStatus(msg_no);
        if (!CommonUtil.isStrBlank(retStr)) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg(retStr);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        }
    }
}
