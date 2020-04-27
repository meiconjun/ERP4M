package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.RoleBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.RoleConfigDao;
import org.meiconjun.erp4m.service.RoleConfigService;
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
        }
        return responseBean;
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
