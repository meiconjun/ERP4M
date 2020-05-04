package org.meiconjun.erp4m.dao;

import org.apache.ibatis.annotations.Param;
import org.meiconjun.erp4m.bean.ButtonBean;
import org.meiconjun.erp4m.bean.MenuBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/3 17:54
 */
public interface RoleRightDao {
    /**
     * 查询所有的菜单和按钮
     * @return
     */
    List<MenuBean> selectAllMenu();
    /**
     * 根据菜单Id获取按钮
     */
    List<ButtonBean> selectBtnByMenuId(@Param("menu_id") String menu_id);
    /**
     * 根据角色编号查询要素权限
     */
    int selectRoleFieldRight(@Param("role_no") String role_no, @Param("field_no") String field_no);
    /**
     * 新增角色权限
     */
    int insertRoleRight(HashMap<String, Object> condMap);
    /**
     * 删除角色权限
     */
    int deleteRoleRight(@Param("role_no") String role_no, @Param("field_no") String field_no);
}
