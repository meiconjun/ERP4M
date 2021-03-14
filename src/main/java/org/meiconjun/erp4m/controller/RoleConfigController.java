package org.meiconjun.erp4m.controller;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.base.BaseController;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.RoleBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.service.RoleConfigService;
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
 * @Description: 角色管理Controller
 * @date 2020/4/27 20:48
 */
@Controller
public class RoleConfigController extends BaseController {

    @Resource(name = "roleConfigService")
    private RoleConfigService roleConfigService;

    @ResponseBody
    @RequestMapping(value = "/roleConfig.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String excute(HttpServletRequest request, HttpServletResponse response) {
        return super.excuteRequest(request, new TypeToken<RequestBean<RoleBean>>(){}.getType());
    }

    @Override
    protected ResponseBean doAction(HttpServletRequest request, RequestBean requestBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = roleConfigService.excute(requestBean);
        } catch (Exception e) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg(e.getMessage());
            errorLogger.error("调用后台服务异常：" + e.getMessage(), e);
        }
        return responseBean;
    }
}
