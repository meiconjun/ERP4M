package org.meiconjun.erp4m.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.meiconjun.erp4m.dto.RetObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送http请求工具
 * @author meiconjun
 * @time 2019年4月6日下午5:05:25
 */
@Getter
@Setter
public class SendRequestUtil {
	/** 日志对象 **/
	private static Logger logger = LoggerFactory.getLogger(SendRequestUtil.class);
	/** 网页字符集 **/
	private static String charSet = "utf-8";
	/** 从连接池中获取连接的超时时间 **/
	private static int requestTimeout = 15000;
	/** 建立连接的超时时间 **/
	private static int connectTimeout = 15000;
	/** 请求获取数据的超时时间 **/
	private static int socketTimeout = 15000;
	private static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectTimeout)
			.setConnectionRequestTimeout(requestTimeout)
			.build();
	/** HttpClient实例 **/
//	private static CloseableHttpClient client = HttpClients.custom().build();
	/** http响应 **/
//	private static CloseableHttpResponse response = null;
	/**
	 * 执行get请求
	 * @author meiconjun
	 * @time 2019年4月6日下午5:24:02
	 * @param url 请求地址
	 * @param headers 请求头信息列表
	 * @param paramMap 表单参数
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static RetObj doGet(String url, Map<String, String> headers, Map<String, String> paramMap, int sleepMillis)throws Exception {
//		client = HttpClients.custom().build();
		RetObj retObj = new RetObj();
		if (sleepMillis > 0) {
			Thread.sleep(sleepMillis);
		}
		//设置表单参数
		if (null != paramMap) {
			logger.debug("----------------doGet:设置表单参数----------------");
			//将参数转为aaa=aaa&bbb=bbb字符串形式
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : paramMap.keySet()) {
				params.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			String paramStr = "?" + EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			url += paramStr;
		}
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		//请求头配置
		//响应体
		HttpEntity httpEntity = null;
		if (null != headers) {
			logger.debug("----------------doGet:设置请求头----------------");
			for (String key : headers.keySet()) {
				httpGet.setHeader(key, headers.get(key));
			}
		}
		///设置随机user-agent，防反爬
		if (!httpGet.containsHeader("User-Agent")) {
			httpGet.setHeader("User-Agent", UserAgentUtil.getRandomUserAgent());
		}
		for (Header header : httpGet.getAllHeaders()) {
			logger.info("reqHeader[" + header.getName() + "]:[" + header.getValue() + "]");
		}
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			logger.debug("----------------doGet:发送请求----------------");
			logger.debug(url);
			client = HttpClients.custom().build();
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (200 == statusCode) {
				httpEntity = response.getEntity();
				//请求成功
//				String charset = CommonUtil.getStreamCharset(httpEntity.getContent());
//				logger.debug("-----------字符集：" + charset);
				String contCharSet = getEntityContEncoding(httpEntity);
				logger.debug("-----------字符集：" + contCharSet);
				String entityStr = EntityUtils.toString(httpEntity, CommonUtil.isStrBlank(contCharSet) ? charSet : contCharSet);//获取网页编码的问题有待解决
//				logger.debug("doGet:请求成功，响应体内容：" + entityStr);
				Header[] headers1 = response.getAllHeaders();
				retObj.setRetCode("0");
				HashMap retMap = new HashMap();
				retMap.put("retStr", entityStr);
				retMap.put("headers", headers1);
				retObj.setRetMap(retMap);
			} else {
				logger.error("doGet:请求失败，返回报文：" + EntityUtils.toString(response.getEntity()));
				logger.error("doGet:请求失败，状态码[" + statusCode + "], URL[" + url + "]");
				retObj.setRetCode("1");
				retObj.setRetMsg("状态码：" + statusCode);
			}
		} catch(IOException e) {
			logger.error("----------------doGet:发送请求出现异常----------------");
			e.printStackTrace();
			throw e;
		} finally {
			//关闭资源
			if (null != httpEntity) {
				EntityUtils.consume(httpEntity);
			}
			httpGet.abort();
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
		return retObj;
	}

	/**
	 * 执行post请求
	 * @author meiconjun
	 * @time 2019年4月7日下午3:50:10
	 * @param url 请求地址
	 * @param headers 请求头信息列表
	 * @param paramMap 表单参数
	 * @throws IOException
	 */
	@SuppressWarnings({  "rawtypes", "unchecked" })
	public static RetObj doPost(String url, Map<String, String> headers, Map<String, String> paramMap, int sleepMillis) throws IOException, InterruptedException {
		RetObj retObj = new RetObj();
		if (sleepMillis > 0) {
			Thread.sleep(sleepMillis);
		}
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		if (null != paramMap){
			logger.debug("----------------doPost:设置表单参数----------------");
			//表单数组
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (String key : paramMap.keySet()) {
				nvps.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		}
		//请求头配置
		//响应体
		HttpEntity httpEntity = null;
		if (null != headers) {
			logger.debug("----------------doPost:设置请求头----------------");
			for (String key : headers.keySet()) {
				httpPost.setHeader(key, headers.get(key));
			}
		}
		///设置随机user-agent，防反爬
		if (!httpPost.containsHeader("User-Agent")) {
			httpPost.setHeader("User-Agent", UserAgentUtil.getRandomUserAgent());
		}
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			logger.debug("----------------doPost:发送请求----------------");
			client = HttpClients.custom().build();
			response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (200 == statusCode) {
				httpEntity = response.getEntity();
				//请求成功
//				String charset = CommonUtil.getStreamCharset(httpEntity.getContent());
//				logger.debug("-----------字符集：" + charset);
				String contCharSet = getEntityContEncoding(httpEntity);
				logger.debug("-----------字符集：" + contCharSet);
				String entityStr = EntityUtils.toString(httpEntity, CommonUtil.isStrBlank(contCharSet) ? charSet : contCharSet);//获取网页编码的问题有待解决
//				logger.debug("doPost:请求成功，响应体内容：" + entityStr);
				retObj.setRetCode("0");
				HashMap retMap = new HashMap();
				retMap.put("retStr", entityStr);
				retObj.setRetMap(retMap);
			} else {
				logger.error("doPost:请求失败，返回报文：" + EntityUtils.toString(response.getEntity()));
				logger.error("doPost:请求失败，状态码[" + statusCode + "], URL[" + url + "]");
				retObj.setRetCode("1");
				retObj.setRetMsg("状态码：" + statusCode);
			}
		} catch(IOException e) {
			logger.error("----------------doPost:发送请求出现异常----------------");
			e.printStackTrace();
			throw e;
		} finally {
			//关闭资源
			if (null != httpEntity) {
				EntityUtils.consume(httpEntity);
			}
			httpPost.abort();
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
		return retObj;
	}

	/**
	 * 获取文件并存到指定位置
	 * @param url
	 * @param headers
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static RetObj doGetFile(String url, Map<String, String> headers, Map<String, String> paramMap, String filePath, int sleepMillis)throws Exception {
		RetObj retObj = new RetObj();
		if (sleepMillis > 0) {
			Thread.sleep(sleepMillis);
		}
		//设置表单参数
		if (null != paramMap) {
			logger.debug("----------------doGet:设置表单参数----------------");
			//将参数转为aaa=aaa&bbb=bbb字符串形式
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : paramMap.keySet()) {
				params.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			String paramStr = "?" + EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			url += paramStr;
		}
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		//请求头配置
		if (null != headers) {
			logger.debug("----------------doGet:设置请求头----------------");
			for (String key : headers.keySet()) {
				httpGet.setHeader(key, headers.get(key));
			}
		}
		//设置随机user-agent，防反爬
		if (!httpGet.containsHeader("User-Agent")) {
			httpGet.setHeader("User-Agent", UserAgentUtil.getRandomUserAgent());
		}
		//响应体
		HttpEntity httpEntity = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			logger.debug("----------------doGet:发送请求----------------");
			client = HttpClients.custom().build();
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (200 == statusCode) {
				httpEntity = response.getEntity();
				//请求成功
//				String charset = CommonUtil.getStreamCharset(httpEntity.getContent());
//				logger.debug("-----------字符集：" + charset);
				File file = new File(filePath);
				logger.info("是否为流：" + httpEntity.isStreaming());
				logger.info("返回体大小：" + httpEntity.getContentLength());
				if (httpEntity.getContentLength() > 10000) {
					try (FileOutputStream fos = new FileOutputStream(file)) {
						byte[] b = new byte[1024];
						int j = 0;
						InputStream is = httpEntity.getContent();
						while ((j = is.read(b)) != -1) {
							fos.write(b, 0, j);
						}
					} catch (Exception e) {
						logger.error("写入文件失败", e);
						throw e;
					}
				}

				retObj.setRetCode("0");
			} else {
				logger.error("doGet:请求失败，返回报文：" + EntityUtils.toString(response.getEntity()));
				logger.error("doGet:请求失败，状态码[" + statusCode + "], URL[" + url + "]");
				retObj.setRetCode("1");
				retObj.setRetMsg("状态码：" + statusCode);
			}
		} catch(IOException e) {
			logger.error("----------------doGet:发送请求出现异常----------------");
			e.printStackTrace();
			throw e;
		} finally {
			//关闭资源
			if (null != httpEntity) {
				EntityUtils.consume(httpEntity);
			}
			httpGet.abort();
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
		return retObj;
	}
	/**
	 * 从响应头中获取响应体编码
	 * @param he
	 * @return
	 */
	private static String getEntityContEncoding(HttpEntity he) {
		Header header = he.getContentType();
		HeaderElement[] hes = header.getElements();
		for (HeaderElement e : hes) {
			for (NameValuePair p : e.getParameters()) {
				if ("charset".equals(p.getName())) {
					return p.getValue();
				}
			}
		}
		return null;
	}

}
