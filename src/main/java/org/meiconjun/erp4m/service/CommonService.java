package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/22 21:00
 */
public interface CommonService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
