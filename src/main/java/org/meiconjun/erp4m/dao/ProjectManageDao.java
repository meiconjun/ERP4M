package org.meiconjun.erp4m.dao;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/25 20:24
 */
public interface ProjectManageDao {
    /**
     * 查询项目信息
     */
    List<HashMap<String, String >> selectProjectInfo(HashMap<String, Object> condMap);
}
