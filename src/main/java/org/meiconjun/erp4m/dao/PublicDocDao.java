package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.DocBean;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 查询公共文档库信息
     * @param condMap
     * @return
     */
    List<DocBean> selectPublicDocInfo(HashMap<String, Object> condMap);

    /**
     * 查询版本历史
     */
    List<HashMap<String, Object>> selectDocHistory(@Param("doc_serial_no")String doc_serial_no);
}
