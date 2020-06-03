package org.meiconjun.erp4m.util;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import org.meiconjun.erp4m.bean.FieldBean;
import org.meiconjun.erp4m.bean.MessageBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.dao.CommonDao;
import org.meiconjun.erp4m.dao.MessageDao;
import org.meiconjun.erp4m.interceptor.RequestHolder;
import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;
import sun.plugin2.message.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 公共工具类
 *
 * @author meiconjun
 * @time 2019年4月7日上午1:02:03
 */
public class CommonUtil {

    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private static MessageDao messageDao;
    private static CommonDao commonDao;

    static {
        messageDao = SpringContextHolder.getBean("messageDao");
        commonDao = SpringContextHolder.getBean("commonDao");
    }
    /**
     * Json解析器
     */
    private static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    /**
     * 将Json字符串解析成对象,注意传递数字要使用字符串形式，否则转换类型会异常
     * 使用示例： Map mm = (Map) CommonUtil.jsonToObj(retStr, new TypeToken<Map<String, String>>(){}.getType());
     * @return
     */
    public static Object jsonToObj(String json, Type t) {
        return gson.fromJson(json, t);
    }

    /**
     * 将对象转为Json字符串
     *
     * @param obj
     * @return
     */
    public static String objToJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 格式化Json字符串
     *
     * @param str
     * @return
     */
    public static String formatJson(String str) {
        JsonParser jp = new JsonParser();
        JsonObject jo = jp.parse(str).getAsJsonObject();
        return gson.toJson(jo);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isStrBlank(String str) {
        if (null == str || "".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前毫秒时间戳
     *
     * @return
     */
    public static String getCurTimeStampMsec() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获取当前秒时间戳
     *
     * @return
     */
    public static String getCurTimeStampSec() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 对URL进行特殊字符编码
     *
     * @param url
     * @return
     */
    public static String urlEncoding(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "utf-8")
                .replace("\\+", "%20").replace("\\!", "%21").replace("\\'", "%27")
                .replace("\\(", "%28").replace("\\)", "%29").replace("\\~", "%7E");
    }

    /**
     * 对编码后的URL进行解码
     *
     * @param url
     * @return
     */
    public static String urlDecoding(String url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, "utf-8");
    }

    /**
     * 检测流的字符集
     *
     * @author meiconjun
     * @time 2019年4月7日上午1:02:57
     */
    public static String getStreamCharset(InputStream is) throws Exception {
        byte[] b = input2byte(is);
        ByteArrayInputStream bais = new ByteArrayInputStream(b);

        CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
        cdp.add(JChardetFacade.getInstance());// 依赖jar包 antlr.jar&chardet.jar
        cdp.add(ASCIIDetector.getInstance());
        cdp.add(UnicodeDetector.getInstance());
        cdp.add(new ParsingDetector(false));
        cdp.add(new ByteOrderMarkDetector());

        Charset charset = null;
        try {
            charset = cdp.detectCodepage(bais, 2147483647);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            bais.close();
        }
        return charset == null ? null : charset.name().toLowerCase();
    }

    public static InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static byte[] input2byte(InputStream inStream) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        byte[] in2b = null;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            in2b = swapStream.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        } finally {
            swapStream.close();
        }
        return in2b;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public static User getLoginUser() {
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("USER_SESSION");
        if (user == null) {
            logger.error("===================获取登录用户为空====================");
        }
        return user;
    }

