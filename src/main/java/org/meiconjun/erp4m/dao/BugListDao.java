package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.BugBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/8/22 22:07
 */
public interface BugListDao {
    /**
     * 查询bug信息
     * @param bugBean
     * @return
     */
    List<HashMap<String, Object>> selectBugList(BugBean bugBean);
    /**
     * 新增bug信息
     */
    int insertBugList(BugBean bugBean);
    /**
     * 查询用户信息
     */
    HashMap<String, String> selectUserInfoByNo(@Param("user_no") String user_no);
    /**
     * 查询帖子回复信息
     */
    List<HashMap<String, String>> selectBugComments(@Param("serial_no") String serial_no, @Param("floor") String floor);
    /**
     * 新增评论
     */
    int insertNewComment(HashMap<String, Object> condMap);
    /**
     * 获取楼层回复列表
     */
    List<HashMap<String, Object>> selectFloorComment(@Param("about_serial")String about_serial);
    /**
     * 更新bug内容
     */
    int updateBugContent(HashMap<String, Object> condMap);
    /**
     * 删除bug
     */
    int deleteBugInfo(@Param("serial_no") String serial_no);
    /**
     * 更新bug状态
     */
    int updateBugStatus(@Param("serial_no") String serial_no, @Param("bug_status")String bug_status);
}
