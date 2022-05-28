package org.meiconjun.erp4m.service;

import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;

/**
 * @ClassName MeiTuanHotelLoginService
 * @Description TODO
 * @Author chunhui.lao
 * @Date 2022/5/28 15:07
 **/
public interface MeiTuanHotelLoginService {
    ResponseBean excute(RequestBean requestBean) throws Exception;
}
