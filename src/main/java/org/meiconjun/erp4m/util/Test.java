package org.meiconjun.erp4m.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.Workbook;
import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.User;
import sun.misc.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/518:43
 */
public class Test {

    public static long getBetweenDays(String begin_date, String end_date) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = sd.parse(begin_date);
        Date endDate = sd.parse(end_date);

        return (endDate.getTime() - beginDate.getTime()) / (1000*60*60*24);
    }
    public static String formatDateString(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date d = sdf.parse(date);
        SimpleDateFormat sdf2 = new SimpleDateFormat(format);
        return  sdf2.format(d);
    }

    public static void poiTest() throws IOException {
        List<String> headList = new ArrayList<>();
        headList.add("用户号");
        headList.add("用户名");
        headList.add("部门");
        List<String[]> dataList = new ArrayList<>();
        dataList.add(new String[]{"00001", "张三", "主板设计部"});
        dataList.add(new String[]{"00002", "李四", "测试部"});

        Workbook workbook = ExcelUtil.exportExcel(headList, dataList);

        File file = new File("D:\\testExcel.xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        workbook.close();
    }
    public static void main(String[] args) throws ParseException, IOException {
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
        /*SerialNumberGenerater se = SerialNumberGenerater.getInstance();
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());
        System.out.println(se.generaterNextNumber());*/
        /*List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");s
        System.out.println(list.contains("aa"));*/
//        String date = "20200531";
//        System.out.println(formatDateString(date, "yyyy-MM-dd HH:mm:ss"));
        poiTest();
    }
}
