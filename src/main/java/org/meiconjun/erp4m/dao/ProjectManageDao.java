package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/25 20:24
 */
public interface ProjectManageDao {
    /**
     * 查询项目信息
     */
    List<HashMap<String, String >> selectProjectInfo(HashMap<String, Object> condMap);
    /**
     * 查询项目所有阶段信息
     */
    List<HashMap<String, String>> selectStageOfProject(@Param("project_no") String project_no);
    /**
     * 查询阶段文档信息
     */
    List<HashMap<String, String>> selectStageDocInfo(@Param("project_no") String project_no, @Param("stage_num") String stage_num);
    /**
     * 查询阶段文档版本
     */
    String selectStageDocLastVersion(@Param("project_no") String project_no, @Param("stage_num") String stage_num);
}
