package org.meiconjun.erp4m.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.User;
import sun.misc.Request;

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
        /*RequestBean<User> requestBean = new RequestBean<User>();
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

        System.out.println(CommonUtil.formatJson(CommonUtil.objToJson(bean)));*/
        /*String aaa = "{\n" +
                "        'beanList' : [{\n" +
                "            \"user_no\" : user_no,\n" +
                "            'pass_word' : pass_word\n" +
                "        }],\n" +
                "        'operType' : 'login',\n" +
                "        'paramMap' : {}\n" +
                "    }";
        Gson gson = new Gson();
        RequestBean re = (RequestBean) gson.fromJson(aaa, new TypeToken<RequestBean<User>>(){}.getType());
        User u = (User) re.getBeanList().get(0);*/
        SerialNumberGenerater se = SerialNumberGenerater.getInstance();
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
    }
}
