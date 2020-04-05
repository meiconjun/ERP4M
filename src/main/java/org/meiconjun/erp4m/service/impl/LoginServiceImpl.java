package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.LoginDao;
import org.meiconjun.erp4m.service.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lch
 * @Title: LoginServiceImpl
 * @Package
 * @Description: 登录处理业务实现类
 * @date 2020/4/5 20:23
        */
@Service("loginService")
@Transactional
public class LoginServiceImpl implements LoginService {
    @Resource
    private LoginDao loginDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) {
        ResponseBean responseBean = new ResponseBean();
        String operType = requestBean.getOperType();
        if ("login".equals(operType)) {
            loginOper(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 用户登录校验
     * @param requestBean
     * @param responseBean
     */
        private void loginOper(RequestBean requestBean, ResponseBean responseBean) {
            User user = (User) requestBean.getBeanList().get(0);
            HashMap<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("user_no", user.getUser_no());
            condMap.put("pass_word", user.getPass_word());

            List<HashMap<String, Object>> userList = loginDao.selectUser(condMap);
            if (userList.isEmpty()) {
                responseBean.setRetCode(SystemContants.HANDLE_FAIL);
                responseBean.setRetMsg("用户号或密码错误！");
            } else {
                HashMap<String, Object> userMap = userList.get(0);
                User user2 = new User();
                user2.setUser_no((String) userMap.get("USER_NO"));
                user2.setUser_name((String) userMap.get("USER_NAME"));
                user2.setAuth_user((String) userMap.get("AUTH_USER"));
                user2.setEmail((String) userMap.get("EMAIL"));
                user2.setPhone((String) userMap.get("PHONE"));
                user2.setPass_word((String) userMap.get("PASS_WORD"));
                user2.setPicture((String) userMap.get("PICTURE"));
                user2.setStatus((String) userMap.get("STATUS"));

                Map retMap = new HashMap();
                retMap.put("user", user2);
                responseBean.setRetMap(retMap);
                responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
                responseBean.setRetMsg("登录成功！");
            }

    }

}
