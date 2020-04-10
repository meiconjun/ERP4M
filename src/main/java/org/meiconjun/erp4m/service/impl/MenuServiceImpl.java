package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lch
 * @Title: MenuServiceImpl
 * @Package
 * @Description: 菜单相关业务的服务类
 * @date 2020/4/9 21:27
 */
@Service("menuService")
@Transactional
public class MenuServiceImpl implements MenuService {
    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        return null;
    }
}
