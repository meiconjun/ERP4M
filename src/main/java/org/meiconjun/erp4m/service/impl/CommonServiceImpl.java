package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.MessageDao;
import org.meiconjun.erp4m.service.CommonService;
import org.meiconjun.erp4m.util.CommonUtil;
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
        }
        return responseBean;
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
