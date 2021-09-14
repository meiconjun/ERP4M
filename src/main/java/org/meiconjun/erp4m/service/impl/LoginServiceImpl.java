package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.config.CustomConfigProperties;
import org.meiconjun.erp4m.dao.LoginDao;
import org.meiconjun.erp4m.service.LoginService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
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
    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    @Resource
    private LoginDao loginDao;
    @Resource
    private CustomConfigProperties customConfigProperties;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws IOException {
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
        private void loginOper(RequestBean requestBean, ResponseBean responseBean) throws IOException {
            User user = (User) requestBean.getBeanList().get(0);
            HashMap<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("user_no", user.getUser_no());
            condMap.put("pass_word", user.getPass_word());

            List<HashMap<String, Object>> userList = loginDao.selectUser(condMap);
            if (userList.isEmpty()) {
                logger.info("用户号或密码错误！");
                responseBean.setRetCode(SystemContants.HANDLE_FAIL);
                responseBean.setRetMsg("用户号或密码错误！");
            } else {
                logger.info("用户名密码正确！");
                HashMap<String, Object> userMap = userList.get(0);
                User user2 = new User();
                user2.setUser_no((String) userMap.get("user_no"));
                user2.setUser_name((String) userMap.get("user_name"));
                user2.setAuth_user((String) userMap.get("auth_user"));
                user2.setEmail((String) userMap.get("email"));
                user2.setPhone((String) userMap.get("phone"));
                String role_no = loginDao.selectRoleNo(user.getUser_no());
                user2.setRole_no(role_no);
//                user2.setPass_word((String) userMap.get("pass_word"));
                // 图片转Base64
                if (!CommonUtil.isStrBlank((String) userMap.get("picture"))) {
                    try {
                        String url = customConfigProperties.getFileSavePath() + (String) userMap.get("picture");
                        File file = new File(url);
                        String imgBase64 = CommonUtil.fileToBase64(file);
                        user2.setPicture(imgBase64);
                    } catch (IOException e) {
                        logger.error("载入头像异常," + e.getMessage(), e);
                    }

                }

                user2.setStatus((String) userMap.get("status"));

                Map retMap = new HashMap();
                //判断用户是否被停用
                if ("2".equals(user2.getStatus())) {
                    responseBean.setRetCode(SystemContants.HANDLE_FAIL);
                    responseBean.setRetMsg("该用户已被停用，请联系管理员！");
                } else {
                    // 判断是否是初始密码
                    String defaultPsw = customConfigProperties.getDefaultPassword();
                    if (defaultPsw.equals(user.getPass_word())) {
                        retMap.put("changePsw", true);
                    }
                    retMap.put("user", user2);
                    responseBean.setRetMap(retMap);
                    responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
                    responseBean.setRetMsg("登录成功！");
                }
            }
    }

}
