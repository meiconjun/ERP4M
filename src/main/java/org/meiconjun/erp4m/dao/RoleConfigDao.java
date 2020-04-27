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
    public List<RoleBean> selectRoles(HashMap<String, Object> condMap);
}
