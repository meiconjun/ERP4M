package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/8/16 23:39
 */
public interface RADWarehouseService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
