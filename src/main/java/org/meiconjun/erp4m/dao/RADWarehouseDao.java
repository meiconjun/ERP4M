package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.RADWarehouseBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/8/17 21:10
 */
public interface RADWarehouseDao {
    /**
     * 查询研发仓信息
     * @param condMap
     * @return
     */
    List<HashMap<String, String>> selectRADWarehouseInfo(HashMap<String, String> condMap);
    /**
     * 新增流水信息
     */
    int insertRADWarehousrInfo(RADWarehouseBean radWarehouseBean);
}
