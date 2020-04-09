package org.meiconjun.erp4m.controller;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.base.BaseController;
import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.service.MenuService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lch
 * @Title: MenuController
 * @Package
 * @Description: 菜单操作控制器
 * @date 2020/4/9 21:02
 */
public class MenuController extends BaseController {

    @Resource(name = "menuService")
    private MenuService menuService;

    /**
     * 接收请求，直接调用父类的excuteRequest
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/menu.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String excute(HttpServletRequest request, HttpServletResponse response) {
        return super.excuteRequest(request, new TypeToken<RequestBean<MenuBean>>(){}.getClass());
    }

    /**
     * 具体处理逻辑,一般直接调用服务类的执行方法即可
     * @param request
     * @param requestBean
     * @return
     */
    @Override
    protected ResponseBean doAction(HttpServletRequest request, RequestBean requestBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = menuService.excute(requestBean);
        } catch (Exception e) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("调用后台服务异常");
            logger.error("调用后台服务异常:" + e.getMessage(), e);
        }
        return responseBean;
    }
}
