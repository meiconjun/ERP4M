package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.MenuBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/11 16:28
 */
public interface MenuDao {
    /**
     * 根据用户号获取用户关联角色的菜单列表
     * @param condMap
     * @return
     */
    public List<MenuBean> selectRoleAuthMenu(HashMap<String, Object> condMap);
    /**
     * 根据用户号获取用户的菜单列表
     */
    public List<MenuBean> selectUserAuthMenu(HashMap<String, Object> condMap);
}
