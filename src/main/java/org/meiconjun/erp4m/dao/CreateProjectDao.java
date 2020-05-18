package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/17 15:55
 */
public interface CreateProjectDao {
    /**
     * 根据阶段类型查询阶段文档
     */
    List<HashMap<String, Object>> selectProjectByStage(@Param("stage") String stage);
    /**
     * 查询文档负责部门
     */
    String selectDocDepartment(@Param("doc_no") String doc_no);
    /**
     * 根据部门编号查询用户列表
     */
    List<HashMap<String, Object>> selectUserByDepartment(@Param("department") String department);
    /**
     * 新增项目主表信息
     */
    int insertNewProjectMain(HashMap<String, Object> condMap);
    /**
     * 新增阶段信息表
     */
    int insertNewProjectStage(HashMap<String, Object> condMap);
}
