package org.meiconjun.erp4m.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip压缩包工具类
 * @author Lch
 */
public class ZipUtil {
    private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 文件列表压缩至zip,达到最大大小时分卷
     * @param srcFiles
     * @param filePath zip文件存储路径
     * @param maxSize   分卷大小
     */
    public static void fileListToZip(ArrayList<File> srcFiles, String filePath, long maxSize) throws ZipException {
        logger.info("分卷压缩文件开始");
        ZipFile zipFile = new ZipFile(filePath);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipFile.createZipFile(srcFiles, parameters, true, maxSize);
    }

    /**
     * 压缩文件夹
     * @param file
     * @param filePath
     * @param maxSize
     * @throws ZipException
     */
    public static void fileFolderToZip(File file, String filePath, long maxSize) throws ZipException {
        logger.info("分卷压缩文件开始");
        ZipFile zipFile = new ZipFile(filePath);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipFile.createZipFileFromFolder(file, parameters, true, maxSize);
    }
    /**
     * 文件列表压缩至zip
     * @param srcFiles 文件列表
     * @param filePath 压缩包存放路径
     */
    public static void fileListToZip(ArrayList<File> srcFiles, String filePath) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("------------压缩zip包开始----------------");
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        FileInputStream in = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            zos = new ZipOutputStream(fos);
            for (File file : srcFiles) {
                byte[] buf = new byte[1024];
                zos.putNextEntry(new ZipEntry(file.getName()));
                int len;
                in = new FileInputStream(file);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            logger.info("--------------压缩Zip包结束，耗时:" + (end - start) + "ms----------------");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                	in.close();
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }

        }
    }
    /**
     * 对字符串进行gzip压缩+base64编码
     * @author: Lch
     * @date: 2020年1月2日上午11:13:21
     */
    public static String CompressToBase64(String string){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(string.getBytes());
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();


            String result = Base64.encodeBase64String(compressed);
            return result;
        } catch (IOException e) {
            e.printStackTrace();




        }
        catch (Exception ex){


        }
        return "";
    }
    
    /**
     * 还原gzip压缩+base64编码的字符串
     * @author: Lch
     * @date: 2020年1月2日上午11:14:23
     */
    public static String DecompressToBase64(String textToDecode){
        //String textToDecode = "H4sIAAAAAAAAAPNIzcnJBwCCidH3BQAAAA==\n";
        try {
            byte[] compressed = Base64.decodeBase64(textToDecode);
            final int BUFFER_SIZE = 32;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(compressed);


            GZIPInputStream gis  = new GZIPInputStream(inputStream, BUFFER_SIZE);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                baos.write(data, 0, bytesRead);
            }


            return baos.toString("UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex){


        }
        return "";
    }
}
