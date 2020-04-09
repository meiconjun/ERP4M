package org.meiconjun.erp4m.base;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

/**
 * @author Lch
 * @Title: BaseController
 * @Package org.meiconjun.erp4m
 * @Description: controller超类, 定义前置后置操作
 * @date 2020/4/518:05
        */
public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     *
     * @param request
     * @param paramClass
     * @return
     */
    protected String excuteRequest (HttpServletRequest request, Type paramClass) {
        String retStr = "";
        RequestBean requestBean;
        ResponseBean responseBean;
        requestBean = beforeAction(request, paramClass);
        responseBean = doAction(request, requestBean);
        retStr = afterAction(responseBean);
        return retStr;
    }

    /**
     * 前置动作，打印报文等
     * @param request
     * @param paramClass
     */
    protected  RequestBean beforeAction (HttpServletRequest request, Type paramClass) {

        String reqJson = request.getParameter("message");
        // 打印请求报文
        logger.info("请求报文:\n" + CommonUtil.formatJson(reqJson));
        // 将json报文转为requestBean
//        return (RequestBean) CommonUtil.jsonToObj(reqJson, RequestBean.class);
        return (RequestBean) CommonUtil.jsonToObj(reqJson, paramClass);
    }

    /**
     * 实际分发逻辑，子类实现
     * @param request
     * @param requestBean
     */
    protected abstract ResponseBean doAction (HttpServletRequest request, RequestBean requestBean);

    /**
     * 后置操作，打印报文等
     * @param responseBean
     * @return
     */
    protected String afterAction (ResponseBean responseBean) {
        String retStr = CommonUtil.objToJson(responseBean);
        logger.info("返回报文:\n" + retStr);
        return retStr;
    }
}
