package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.DocBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 公共文档库Dao
 * @date 2020/7/5 21:56
 */
public interface PublicDocDao {

    /**
     * 新增公共文档信息
     */
    int insertNewPublicDocInfo(DocBean docBean);
}
