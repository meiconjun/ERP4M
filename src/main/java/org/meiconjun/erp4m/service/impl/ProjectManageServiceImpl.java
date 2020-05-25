package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.ProjectManageDao;
import org.meiconjun.erp4m.service.ProjectManageService;
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
 * @date 2020/5/25 20:15
 */
@Service("projectManageService")
@Transactional
public class ProjectManageServiceImpl implements ProjectManageService {

    private Logger logger = LoggerFactory.getLogger(ProjectManageServiceImpl.class);

    @Resource
    private ProjectManageDao projectManageDao;

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
     * 查询操作
     * @param requestBean
     * @param responseBean
     */
    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        Map<String, String> beanMap = (Map<String, String>)requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();

        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        String project_name = beanMap.get("project_name");
        String chn_name = beanMap.get("chn_name");
        String principal = beanMap.get("principal");

        HashMap<String, Object> condMap = new HashMap<String, Object>();
        condMap.put("project_name", project_name);
        condMap.put("chn_name", chn_name);
        condMap.put("principal", principal);

        Page page = PageHelper.startPage(curPage, limit);

        List<HashMap<String, String>> list = projectManageDao.selectProjectInfo(condMap);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
