package org.meiconjun.erp4m.dao;

import org.meiconjun.erp4m.bean.FieldBean;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/22 22:31
 */
public interface FieldDao {
    /**
     * 查询所有数据字典
     * @return
     */
    public List<FieldBean> selectAllFields();
}
