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
     * 查询消息的推送用户和已阅用户
     * @param msg_no
     * @return
     */
    HashMap<String, Object> selectReadAndUnReadUser(@Param("msg_no") String msg_no);
    /**
     * 更新已阅用户列表和处理状态
     */
    int updateReadUserAndStatus(HashMap<String, Object> condMap);
    /**
     * 新增系统消息
     */
    int insertNewMessage(HashMap<String, Object> condMap);
}
