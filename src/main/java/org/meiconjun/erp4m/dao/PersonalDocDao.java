package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
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
    /**
     * 删除个人文档信息
     */
    int deletePersonalDocInfo(@Param("doc_serial_no")String doc_serial_no);
    /**
     * 新增回收站表信息
     */
    int insertRecycleInfo(HashMap<String, Object> condMap);

    /**
     * 查询版本历史
     */
    List<HashMap<String, Object>> selectDocHistory(@Param("doc_serial_no")String doc_serial_no);
}
