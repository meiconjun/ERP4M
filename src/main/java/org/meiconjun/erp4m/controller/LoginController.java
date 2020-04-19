package org.meiconjun.erp4m.controller;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.base.BaseController;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Lch
 * @Title: LoginController
 * @Package org.meiconjun.controller
 * @Description: 登录控制器
 * @date 2020/4/5 17:06
 */
@Controller
public class LoginController extends BaseController {

    @Resource(name="loginService")
    private LoginService loginService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String excute(HttpServletRequest request, HttpServletResponse response) {
        return super.excuteRequest(request, new TypeToken<RequestBean<User>>(){}.getType());
    }

    @Override
    protected ResponseBean doAction(HttpServletRequest request, RequestBean requestBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = loginService.excute(requestBean);
            if (SystemContants.HANDLE_SUCCESS.equals(responseBean.getRetCode())) {
                HttpSession session = request.getSession();
                session.setAttribute("USER_SESSION", responseBean.getRetMap().get("user"));
                session.setMaxInactiveInterval(30*60);//30分钟超时
            }
        } catch (Exception e) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("调用后台服务异常");
            logger.error("调用后台服务异常:" + e.getMessage(), e);
        }
        return responseBean;
    }

}
