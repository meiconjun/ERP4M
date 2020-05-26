package org.meiconjun.erp4m.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	
	@RequestMapping(value = "/fileDownload.do", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public void fileDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("----------------开始下载文件-------------------");
		String fileName = request.getParameter("fileName");//文件名
		String filePath = request.getParameter("filePath");//文件全路径

		String rootPath = PropertiesUtil.getProperty("fileSavePath");
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
}
