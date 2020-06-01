package org.meiconjun.erp4m.controller;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.base.BaseController;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/1 20:15
 */
@Controller
public class LogoutController extends BaseController {

    @RequestMapping(value = "/logout.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String excute(HttpServletRequest request, HttpServletResponse response) {
        return super.excuteRequest(request, new TypeToken<RequestBean<Map>>(){}.getType());
    }

    @Override
    protected ResponseBean doAction(HttpServletRequest request, RequestBean requestBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");
        logger.info("==============用户[" + user_no + "]登出================");
        ResponseBean responseBean = new ResponseBean();
        HttpSession session = request.getSession();
        session.removeAttribute("USER_SESSION");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        return responseBean;
    }
}
