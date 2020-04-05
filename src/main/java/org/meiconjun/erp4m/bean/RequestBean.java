package org.meiconjun.erp4m.bean;

import java.util.List;
import java.util.Map;

/**
 * @author Lch
 * @Title: RequestBean
 * @Package org.meiconjun.erp4m, bean
 * @Description: 请求报文bean
 * @date 2020/4/518:17
 */
public class RequestBean<V>{

    /**
     * 实体bean列表，用于普通增删改查时用
     */
    private List<V> beanList;
    /**
     * 请求操作标识，用于分发
     */
    private String operType;
    /**
     * 其它参数map
     */
    private Map<String, Object> paramMap;

    public List<V> getBeanList() {
        return beanList;
    }

    public void setBeanList(List<V> beanList) {
        this.beanList = beanList;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
}
