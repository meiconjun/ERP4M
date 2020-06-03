package org.meiconjun.erp4m.service.impl;

import org.meiconjun.erp4m.bean.ButtonBean;
import org.meiconjun.erp4m.bean.MenuBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.MenuDao;
import org.meiconjun.erp4m.service.MenuService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        /*if (menuListRole.isEmpty()) {
            logger.error("登录用户[" + user_no + "]没有设定角色或角色未分配权限");
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("登录用户没有设定角色或角色未分配权限");
            return;
        }*/
        // 获取角色和用户的有权菜单结构树
        List<HashMap<String, Object>> menuList = getMenu("", menuListRole, menuListUser, user_no);
//        List<HashMap<String, Object>> userMenuList = getMenu("", menuListUser);

        Map retMap = new HashMap();
        retMap.put("menuList", menuList);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMsg("获取菜单列表成功");
        responseBean.setRetMap(retMap);
    }

    /**
     * 获取并构造菜单
     * @param parent_menu
     * @param roleMenuList
     * @param userMenuList
     * @param user_no
     * @return
     */
    private List<HashMap<String, Object>> getMenu(String parent_menu, List<MenuBean> roleMenuList, List<MenuBean> userMenuList, String user_no) {
        List<HashMap<String, Object>> retList = new ArrayList<HashMap<String, Object>>();
        String idStr = "";
        for (MenuBean m : roleMenuList) {
            if (parent_menu.equals(m.getParent_menu())) {
                HashMap<String, Object> menuMap = new HashMap<String, Object>();
                menuMap.put("menu_id", m.getMenu_id());
                menuMap.put("menu_name", m.getMenu_name());
                menuMap.put("parent_menu", m.getParent_menu());
                menuMap.put("menu_desc", m.getMenu_desc());
                menuMap.put("menu_url", m.getMenu_url());
                menuMap.put("menu_level", m.getMenu_level());
                menuMap.put("is_parent", m.getIs_parent());
                menuMap.put("menu_icon", m.getMenu_icon());
                if ("1".equals(m.getIs_parent())) {
                    menuMap.put("submemus", getMenu(m.getMenu_id(), roleMenuList, userMenuList, user_no));
                }
                //获取角色和用户的有权按钮
                /*menuMap.put("buttonList", getButton(user_no, m.getMenu_id()));*/
                retList.add(menuMap);
                idStr += "," + m.getMenu_id();
            }
        }
        // 合并用户权限
        for (MenuBean m : userMenuList) {
            if (!idStr.contains(m.getMenu_id()) && parent_menu.equals(m.getParent_menu())) {
                HashMap<String, Object> menuMap = new HashMap<String, Object>();
                menuMap.put("menu_id", m.getMenu_id());
                menuMap.put("menu_name", m.getMenu_name());
                menuMap.put("parent_menu", m.getParent_menu());
                menuMap.put("menu_desc", m.getMenu_desc());
                menuMap.put("menu_url", m.getMenu_url());
                menuMap.put("menu_level", m.getMenu_level());
                menuMap.put("is_parent", m.getIs_parent());
                menuMap.put("menu_icon", m.getMenu_icon());
                if ("1".equals(m.getIs_parent())) {
                    menuMap.put("submemus", getMenu(m.getMenu_id(), roleMenuList, userMenuList, user_no));
                }
                // 获取角色和用户的有权按钮
                //获取角色和用户的有权按钮
                /*menuMap.put("buttonList", getButton(user_no, m.getMenu_id()));*/
                retList.add(menuMap);
            }
        }
        return retList;
    }


}
