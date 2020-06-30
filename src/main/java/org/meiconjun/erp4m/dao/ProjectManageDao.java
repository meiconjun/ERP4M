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
    List<HashMap<String, String>> selectStageDocInfo(@Param("project_no") String project_no, @Param("stage_num") String stage_num, @Param("doc_no") String doc_no);
    /**
     * 查询阶段文档版本
     */
    String selectStageDocLastVersion(@Param("project_no") String project_no, @Param("stage_num") String stage_num, @Param("doc_no") String doc_no);
    /**
     * 更新阶段文档 1.0
     */
    int updateStageDoc(@Param("upload_date") String upload_date, @Param("file_path") String file_path, @Param("doc_serial") String doc_serial,
                       @Param("doc_no") String doc_no, @Param("upload_user") String upload_user);
    /**
     * 新增阶段文档
     */
    int insertStageDocInfo(HashMap<String, Object> condMap);
    /**
     * 查询阶段信息
     */
    HashMap<String, String> selectStageInfo(@Param("project_no") String project_no, @Param("stage_num") String stage_num);
    /**
     * 更新阶段信息
     */
    int updateStageInfo(HashMap<String, Object> condMap);
    /**
     * 更新项目主表信息
     */
    int updateProjectMainInfo(HashMap<String, Object> condMap);
    /**
     * 查询项目最后阶段编号
     */
    String selectMaxStageNumOfProject(@Param("project_no") String project_no);
    /**
     * 查询用户的所属部门部门经理
     */
    String selectStagePrincipalManager(@Param("principal") String principal);
    /**
     * 查询所有项目所处阶段信息
     */
    List<HashMap<String, Object>> selectProjectCurStage();
    /**
     * 查询某阶段某文档是否已上传
     */
    int selectCountOfStageDoc(@Param("doc_serial") String doc_serial, @Param("doc_no") String doc_no);
    /**
     * 查询文档列表，将已上传的文档和待上传的文档合并
     */
    List<HashMap<String, String>> selectStageDocUnion(@Param("project_no") String project_no, @Param("stage_num") String stage_num, @Param("unupload_doc") String unupload_doc);
    /**
     * 查询文员列表
     */
    List<String> selectClerkList();
}
