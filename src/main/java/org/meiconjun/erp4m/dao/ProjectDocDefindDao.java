package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.ProjectDocBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/12 20:47
 */
public interface ProjectDocDefindDao {
    /**
     * 查询所有项目文档
     * @return
     */
    List<ProjectDocBean> selectProjectDoc();
    /**
     * 插入项目文档信息
     */
    int insertDoc(ProjectDocBean bean);
    /**
     * 更新项目文档信息
     */
    int updateDoc(ProjectDocBean bean);
    /**
     * 删除项目文档信息
     */
    int deleteDoc(@Param("doc_no") String doc_no);
    /**
     * 根据文档编号查询文档信息
     */
    HashMap<String, String> selectProjectDocInfoByDocNo(@Param("doc_no") String doc_no);
}
