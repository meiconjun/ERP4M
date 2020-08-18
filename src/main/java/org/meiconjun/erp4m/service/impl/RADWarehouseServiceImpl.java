package org.meiconjun.erp4m.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.meiconjun.erp4m.bean.RADWarehouseBean;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.common.SystemContants;
import org.meiconjun.erp4m.dao.RADWarehouseDao;
import org.meiconjun.erp4m.service.RADWarehouseService;
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
 * @date 2020/8/16 23:40
 */
@Service("rADWarehouseService")
@Transactional
public class RADWarehouseServiceImpl implements RADWarehouseService {
    private Logger logger = LoggerFactory.getLogger(RADWarehouseServiceImpl.class);

    @Resource
    private RADWarehouseDao radWarehouseDao;

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

    /**
     * 录入研发仓流水
     * @param requestBean
     * @param responseBean
     */
    private void addOperation(RequestBean requestBean, ResponseBean responseBean) {
        RADWarehouseBean radWarehouseBean = (RADWarehouseBean) requestBean.getBeanList().get(0);
        radWarehouseBean.setSerial_no(SerialNumberGenerater.getInstance().generaterNextNumber());
        radWarehouseBean.setLast_modi_time(CommonUtil.getCurrentTimeStr());
        radWarehouseBean.setLast_modi_user(CommonUtil.getLoginUser().getUser_no());

        radWarehouseDao.insertRADWarehousrInfo(radWarehouseBean);
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
    }

    /**
     * 查询研发仓流水信息
     * @param requestBean
     * @param responseBean
     */
    private void queryOperation(RequestBean requestBean, ResponseBean responseBean) {
        RADWarehouseBean radWarehouseBean = (RADWarehouseBean) requestBean.getBeanList().get(0);
        Map<String, Object> paramMap = requestBean.getParamMap();
        String serial_no = radWarehouseBean.getSerial_no();
        String material_no = radWarehouseBean.getMaterial_no();
        String oper_type = radWarehouseBean.getOper_type();
        String date_begin = (String) paramMap.get("date_begin");
        String date_end = (String) paramMap.get("date_end");

        int curPage = Integer.parseInt((String) paramMap.get("curPage"));// 当前页码
        int limit = Integer.parseInt((String) paramMap.get("limit"));// 每页数量

        HashMap<String, String> condMap = new HashMap<>();
        condMap.put("serial_no", serial_no);
        condMap.put("material_no", material_no);
        condMap.put("oper_type", oper_type);
        condMap.put("date_begin", date_begin);
        condMap.put("date_end", date_end);

        Page page = PageHelper.startPage(curPage, limit);

        List<HashMap<String, String>> list = radWarehouseDao.selectRADWarehouseInfo(condMap);
        long total = page.getTotal();// 总条数
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("list", list);
        retMap.put("total", total);
        responseBean.setRetMsg("查询成功");
        responseBean.setRetCode(SystemContants.HANDLE_SUCCESS);
        responseBean.setRetMap(retMap);
    }
}
