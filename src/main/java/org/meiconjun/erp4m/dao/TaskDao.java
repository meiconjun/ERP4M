package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.TaskBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 任务处理相关数据库接口
 * @date 2020/6/27 16:47
 */
public interface TaskDao {
    /**
     * 查询任务信息
     * @param task_no
     * @return
     */
    HashMap<String, Object> selectTaskInfo(@Param("task_no") String task_no);
    /**
     * 更新已处理用户列表和处理状态
     */
    int updateDealUserAndStatus(HashMap<String, Object> condMap);
    /**
     * 查询任务处理状态
     * @param task_no
     * @return
     */
    String selectTaskStatus(@Param("task_no") String task_no);
    /**
     * 查询用户代办任务列表
     */
    List<TaskBean> selectTodoTask(@Param("user_no") String user_no, @Param("role_no") String role_no);
    /**
     * 查询用户已办任务列表
     */
    List<TaskBean> selectDoneTask(@Param("user_no") String user_no, @Param("role_no") String role_no);
}
