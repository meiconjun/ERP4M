package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 消息表相关数据库操作接口
 * @date 2020/5/20 21:36
 */
public interface MessageDao {
    /**
     * 查询消息的全部信息
     * @param msg_no
     * @return
     */
    HashMap<String, Object> selectMessageInfo(@Param("msg_no") String msg_no);
    /**
     * 更新已阅用户列表和处理状态
     */
    int updateReadUserAndStatus(HashMap<String, Object> condMap);
    /**
     * 新增系统消息
     */
    int insertNewMessage(HashMap<String, Object> condMap);
    /**
     * 查询消息的处理状态
     */
    String selectMessageStatus(@Param("msg_no") String msg_no);
    /**
     * 查询用户未读消息
     */
    List<HashMap<String, Object>> selectUnReadMsg(@Param("user_no") String user_no, @Param("role_no") String role_no);
    /**
     * 查询已阅消息
     */
    List<HashMap<String, Object>> selectReadMsg(@Param("user_no") String user_no, @Param("role_no") String role_no);
}
