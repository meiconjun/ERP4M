package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CreateProjectDao;
import org.meiconjun.erp4m.dao.MessageDao;
import org.meiconjun.erp4m.service.CreateProjectService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
import org.meiconjun.erp4m.util.WebsocketMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

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
    @Resource
    private MessageDao messageDao;

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
        } else if ("countersign".equals(operType)) {
            countersignOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 会签
     * @param requestBean
     * @param responseBean
     */
    private void countersignOperation(RequestBean requestBean, ResponseBean responseBean) {
        String user_no = CommonUtil.getLoginUser().getUser_no();
        Map<String, Object> paramMap = requestBean.getParamMap();
        String state = (String) paramMap.get("state");//1-同意 2-拒绝
        String msg_no = (String) paramMap.get("msg_no");// 消息编号
        String status = "0";//处理状态
        String project_no = (String) paramMap.get("project_no");//项目编号
        // 更新消息表已阅用户和处理状态
        HashMap<String, Object> msgUserMap = messageDao.selectReadAndUnReadUser(msg_no);
        String readUser = (String) msgUserMap.get("read_user");
        if (!CommonUtil.isStrBlank(readUser)) {
            readUser += "," + user_no;
        } else {
            readUser = user_no;
        }
        List<String> readUsers = Arrays.asList(readUser.split(","));
        List<String> receiveUsers = Arrays.asList(((String) msgUserMap.get("receive_user")).split(","));
        if (readUsers.size() >= receiveUsers.size()) {
            status = "1";
        }
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("read_user", readUser);
        condMap.put("status", status);
        messageDao.updateReadUserAndStatus(condMap);
        // 更新项目主表信息
        String create_state = "1";//会签中
        String project_state = "1";//立项中
        HashMap<String, Object> projectInfo = createProjectDao.selectProjectMain(project_no);
        String countersign_y = (String) projectInfo.get("countersign_y");//同意人员列表
        String countersign_n = (String) projectInfo.get("countersign_n");//拒绝人员列表
        String project_menbers = (String) projectInfo.get("project_menbers");//项目成员
        if ("2".equals(state)) {
            create_state = "3";//立项结束
            project_state = "2";//立项失败
            if (CommonUtil.isStrBlank(countersign_n)) {
                countersign_n = user_no;
            } else {
                countersign_n += "," + user_no;
            }
            //TODO 推送消息给立项人，立项失败

        } else {
            if (CommonUtil.isStrBlank(countersign_y)) {
                countersign_y = user_no;
            } else {
                countersign_y += "," + user_no;
            }
            if (countersign_y.split(",").length >= project_menbers.split(",").length) {
                create_state = "2";//老板审核
                //TODO 推送给老板审核
            }
        }
        HashMap<String, Object> condMap2 = new HashMap<String, Object>();
        condMap2.put("countersign_y", countersign_y);
        condMap2.put("countersign_n", countersign_n);
        condMap2.put("create_state", create_state);
        condMap2.put("project_state", project_state);
        createProjectDao.updateProjectMain(condMap2);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
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
        // 推送消息给项目成员进行会签
        MessageBean messageBean = new MessageBean();
        messageBean.setCreate_time(CommonUtil.getCurrentTimeStr());
        messageBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
        messageBean.setMsg_content("项目[" + project_name + "]正在发起立项");
        messageBean.setMsg_type(SystemContants.FIELD_MSG_TYPE_COUNTERSIGN);
        Map<String, Object> msg_param = new HashMap<String, Object>();
        msg_param.put("project_no", project_no);
        msg_param.put("project_name", project_name);
        msg_param.put("chn_name", chn_name);
        msg_param.put("begin_date", begin_date);
        msg_param.put("file_path", specifications);
        messageBean.setMsg_param(msg_param);
        List<String> userList = Arrays.asList((project_menbers.split(",")));
        String deal_type = "2";
        String end_time = CommonUtil.getCurrentDateAfterDays(7);

        String msg_no = CommonUtil.addMessageAndSend(userList, null, messageBean, deal_type, end_time);
        messageBean.setMsg_no(msg_no);
        WebsocketMsgUtil.sendMsgToMultipleUser(userList, null, messageBean);

        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
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
