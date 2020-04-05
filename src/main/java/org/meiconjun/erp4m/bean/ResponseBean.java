package org.meiconjun.erp4m.bean;

import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/519:37
 */
public class ResponseBean {
    /**
     * 返回码
     */
    private String retCode;
    /**
     * 返回信息
     */
    private String retMsg;
    /**
     * 返回参数Map
     */
    private Map<String, Object> retMap;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public Map<String, Object> getRetMap() {
        return retMap;
    }

    public void setRetMap(Map<String, Object> retMap) {
        this.retMap = retMap;
    }
}
