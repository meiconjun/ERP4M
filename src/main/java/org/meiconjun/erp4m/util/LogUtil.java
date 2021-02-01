package org.meiconjun.erp4m.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 日志相关工具类
 * @date 2021/1/30 20:22
 */
public class LogUtil {

    /**
     * 获取平台日志对象
     * @return
     */
    public static Logger getPlatformLogger() {
        return LoggerFactory.getLogger("platform");
    }

    /**
     * 获取业务日志对象
     * @return
     */
    public static Logger getBusinessLogger() {
        return LoggerFactory.getLogger("business");
    }

    /**
     * 获取错误日志对象
     * @return
     */
    public static Logger getExceptionLogger() {
        return LoggerFactory.getLogger("exception");
    }
}
