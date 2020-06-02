package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:登录数据库操作接口
        * @date 2020/4/5 20:49
        */
public interface LoginDao {
    /**
     * 根据用户名密码查询用户信息
     * @param condMap
     * @return
     */
    List<HashMap<String, Object>> selectUser(HashMap<String, Object> condMap);

    String selectRoleNo(@Param("user_no") String user_no);
}
