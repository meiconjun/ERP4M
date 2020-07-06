package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.DocBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/7/6 22:12
 */
public interface RecycleStationDao {
    /**
     * 查询个人回收站信息
     */
    List<DocBean> selectPersonalRecycleDocInfo(HashMap<String, Object> condMap);
}
