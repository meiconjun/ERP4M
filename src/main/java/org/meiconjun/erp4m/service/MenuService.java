package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @author Lch
 * @Title: MenuService
 * @Package
 * @Description: 菜单服务类
 * @date 2020/4/9 21:20
 */
public interface MenuService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
