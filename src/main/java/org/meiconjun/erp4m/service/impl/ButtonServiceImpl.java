package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.ButtonBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.ButtonDao;
import org.meiconjun.erp4m.service.ButtonService;
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
 * @Description: 按钮相关 服务类
 * @date 2020/4/14 21:33
 */
@Service("buttonService")
@Transactional
public class ButtonServiceImpl implements ButtonService {
    private Logger logger = LoggerFactory.getLogger(ButtonServiceImpl.class);

    @Resource
    private ButtonDao buttonDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("getUserButton".equals(operType)) {
            getUserButtons(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 获取按钮列表
     * @param requestBean
     * @param responseBean
     * @return
     */
    private void getUserButtons(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, String> paramMap = requestBean.getParamMap();
        String menu_id = paramMap.get("menu_id");
        String user_no = CommonUtil.getLoginUser().getUser_no();

        List<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("user_no", user_no);
        condMap.put("menu_id", menu_id);
        List<ButtonBean> roleButtonList = buttonDao.selectRoleAuthButton(condMap);
        List<ButtonBean> userButtonList = buttonDao.selectUserAuthButton(condMap);
        String idStr = "";
        // 合并角色和用户权限按钮
        for (ButtonBean b : roleButtonList) {
            HashMap<String, String> buttonMap = new HashMap<String, String>();
            buttonMap.put("button_id", b.getButton_id());
            buttonMap.put("button_name", b.getButton_name());
            buttonMap.put("button_type", b.getButton_type());
            retList.add(buttonMap);
            idStr += "," + b.getButton_id();
        }
        for (ButtonBean b : userButtonList) {
            if (idStr.contains(b.getButton_id())) {
                HashMap<String, String> buttonMap = new HashMap<String, String>();
                buttonMap.put("button_id", b.getButton_id());
                buttonMap.put("button_name", b.getButton_name());
                buttonMap.put("button_type", b.getButton_type());
                retList.add(buttonMap);
            }
        }
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("buttonList", retList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMsg("获取按钮权限成功");
        responseBean.setRetMap(retMap);
    }
}
