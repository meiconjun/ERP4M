package org.meiconjun.erp4m.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 文件相关 操作工具类
 * @date 2020/7/1 21:44
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 根据路径删除指定的目录或文件
     * 包括目录及目录下文件
     * @param sPath
     * @return
     */
    public static boolean deleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (!file.exists()) {
            logger.error("文件{}不存在", sPath);
        } else {
            if (file.isFile()) {
                flag = deleteFile(sPath);
            } else {
                flag = deleteDirectory(sPath);
            }
        }
        return flag;
    }

    /**
     * 删除单个文件
     * @param sPath
     * @return
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
            logger.info("删除文件{}成功", sPath);
        }
        return flag;
    }

    /**
     * 删除文件夹及目录下文件
     * @param sPath
     * @return
     */
    public static boolean deleteDirectory(String sPath) {
        boolean flag = true;
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) {
            return flag;
        }
        // 删除当前目录
        return dirFile.delete();
    }
}
