package org.meiconjun.erp4m.controller;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.base.BaseController;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.service.UserRightService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/10 20:25
 */
@Controller
public class UserRightController extends BaseController {

    @Resource(name = "userRightService")
    private UserRightService userRightService;

    @ResponseBody
    @RequestMapping(value = "/userRight.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String excute(HttpServletRequest request, HttpServletResponse response) {
        return super.excuteRequest(request, new TypeToken<RequestBean<User>>(){}.getType());
    }

    @Override
    protected ResponseBean doAction(HttpServletRequest request, RequestBean requestBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userRightService.excute(requestBean);
        } catch (Exception e) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg(e.getMessage());
            logger.error("调用后台服务异常：" + e.getMessage(), e);
        }
        return responseBean;
    }
}
