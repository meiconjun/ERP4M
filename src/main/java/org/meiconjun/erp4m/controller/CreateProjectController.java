package org.meiconjun.erp4m.controller;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.base.BaseController;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.service.CreateProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/17 16:04
 */
@Controller
public class CreateProjectController extends BaseController {
    @Resource(name = "createProjectService")
    private CreateProjectService createProjectService;

    @ResponseBody
    @RequestMapping(value = "createProject.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        return super.excuteRequest(request, new TypeToken<RequestBean<Map>>(){}.getType());
    }

    @Override
    protected ResponseBean doAction(HttpServletRequest request, RequestBean requestBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = createProjectService.excute(requestBean);
        } catch (Exception e) {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg(e.getMessage());
            logger.error("调用后台服务异常：" + e.getMessage(), e);
        }
        return responseBean;
    }
}
