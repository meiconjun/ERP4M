package org.meiconjun.erp4m.controller;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.meiconjun.erp4m.config.CustomConfigProperties;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/20 15:51
 */
@Controller
public class FileDownLoadController {
	
	private Logger logger = LoggerFactory.getLogger(FileDownLoadController.class);

	@Resource
	private CustomConfigProperties customConfigProperties;
	
	@RequestMapping(value = "/fileDownload.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public void fileDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("----------------开始下载文件-------------------");
		String fileName = request.getParameter("fileName");//文件名
		String filePath = request.getParameter("filePath");//文件全路径

		String rootPath = customConfigProperties.getFileSavePath();
		if (CommonUtil.isStrBlank(rootPath)) {
			// TODO 推送错误给客户端
			logger.error("未配置文件存储根路径");
			return;
		}
		filePath = rootPath + filePath;
		logger.info("文件名称：" + fileName + "，文件下载路径：" + filePath);
		response.setCharacterEncoding("utf-8");
	    response.setContentType("application/x-download;charset=utf-8");
	    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		InputStream inputStream = null;
		OutputStream os = null;
		try {
	    	// 打开本地文件流
	    	inputStream = new FileInputStream(filePath);
	    	// 激活下载操作
	    	os = response.getOutputStream();
	    	// 写入输出流
	    	byte[] b = new byte[2048];
	    	int length;
	    	while ((length = inputStream.read(b)) > 0) {
	    		os.write(b, 0, length);
	    	}
	    } catch (Exception e) {
	    	throw e;
	    } finally {
	    	os.close();
	    	inputStream.close();
	    }
	}

	/**
	 * 获取媒体文件
	 * @param response
	 * @param module  所属模块 例如bugList
	 * @param date  上传日期
	 * @param fileName  文件名 带后缀
	 * @return
	 * @author xWang
	 * @Date 2020-05-20
	 */
	@RequestMapping(value = "/getVideo/{module}/{date}/{fileName:.+}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public void getVideo(HttpServletRequest request,HttpServletResponse response, @PathVariable String module, @PathVariable String date, @PathVariable String fileName)
	{
		response.reset();
		//获取从那个字节开始读取文件
		String rangeString = request.getHeader("Range");
		String fileSavePath = (String)customConfigProperties.getFileSavePath();
		logger.info("===========fileSavePath: " + fileSavePath);
//		String rootPath = PropertiesUtil.getProperty("fileSavePath")  + File.separator;
		String rootPath = fileSavePath  + File.separator;
		String filePath = rootPath + module + File.separator + date + File.separator + fileName;
		logger.info("============媒体文件下载路径[{}]=======================", filePath);
		try (OutputStream outputStream = response.getOutputStream();) {
			//获取响应的输出流
			File file = new File(filePath);
			if(file.exists()){
				RandomAccessFile targetFile = new RandomAccessFile(file, "r");
				long fileLength = targetFile.length();
				//播放
				if(!StringUtils.isBlank(rangeString)){
					long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
					//设置内容类型
					response.setHeader("Content-Type", "video/mp4");
					//设置此次相应返回的数据长度
					response.setHeader("Content-Length", String.valueOf(fileLength - range));
					//设置此次相应返回的数据范围
					response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
					//返回码需要为206，而不是200
					response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
					//设定文件读取开始位置（以字节为单位）
					targetFile.seek(range);
				} else {//其它文件
					//设置响应头，把文件名字设置好
					response.setHeader("Content-Disposition", "attachment; filename=" + fileName );
					//设置文件长度
					response.setHeader("Content-Length", String.valueOf(fileLength));
					//解决编码问题
					response.setHeader("Content-Type","application/octet-stream");
				}
				byte[] cache = new byte[1024 * 300];
				int flag;
				while ((flag = targetFile.read(cache))!=-1){
					outputStream.write(cache, 0, flag);
				}
			} else {
				String message = "file:" + fileName + " not exists";
				//解决编码问题
				response.setHeader("Content-Type","application/json");
				outputStream.write(message.getBytes(StandardCharsets.UTF_8));
			}
			outputStream.flush();
		} catch (ClientAbortException ce) {

		} catch (Exception e ) {
			logger.error("获取媒体文件异常:" + e.getMessage(), e);
		}
	}
}
