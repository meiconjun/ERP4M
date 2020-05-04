package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.RoleBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/27 20:49
 */
public interface RoleConfigDao {
    /**
     * 查询角色信息
     * @param condMap
     * @return
     */
    List<RoleBean> selectRoles(HashMap<String, Object> condMap);
    /**
     * 插入角色信息
     */
    int insertRole(RoleBean bean);

    /**
     * 修改角色信息
     * @param bean
     * @return
     */
    int updateRole(RoleBean bean);
    /**
     * 删除角色信息
     */
    int deleteRole(RoleBean bean);
    /**
     * 查询关联角色的用户
     */
    int selectRoleUserNum(RoleBean bean);
    /**
     * 删除角色权限
     */
    int deleteRoleRight(RoleBean bean);
}
