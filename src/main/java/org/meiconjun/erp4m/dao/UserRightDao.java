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
 * @date 2020/5/10 19:02
 */
public interface UserRightDao {
    /**
     * 查询所有的菜单和按钮
     * @return
     */
    List<MenuBean> selectAllMenu();
    /**
     * 根据用户号查询要素权限
     */
    int selectUserFieldRight(@Param("user_no") String user_no, @Param("field_no") String field_no);
    /**
     * 根据菜单Id获取按钮
     */
    List<ButtonBean> selectBtnByMenuId(@Param("menu_id") String menu_id);
    /**
     * 新增用户权限
     */
    int insertUserRight(HashMap<String, Object> condMap);
    /**
     * 删除用户权限
     */
    int deleteUserRight(@Param("user_no") String user_no, @Param("field_no") String field_no);
}
