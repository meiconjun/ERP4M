package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.MenuDao;
import org.meiconjun.erp4m.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Resource
    private MenuDao menuDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("getUserMenu".equals(operType)) {
            getUserMenuAndRight(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 获取用户的菜单和按钮权限
     * @param requestBean
     * @param responseBean
     */
    private void getUserMenuAndRight(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String user_no = (String) paramMap.get("user_no");

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("user_no", user_no);

        // 获取用户角色的有权菜单和用户的有权菜单
        List<MenuBean> menuListRole = menuDao.selectRoleAuthMenu(condMap);
        List<MenuBean> menuListUser = menuDao.selectUserAuthMenu(condMap);
        if (menuListRole.isEmpty()) {
            logger.error("登录用户[" + user_no + "]没有设定角色或角色未分配权限");
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("登录用户没有设定角色或角色未分配权限");
            return;
        }
        // 合并角色和用户菜单
        HashMap<String, String> menuMap = new HashMap<String, String>();
        for (MenuBean m : menuListRole) {

            menuMap.put("menu_id", m.getMenu_id());
            menuMap.put("menu_name", m.getMenu_name());
            menuMap.put("parent_menu", m.getParent_menu());
            menuMap.put("menu_desc", m.getMenu_desc());
            menuMap.put("menu_url", m.getMenu_url());
            menuMap.put("menu_level", m.getMenu_level());
            menuMap.put("is_parent", m.getIs_parent());
            menuMap.put("menu_icon", m.getMenu_icon());
        }


    }
}
