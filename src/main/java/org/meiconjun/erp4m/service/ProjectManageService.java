package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/25 20:14
 */
public interface ProjectManageService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
