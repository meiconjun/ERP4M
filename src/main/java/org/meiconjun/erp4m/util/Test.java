package org.meiconjun.erp4m.util;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/518:43
 */
public class Test {

    public static void main(String[] args) {
        RequestBean<User> requestBean = new RequestBean<User>();
        User m = new User();
        m.setUser_no("AaaAss");
        Map m2 = new HashMap();
        m2.put("a", "12323213321421444");
        List<User> l = new ArrayList<User>();
        l.add(m);
        requestBean.setBeanList(l);
        requestBean.setOperType("aaaaa");
        requestBean.setParamMap(m2);

        String json = CommonUtil.objToJson(requestBean);

        RequestBean bean = (RequestBean) CommonUtil.jsonToObj(json, RequestBean.class);

        System.out.println(CommonUtil.formatJson(CommonUtil.objToJson(bean)));
    }
}
