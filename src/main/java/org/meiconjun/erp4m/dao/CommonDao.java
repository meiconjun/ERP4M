package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.User;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 通用数据库查询接口
 * @date 2020/5/21 20:47
 */
public interface CommonDao {
    /**
     * 查询老板的账号
     * @return
     */
    List<User> selectBossAccount();
    /**
     * 查询数据字典名称
     */
    String selectFieldName(@Param("field_value")String field_value, @Param("parent_field") String parent_field);
    /**
     * 查询用户号和用户名
     */
    List<HashMap<String, String>> selectAllUserNoAndName();
}
