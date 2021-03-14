package org.meiconjun.erp4m.util;

import cn.hutool.Hutool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.lingala.zip4j.exception.ZipException;
import org.apache.poi.ss.usermodel.Workbook;
import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.job.WuneriguiDailyMail;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Request;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    private static Logger logger = LoggerFactory.getLogger(Test.class);
    private static String mailFrom = "baoshiki@vip.qq.com";// 指明邮件的发件人
    private static String password_mailFrom = "fsehrqwsewmtbbcc";// 指明邮件的发件人登陆密码
    //    private static String mailTo = "baoshiki@vip.qq.com"; // 指明邮件的收件人
    private static String mail_host = "smtp.qq.com"; // 邮件的服务器域名
    private static String[] mailToArr = {"baoshiki@vip.qq.com", "260535105@qq.com"};
    private static String basePath = "D:\\";
    private static String curDate = "";

    private Stack<Integer> pushStack = new Stack();
    private Stack<Integer> popStack = new Stack();

    public static void main(String[] args) throws ParseException, IOException, ZipException, JobExecutionException {
//        File file = new File("D:\\20210228");
//        ZipUtil.fileFolderToZip(file, "D:\\20210228.zip", 47185920);
//        mailTest();
        System.out.println(DateUtil.format(DateUtil.yesterday(), "yyyyMMdd"));
    }


    private static void mailTest() {
        curDate = "20210228";
        File todayFile = new File(basePath + curDate);

        if (todayFile.exists()) {
            String zipFile = basePath + curDate + ".zip";
            try {
                ZipUtil.fileFolderToZip(todayFile, zipFile, 47185920);

                // 发送邮件
                Properties prop = new Properties();
                prop.setProperty("mail.host", mail_host);// 需要修改
                prop.setProperty("mail.transport.protocol", "smtp");
                prop.setProperty("mail.smtp.auth", "true");

                File files = new File(basePath);
                String[] names = files.list();
                int partCount = 0;
                for (int i = 1; i <= names.length; i++) {
                    if (names[i].startsWith(curDate + ".z")) {
                        partCount++;
                        // 使用JavaMail发送邮件的5个步骤
                        // 1、创建session
                        Session session = Session.getInstance(prop);
                        // 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
                        session.setDebug(true);
                        // 2、通过session得到transport对象
                        Transport ts = session.getTransport();
                        // 3、连上邮件服务器，需要发件人提供邮箱的用户名和密码进行验证
                        ts.connect(mail_host, mailFrom, password_mailFrom);// 需要修改
                        // 4、创建邮件
                        Message message = createAttachMail(session, "无内鬼日报-" + curDate + "-part" + partCount, basePath + names[i]);
                        // 5、发送邮件
                        ts.sendMessage(message, message.getAllRecipients());
                        ts.close();
                    }
                }
            } catch (ZipException e) {
                logger.error("[" + curDate + "]没有无内鬼文件压缩失败", e);
            } catch ( MessagingException e) {
                logger.error("[" + curDate + "]没有无内鬼邮件发送失败", e);
            } catch (Exception e) {
                logger.error("[" + curDate + "]没有无内鬼邮件发送失败", e);
            }
        } else {
            logger.error("[" + curDate + "]没有无内鬼新文件");
        }
    }

    private static MimeMessage createAttachMail(Session session, String title, String fileName) throws Exception {
        MimeMessage message = new MimeMessage(session);

        // 设置邮件的基本信息
        message.setFrom(new InternetAddress(mailFrom));	// 发件人

        // 邮件标题
        message.setSubject(title);

        // 创建邮件正文，为了避免邮件正文中文乱码问题，需要使用charset=UTF-8指明字符编码
        MimeBodyPart text = new MimeBodyPart();
        text.setContent("宁好", "text/html;charset=UTF-8");
        InternetAddress[] ia = new InternetAddress[mailToArr.length];
        for (int i = 0; i < mailToArr.length; i++) {
            ia[i] = new InternetAddress(mailToArr[i]);
        }
        message.setRecipients(Message.RecipientType.TO, ia);// 收件人

        // 创建容器描述数据关系
        MimeMultipart mp = new MimeMultipart();
        mp.addBodyPart(text);
        mp.setSubType("mixed");

        // 获取文件列表
//        File files = new File(basePath);
//        String[] names = files.list();
//        for (String s : names) {
//            if (s.startsWith(curDate + ".z")) {
                MimeBodyPart attach = new MimeBodyPart();
                DataHandler dh = new DataHandler(new FileDataSource(fileName));// 需要修改
                attach.setDataHandler(dh);
                attach.setFileName(dh.getName());
                mp.addBodyPart(attach);
//            }
//        }
        // 创建邮件附件
        message.setSentDate(new Date());
        message.setContent(mp);
        message.saveChanges();
        // 将创建的Email写入到F盘存储
//        message.writeTo(new FileOutputStream("F:/Program Files/TestMail/ImageMail.eml"));// 需要修改
        // 返回生成的邮件
        return message;
    }
}
