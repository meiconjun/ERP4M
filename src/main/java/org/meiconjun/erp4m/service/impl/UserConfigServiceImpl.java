package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.config.CustomConfigProperties;
import org.meiconjun.erp4m.dao.UserConfigDao;
import org.meiconjun.erp4m.service.UserConfigService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description:
 * @date 2020/5/5 15:07
 */
@Service("userConfigService")
@Transactional
public class UserConfigServiceImpl implements UserConfigService {
    private Logger logger = LoggerFactory.getLogger(UserConfigServiceImpl.class);

    @Resource
    private UserConfigDao userConfigDao;
    @Resource
    private CustomConfigProperties customConfigProperties;


    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("getRoleList".equals(operType)) {
            getRoleListOperation(requestBean, responseBean);
        } else if ("add".equals(operType)) {
            addOperation(requestBean, responseBean);
        } else if ("modify".equals(operType)) {
            modifyOperation(requestBean, responseBean);
        } else if ("getImgBase64".equals(operType)) {
            getImgBase64(requestBean, responseBean);
        } else if ("delete".equals(operType)) {
            deleteOperation(requestBean, responseBean);
        } else if ("deact".equals(operType)) {
            deactOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 启用/停用用户
     * @param requestBean
     * @param responseBean
     */
    private void deactOperation(RequestBean requestBean, ResponseBean responseBean) {
        User user = (User) requestBean.getBeanList().get(0);
        String status = "1";
        if ("1".equals(user.getStatus())) {
            status = "2";
        }
        user.setStatus(status);
        int effect = userConfigDao.updateUserStatus(user);
        if (effect == 0) {
            throw new RuntimeException("更新用户状态失败");
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    private void deleteOperation(RequestBean requestBean, ResponseBean responseBean) {
        List<User> beanList = requestBean.getBeanList();

        for (User user : beanList) {
            // 删除用户
            int effect = userConfigDao.deleteUser(user.getUser_no());
            if (effect < 1) {
                throw new RuntimeException("用户[" + user.getUser_no() + "]删除失败");
            }
            // 删除用户角色关联
            userConfigDao.deleteUserRoleOld(user.getUser_no());
            // 删除用户权限
            userConfigDao.deleteUserRight(user.getUser_no());
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    private void getImgBase64(RequestBean requestBean, ResponseBean responseBean) throws IOException {
        Map<String, Object> paramMap = requestBean.getParamMap();
        String imgPath = (String) paramMap.get("imgUrl");
        imgPath = customConfigProperties.getFileSavePath() + imgPath;

        File file = new File(imgPath);
        String imgBase64 = CommonUtil.fileToBase64(file);
        Map <String, Object> retMap = new HashMap<String, Object>();
        retMap.put("imgBase64", imgBase64);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 修改用户信息
     * @param requestBean
     * @param responseBean
     */
    private void modifyOperation(RequestBean requestBean, ResponseBean responseBean) throws IOException {
        User bean = (User) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = (Map<String, Object>) requestBean.getParamMap();
        bean.setLast_modi_time(CommonUtil.getCurrentTimeStr());

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("bean", bean);
        condMap.put("role_no", paramMap.get("role_no"));
        //修改用户信息
        userConfigDao.updateUser(bean);
        if (!CommonUtil.isStrBlank((String) paramMap.get("role_no"))) {
            //删除旧的用户角色信息
            userConfigDao.deleteUserRoleOld(bean.getUser_no());
            //更新用户角色信息
            userConfigDao.inserUserRole(condMap);
        }
        if (!CommonUtil.isStrBlank(bean.getPicture())) {
//            String url = customConfigProperties.getFileSavePath() + bean.getPicture();
//            File file = new File(url);
//            String imgBase64 = CommonUtil.fileToBase64(file);
            Map<String, Object> retMap = new HashMap<String, Object>();
            retMap.put("base64", bean.getPicture());
            responseBean.setRetMap(retMap);
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 新增用户操作
     * @param requestBean
     * @param responseBean
     */
    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        User bean = (User) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = (Map<String, Object>) requestBean.getParamMap();
        bean.setAuth_user(CommonUtil.getLoginUser().getUser_no());

        String defaultPsw = customConfigProperties.getDefaultPassword();
        if (CommonUtil.isStrBlank(defaultPsw)) {
            defaultPsw = "123456";
        }
        bean.setPass_word(defaultPsw);
        bean.setLast_modi_time(CommonUtil.getCurrentTimeStr());
        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("bean", bean);
        condMap.put("role_no", paramMap.get("role_no"));
        userConfigDao.insertUser(condMap);
        userConfigDao.inserUserRole(condMap);

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("defaultPsw", defaultPsw);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }

    /**
     * 获取角色列表
     * @param requestBean
     * @param responseBean
     */
    private void getRoleListOperation(RequestBean requestBean, ResponseBean responseBean) {
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        List<HashMap<String, String>> list = userConfigDao.selectRoles();
        retMap.put("list", list);
        responseBean.setRetMap(retMap);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询操作
     * @param requestBean
     * @param responseBean
     */
    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        User user = (User) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        String role_no = (String) paramMap.get("role_no");

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("bean", user);
        condMap.put("role_no", role_no);
        Page page = PageHelper.startPage(curPage, limit);

        List<HashMap<String, Object>> users = userConfigDao.selectUsers(condMap);
        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", users);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
