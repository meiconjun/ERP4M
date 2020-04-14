package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.ButtonBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/14 21:37
 */
public interface ButtonDao {
    /**
     * 根据用户角色和菜单号获取角色的菜单按钮
     */
    public List<ButtonBean> selectRoleAuthButton(HashMap<String, Object> condMap);
    /**
     * 根据用户号和菜单号获取用户的菜单按钮
     */
    public List<ButtonBean> selectUserAuthButton(HashMap<String, Object> condMap);
}
