package org.meiconjun.erp4m.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 流水号生成器
 * @date 2020/5/7 21:54
 */
public class SerialNumberGenerater {
    private static SerialNumberGenerater serialNumberGenerater = null;

    /**
     * 获取实例
     * @return
     */
    public static SerialNumberGenerater getInstance() {
        if (serialNumberGenerater == null) {
            synchronized (SerialNumberGenerater.class) {
                if (serialNumberGenerater == null) {
                    serialNumberGenerater = new SerialNumberGenerater();
                }
            }
        }
        return serialNumberGenerater;
    }

    /**
     * 获取一个流水号 22位
     * @return
     */
    public synchronized String generaterNextNumber() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        SecureRandom random = new SecureRandom();
        String nowTime = sdf.format(date);
        for (int i = 0; i < 5; i++) {
            nowTime += String.valueOf(random.nextInt(10));
        }
        return nowTime;
    }
}