    /**
     * 获取当前时间字符串 格式yyyyMMddHHmmss
     * @return
     */
    public static String getCurrentTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期字符串 格式yyyyMMdd
     * @return
     */
    public static String getCurrentDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    /**
     * 格式化日期字符串
     * @param date
     * @param targetFormat
     * @return
     * @throws ParseException
     */
    public static String formatDateString(String date, String orgFormat, String targetFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(orgFormat);
        Date d = sdf.parse(date);
        SimpleDateFormat sdf2 = new SimpleDateFormat(targetFormat);
        return  sdf2.format(d);
    }
    /**
     * 将文件转为Base64
     * @param file
     * @return
     */
    public static String fileToBase64(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        is.read(buffer);
        is.close();
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * 新增系统消息表数据
     * @return 消息编号
     */
    public static String addMessageAndSend(List<String> userList, List<String> roleList, MessageBean messageBean, String deal_type, String end_time) {
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        String receive_role = "";
        String receive_user = "";
        if (roleList != null) {
            for (String role_no: roleList) {
                receive_role += "," + role_no;
            }
        }
        if (!CommonUtil.isStrBlank(receive_role)) {
            receive_role = receive_role.substring(1);
        }
        if (userList != null) {
            for (String user_no: userList) {
                receive_user += "," + user_no;
            }
        }

        if (!CommonUtil.isStrBlank(receive_user)) {
            receive_user = receive_user.substring(1);
        }
        String msg_no = SerialNumberGenerater.getInstance().generaterNextNumber();
        if (CommonUtil.isStrBlank(messageBean.getIs_task())) {
            messageBean.setIs_task("0");
        }
        condMap.put("msg_no", msg_no);
        condMap.put("msg_type", messageBean.getMsg_type());
        condMap.put("create_user", messageBean.getCreate_user());
        condMap.put("create_time", messageBean.getCreate_time());
        condMap.put("receive_role", receive_role);
        condMap.put("receive_user", receive_user);
        condMap.put("read_user", "");
        condMap.put("msg_param", CommonUtil.objToJson(messageBean.getMsg_param()));
        condMap.put("msg_content", messageBean.getMsg_content());
        condMap.put("status", "");
        condMap.put("end_time", end_time);
        condMap.put("deal_type", deal_type);
        condMap.put("is_task", messageBean.getIs_task());
        messageDao.insertNewMessage(condMap);

        return msg_no;
    }

    /**
     * 获取当前日期N日后的日期或N日前的日期，正数为往后，复数为往前
     * @param afterNum
     * @return
     */
    public static String getCurrentDateAfterDays(int afterNum) {
        Date dNow = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dNow);
        calendar.add(Calendar.DATE, afterNum);
        dNow = calendar.getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String defaultDate = sdf.format(dNow);
        return defaultDate;
    }

    /**
     * 获取传入天数afterNum天后日期
     * @param date
     * @param afterNum
     * @return
     */
    public static String getDateAfterDays(String date, int afterNum) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Date thisDate = sd.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(thisDate);
        calendar.add(Calendar.DATE, afterNum);
        thisDate = calendar.getTime();
        return sd.format(thisDate);
    }
    /**
     * 更新消息的处理状态，更新成功返回空，更新失败返回错误信息
     * @param msg_no
     * @return
     */
    public static String updateMsgStatus(String msg_no) {
        String user_no = CommonUtil.getLoginUser().getUser_no();
        String status = messageDao.selectMessageStatus(msg_no);
        if ("1".equals(status)) {
            return "该事项已被其他用户处理！";
        }
        // 更新已阅用户列表，处理状态
        HashMap<String, Object> msgUserMap = messageDao.selectMessageInfo(msg_no);
        String readUser = (String) msgUserMap.get("read_user");//已阅用户
        if (!CommonUtil.isStrBlank(readUser)) {
        	readUser += "," + user_no;
        } else {
        	readUser = user_no;
        }
        //单人处理形式时，直接结束事项
        String deal_type = (String) msgUserMap.get("deal_type");//已阅用户
        if ("1".equals(deal_type)) {
        	status = "1";//已处理
        } else {
        	// 比对已阅用户和目标用户
        	String receive_user = (String) msgUserMap.get("receive_user");
        	if (readUser.split(",").length >= receive_user.split(",").length) {
        		status = "1";//已处理
        	}
        }
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("read_user", readUser);
        condMap.put("status", status);
        condMap.put("msg_no", msg_no);
        messageDao.updateReadUserAndStatus(condMap);
        return "";
    }
    /**
     * 给字符串拼接子字符串，以'splitStr'为分隔符分隔
     */
    public static String addStringSplitByStr(String target, String str, String splitStr) {
    	String retStr = "";
    	if (CommonUtil.isStrBlank(target)) {
    		retStr = str;
    	} else {
    		retStr = target + splitStr + str;
    	}
    	return retStr;
    }

    /**
     * 获取数据字典值对应的中文名称
     * @param parent_field
     * @param field_value
     * @return
     */
    public static String getFieldName(String parent_field, String field_value) {
        String retValue = commonDao.selectFieldName(field_value, parent_field);
        return retValue;
    }

    /**
     * 计算两个日期之间间隔天数
     * @param begin_date
     * @param end_date
     * @return
     */
    public static long getBetweenDays(String begin_date, String end_date) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = sd.parse(begin_date);
        Date endDate = sd.parse(end_date);

        return (endDate.getTime() - beginDate.getTime()) / (1000*60*60*24);
    }

    /**
     * 根据数据字典码获取其子节点列表
     * @param parent_field
     * @return
     */
    public static List<FieldBean> getFieldChildList(String parent_field) {
        List<FieldBean> fieldList = commonDao.selectChildFieldList(parent_field);
        return fieldList;
    }
}
