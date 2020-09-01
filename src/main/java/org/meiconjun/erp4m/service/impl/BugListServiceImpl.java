package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.BugBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.BugListDao;
import org.meiconjun.erp4m.service.BugListService;
import org.meiconjun.erp4m.util.CommonUtil;
import org.meiconjun.erp4m.util.SerialNumberGenerater;
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
 * @date 2020/8/22 22:24
 */
@Service("bugListServiceImpl")
@Transactional
public class BugListServiceImpl implements BugListService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private BugListDao bugListDao;


    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("query".equals(operType)) {
            queryOperation(requestBean, responseBean);
        } else if ("add".equals(operType)) {
            addOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        BugBean bugBean = (BugBean) requestBean.getBeanList().get(0);
        // 设置必要属性
        bugBean.setSerial_no(SerialNumberGenerater.getInstance().generaterNextNumber());
        bugBean.setBug_status("0");//BUG状态 0-未解决
        bugBean.setCreate_user(CommonUtil.getLoginUser().getUser_no());
        bugBean.setLast_modi_time(CommonUtil.getCurrentTimeStr());

        int effect = bugListDao.insertBugList(bugBean);
        if (effect > 0) {
            responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        } else {
            responseBean.setRetCode(SystemContants.HANDLE_FAIL);
            responseBean.setRetMsg("新增Bug信息失败");
        }
    }

    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        BugBean bugBean = (BugBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();

        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        Page page = PageHelper.startPage(curPage, limit);
        List<HashMap<String, Object>> list = bugListDao.selectBugList(bugBean);

        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("total", total);
        retMap.put("list", list);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
    
    
}
