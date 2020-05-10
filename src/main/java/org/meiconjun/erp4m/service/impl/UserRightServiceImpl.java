package org.meiconjun.erp4m.service.impl;

import com.google.gson.reflect.TypeToken;
import org.meiconjun.erp4m.bean.*;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.UserRightDao;
import org.meiconjun.erp4m.service.UserRightService;
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
 * @Title:
 * @Package
 * @Description: 用户权限管理
 * @date 2020/5/10 19:01
 */
@Transactional
@Service("userRightService")
public class UserRightServiceImpl implements UserRightService {
    private Logger logger = LoggerFactory.getLogger(UserRightServiceImpl.class);

    @Resource
    UserRightDao userRightDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("getUserRight".equals(operType)) {
            getUserRightOperation(requestBean, responseBean);
        } else if ("updateRight".equals(operType)) {
            updateUserRight(requestBean, responseBean);
        }
        return responseBean;
    }

    private void updateUserRight(RequestBean requestBean, ResponseBean responseBean) {
        String user_no = ((User) requestBean.getBeanList().get(0)).getUser_no();
        Map<String, Object> paramMap = requestBean.getParamMap();
        String addRightStr = CommonUtil.objToJson(paramMap.get("addRight"));
        String delRightStr = CommonUtil.objToJson(paramMap.get("delRight"));
        HashMap<String, String> addRight = (HashMap<String, String>) CommonUtil.jsonToObj(addRightStr, new TypeToken<HashMap<String, String>>(){}.getType());
        HashMap<String, String> delRight = (HashMap<String, String>) CommonUtil.jsonToObj(delRightStr, new TypeToken<HashMap<String, String>>(){}.getType());
        for (String key : addRight.keySet()) {
            HashMap<String ,Object> condMap = new HashMap<String ,Object>();
            condMap.put("user_no", user_no);
            condMap.put("field_no", key);
            condMap.put("field_type", addRight.get(key));
            condMap.put("last_modi_time", CommonUtil.getCurrentTimeStr());
            userRightDao.insertUserRight(condMap);
        }
        for (String key : delRight.keySet()) {
            userRightDao.deleteUserRight(user_no, key);
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    private void getUserRightOperation(RequestBean requestBean, ResponseBean responseBean) {
        User user = (User) requestBean.getBeanList().get(0);
        List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        // 获取所有所有菜单和按钮，并构造成树结构参数
        List<MenuBean> allMenuList = userRightDao.selectAllMenu();
        for (int i = 3; i >= 1; i--) {// 设定最多只有三级菜单
            for (MenuBean menuBean : allMenuList) {
                if (Integer.valueOf(menuBean.getMenu_level()) == i) {
                    HashMap<String, Object> treeMap = new HashMap<String, Object>();
                    treeMap.put("parent_menu", menuBean.getParent_menu());
                    treeMap.put("name", menuBean.getMenu_name());
                    treeMap.put("field_no", menuBean.getMenu_id());
                    treeMap.put("field_type", "1");// 要素类型-菜单
                    treeMap.put("open", true);
                    // 判断是否有此菜单权限
                    int count = userRightDao.selectUserFieldRight(user.getUser_no(), menuBean.getMenu_id());
                    if (count == 1) {
                        treeMap.put("checked", true);
                    } else {
                        treeMap.put("checked", false);
                    }
                    // 获取菜单按钮列表
                    List<HashMap<String, Object>> childTreeList = new ArrayList<HashMap<String, Object>>();
                    List<ButtonBean> buttonList = userRightDao.selectBtnByMenuId(menuBean.getMenu_id());
                    for (ButtonBean buttonBean : buttonList) {
                        HashMap<String, Object> buttonTreeMap = new HashMap<String, Object>();
                        buttonTreeMap.put("name", buttonBean.getButton_name());
                        buttonTreeMap.put("field_no", buttonBean.getButton_id());
                        buttonTreeMap.put("field_type", "2");// 要素类型-按钮
                        buttonTreeMap.put("open", true);
                        // 判断是否有此按钮权限
                        count = userRightDao.selectUserFieldRight(user.getUser_no(), buttonBean.getButton_id());
                        if (count == 1) {
                            buttonTreeMap.put("checked", true);
                        } else {
                            buttonTreeMap.put("checked", false);
                        }
                        childTreeList.add(buttonTreeMap);
                    }

                    // 获取上一次循环插入的子节点，并放到本次循环Map中
                    for (int j = 0; j < dataList.size(); j++ ) {
                        HashMap<String, Object> tempMap = dataList.get(j);
                        String parent_menu = (String) tempMap.get("parent_menu");
                        if (menuBean.getMenu_id().equals(parent_menu)) {
                            childTreeList.add(tempMap);
                            dataList.remove(j);
                            j--;//IMPORTANT
                        }
                    }
                    if (!childTreeList.isEmpty()) {
                        treeMap.put("children", childTreeList);
                    }

                    // 插入dataList
                    dataList.add(treeMap);
                }
            }
        }
        logger.info("获取用户权限树成功:\n" + CommonUtil.objToJson(dataList));
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("dataList", dataList);
        responseBean.setRetMap(retMap);
    }
}
