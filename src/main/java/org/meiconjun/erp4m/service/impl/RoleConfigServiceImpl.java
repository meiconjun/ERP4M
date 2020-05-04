package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.RoleBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.RoleConfigDao;
import org.meiconjun.erp4m.service.RoleConfigService;
import org.meiconjun.erp4m.util.CommonUtil;
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
 * @Title:
 * @Package
 * @Description: 角色管理业务服务
 * @date 2020/4/27 20:51
 */
@Service("roleConfigService")
@Transactional
public class RoleConfigServiceImpl implements RoleConfigService {
    private Logger logger = LoggerFactory.getLogger(RoleConfigServiceImpl.class);

    @Resource
    private RoleConfigDao roleConfigDao;

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("add".equals(operType)) {
            addOperation(requestBean, responseBean);
        } else if ("modify".equals(operType)) {
            modifyOperation(requestBean, responseBean);
        } else if ("delete".equals(operType)) {
            deleteOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    /**
     * 删除操作
     * @param requestBean
     * @param responseBean
     */
    private void deleteOperation(RequestBean requestBean, ResponseBean responseBean) {
        List<RoleBean> beanList = requestBean.getBeanList();

        for (RoleBean bean : beanList) {
            // 判断是否有关联此角色的用户
            int roleUserNum = roleConfigDao.selectRoleUserNum(bean);
            if (roleUserNum > 0) {
                throw new RuntimeException("角色号[" + bean.getRole_no() + "]存在关联的用户，清先删除用户或修改用户关联角色");
            }
            int effect = roleConfigDao.deleteRole(bean);
            if (effect < 1) {
                throw new RuntimeException("角色号[" + bean.getRole_no() + "]的角色删除失败");
            } else {
                logger.info("角色号[" + bean.getRole_no() + "]删除成功");
                //删除关联的权限信息
                roleConfigDao.deleteRoleRight(bean);
            }
        }
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }


    /**
     * 修改操作
     * @param requestBean
     * @param responseBean
     */
    private void modifyOperation(RequestBean requestBean, ResponseBean responseBean) {
        RoleBean bean = (RoleBean) requestBean.getBeanList().get(0);
        bean.setLast_modi_time(CommonUtil.getCurrentTimeStr());
        int effect = roleConfigDao.updateRole(bean);
        if (effect > 0) {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("修改失败，未知原因");
        }
    }

    /**
     * 新增操作
     * @param requestBean
     * @param responseBean
     */
    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        RoleBean bean = (RoleBean) requestBean.getBeanList().get(0);
        bean.setLast_modi_time(CommonUtil.getCurrentTimeStr());
        int effect = roleConfigDao.insertRole(bean);
        if (effect > 0) {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("新增失败，未知原因");
        }

    }

    /**
     * 查询角色信息
     * @param requestBean
     * @param responseBean
     */
    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        RoleBean bean = (RoleBean)requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量



        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("bean", bean);
        Page page = PageHelper.startPage(curPage, limit);
        List<RoleBean> roles = roleConfigDao.selectRoles(condMap);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", roles);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
