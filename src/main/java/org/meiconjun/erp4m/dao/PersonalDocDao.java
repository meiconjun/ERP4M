package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.DocBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/11 21:26
 */
public interface PersonalDocDao {
    /**
     * 查询个人文档库信息
     * @param condMap
     * @return
     */
    List<DocBean> selectPersonalDocInfo(HashMap<String, Object> condMap);
    /**
     * 新增文档
     */
    int insertPersonalDocInfo(HashMap<String, Object> condMap);
    /**
     * 新增审批信息
     */
    int insertDocReviewInfo(HashMap<String, Object> condMap);
    /**
     * 新增版本信息
     */
    int insertDocVersionInfo(HashMap<String, Object> condMap);
    /**
     * 更新文档信息
     */
    int updatePersonalDocInfo(HashMap<String, Object> condMap);
}