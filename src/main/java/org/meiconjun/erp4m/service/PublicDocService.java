package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/7/16 22:07
 */
public interface PublicDocService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
