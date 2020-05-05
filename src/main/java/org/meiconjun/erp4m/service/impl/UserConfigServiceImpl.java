package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.UserConfigDao;
import org.meiconjun.erp4m.service.UserConfigService;
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
 * @Description:
 * @date 2020/5/5 15:07
 */
@Service("userConfigService")
@Transactional
public class UserConfigServiceImpl implements UserConfigService {
    private Logger logger = LoggerFactory.getLogger(UserConfigServiceImpl.class);

    @Resource
    private UserConfigDao userConfigDao;


    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("getRoleList".equals(operType)) {
            getRoleListOperation(requestBean, responseBean);
        }
        return responseBean;
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
