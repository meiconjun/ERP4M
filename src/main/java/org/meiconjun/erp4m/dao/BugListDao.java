package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.BugBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/8/22 22:07
 */
public interface BugListDao {
    /**
     * 查询bug信息
     * @param bugBean
     * @return
     */
    List<HashMap<String, Object>> selectBugList(BugBean bugBean);
    /**
     * 新增bug信息
     */
    int insertBugList(BugBean bugBean);
}
