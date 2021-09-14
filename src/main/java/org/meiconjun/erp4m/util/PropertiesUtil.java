/*
package org.meiconjun.erp4m.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

*/
/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: Properties读写工具类(customConfig.properties)
 * @date 2020/5/7 21:37
 *//*

public class PropertiesUtil {
    public static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    static {
        String fileName = "customConfig.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常", e);
        }
    }

    */
/**
     * 获取属性值
     * @param key
     * @return
     *//*

    public static String getProperty(String key) {
        String value = props.getProperty(key);
        if (CommonUtil.isStrBlank(value)) {
            return "";
        }
        return value;
    }

    */
/**
     * 设置属性和值
     * @param key
     * @param value
     * @return
     *//*

    public boolean setProperty(String key, String value) {
        if (CommonUtil.isStrBlank(key)) {
            return false;
        }
        props.setProperty(key, value);
        return true;
    }
}
*/
