package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.FieldBean;
import org.meiconjun.erp4m.bean.RoleBean;
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
    /**
     * 获取所有用户的用户号
     */
    List<String> selectAllUserNo();
    /**
     * 查询目标用户的角色的信息
     */
    RoleBean selectUserRoleInfo(@Param("user_no") String user_no);
    /**
     * 根据数据字典号查询其子节点列表
     */
    List<FieldBean> selectChildFieldList(@Param("parent_field") String parent_field);
}
