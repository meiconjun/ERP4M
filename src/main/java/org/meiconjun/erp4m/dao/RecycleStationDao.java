package org.meiconjun.erp4m.dao;


import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/7/6 22:12
 */
public interface RecycleStationDao {
    /**
     * 查询个人回收站信息
     */
    List<HashMap<String, Object>> selectPersonalRecycleDocInfo(HashMap<String, Object> condMap);
    /**
     * 新增个人文档库信息
     */
    int insertPersonalDocInfo(HashMap<String, Object> condMap);
    /**
     * 删除回收站数据
     */
    int deleteRecycleInfo(@Param("doc_serial_no") String doc_serial_no);
    /**
     * 更新回收站信息
     */
    int updateRecycleInfo(HashMap<String, Object> condMap);
}
