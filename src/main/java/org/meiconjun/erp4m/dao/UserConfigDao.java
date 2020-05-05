package org.meiconjun.erp4m.dao;

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
}
