package org.meiconjun.erp4m.dto;

import java.util.HashMap;

/**
 * 返回对象实体，包含返回值和返回内容
 * @author meiconjun
 * @time 2019年4月6日下午6:18:03
 */
public class RetObj {
	/** 返回值0-成功 1-失败 **/
	private String retCode;
	/** 返回信息 **/
	private String retMsg;
	/** 返回内容 **/
	private HashMap<String, Object> retMap;
	/**
	 * @return the retCode
	 */
	public String getRetCode() {
		return retCode;
	}
	/**
	 * @param retCode the retCode to set
	 */
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	/**
	 * @return the retMsg
	 */
	public String getRetMsg() {
		return retMsg;
	}
	/**
	 * @param retMsg the retMsg to set
	 */
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	/**
	 * @return the retMap
	 */
	public HashMap<String, Object> getRetMap() {
		return retMap;
	}
	/**
	 * @param retMap the retMap to set
	 */
	public void setRetMap(HashMap<String, Object> retMap) {
		this.retMap = retMap;
	}
	
	
}
