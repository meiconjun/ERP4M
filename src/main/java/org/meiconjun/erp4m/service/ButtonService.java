package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/4/14 21:32
 */
public interface ButtonService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
