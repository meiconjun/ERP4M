package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.User;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/5 15:02
 */
public interface UserConfigDao {
    /**
     * 查询角色列表 以name value形式返回
     * @return
     */
    List<HashMap<String, String>> selectRoles();

    /**
     * 查询用户列表
     */
    List<HashMap<String, Object>> selectUsers(HashMap<String, Object> condMap);
    /**
     * 新增用户
     */
    int insertUser(HashMap<String, Object> condMap);
    /**
     * 新增用户角色关联
     */
    int inserUserRole(HashMap<String, Object> condMap);
    /**
     * 更新用户信息
     */
    int updateUser(User user);
    /**
     * 删除旧的用户角色信息
     */
    int deleteUserRoleOld(@Param("user_no") String user_no);
    /**
     * 删除用户
     */
    int deleteUser(@Param("user_no") String user_no);
    /**
     * 删除用户权限
     */
    int deleteUserRight(@Param("user_no") String user_no);
    /**
     * 更新用户状态
     */
    int updateUserStatus(User user);
    /**
     * 根据角色号查询用户
     */
    List<String> selectUsersByRole(@Param("role_no") String role_no);
    /**
     * 根据用户号查询用户
     */
    User selectUserByNo(String user_no);

    /**
     *
     * @param user
     * @return
     */
    int mergeUser(User user);
}
