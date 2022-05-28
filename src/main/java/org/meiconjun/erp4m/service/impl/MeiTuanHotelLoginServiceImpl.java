package org.meiconjun.erp4m.service.impl;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.meiconjun.erp4m.bean.RequestBean;
import org.meiconjun.erp4m.bean.ResponseBean;
import org.meiconjun.erp4m.service.MeiTuanHotelLoginService;
import org.springframework.stereotype.Service;

/**
 * @ClassName MeiTuanHotelLoginServiceImpl
 * @Description TODO
 * @Author chunhui.lao
 * @Date 2022/5/28 15:08
 **/
@Slf4j
@AllArgsConstructor
@Service("meiTuanHotelLoginService")
public class MeiTuanHotelLoginServiceImpl implements MeiTuanHotelLoginService {

    @Override
    public ResponseBean excute(RequestBean requestBean) throws Exception {
        String operType = requestBean.getOperType();
        ResponseBean responseBean = new ResponseBean();
        if ("loginCheck".equals(operType)) {
            loginCheckOperation(requestBean, responseBean);
        } else if ("login".equals(operType)) {
            loginOperation(requestBean, responseBean);
        }
        return responseBean;
    }

    private void loginCheckOperation(RequestBean requestBean, ResponseBean responseBean) {

    }

    /**
     * 登录
     * @param requestBean
     * @param responseBean
     */
    private void loginOperation(RequestBean requestBean, ResponseBean responseBean) {
        // 通用头信息
        HashMap<String, String> baseHeader = new HashMap();
        baseHeader.put("X-Requested-With", "XMLHttpRequest");
        baseHeader.put("Accept-Encoding", "gzip, deflate, br");
        baseHeader.put("Accept", "application/json");
        baseHeader.put("Content-Type", "application/json;charset=UTF-8");
        baseHeader.put("Accept-Encoding", "gzip, deflate, br");
        baseHeader.put("Accept-Language", "zh-TW,zh-CN;q=0.9,zh;q=0.8,en;q=0.7");
        baseHeader.put("Host", "epassport.meituan.com");
        baseHeader.put("Connection", "keep-alive");
        baseHeader.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"101\", \"Google Chrome\";v=\"101\"");
        baseHeader.put("sec-ch-ua-platform", "\"Windows\"");
        baseHeader.put("Sec-Fetch-Dest", "empty");
        baseHeader.put("Sec-Fetch-Site", "same-origin");
        baseHeader.put("Sec-Fetch-Mode", "cors");
        // TODO step1
        HashMap<String, String> header1 = new HashMap();
        header1.putAll(baseHeader);
        header1.put("Referer", "https://epassport.meituan.com/account/unitivelogin?service=hotel&loginsource=14&noSignup=true&bg_source=4&loginurl=https%3A%2F%2Feb.meituan.com%2Febk%2Flogin%2Flogin.html&continue=https%3A%2F%2Feb.meituan.com%2Fgw%2Faccount%2Fbiz%2Fsettoken%3Fredirect_uri%3Dhttps%253A%252F%252Feb.meituan.com%252Febk%252Flogin%252Fsettoken.html%253Fredirect%253Dhttps%25253A%25252F%25252Feb.meituan.com%25252Febooking%25252Fnew-workbench%25252Findex.html");
        header1.put("Origin", "https://epassport.meituan.com");
        header1.put("Cookie", "_lxsdk_cuid=180fb31c2bac8-0da47a6232d-17333273-144000-180fb31c2bbc8; uuid=624636dff877484a7c65.1653481786.1.0.0; _lxsdk=180fb31c2bac8-0da47a6232d-17333273-144000-180fb31c2bbc8; WEBDFPID=1653552271988OEMKCUIfd79fef3d01d5e9aadc18ccd4d0c95072688-1653552271988-1653552271988OEMKCUIfd79fef3d01d5e9aadc18ccd4d0c95072688; _lx_utm=utm_source%3DBaidu%26utm_medium%3Dorganic; logan_session_token=61y1lr1u401tttmqk3j0; _lxsdk_s=180ff6553ce-e58-eb9-dd0%7C%7C3");

        String url1 = "https://epassport.meituan.com/api/account/login?service=hotel&bg_source=4&loginContinue=https:%2F%2Feb.meituan.com"
                + "%2Fgw%2Faccount%2Fbiz%2Fsettoken%3Fredirect_uri%3Dhttps%253A%252F%252Feb.meituan.com"
                + "%252Febk%252Flogin%252Fsettoken.html%253Fredirect%253Dhttps%25253A%25252F%25252Feb.meituan.com"
                + "%25252Febooking%25252Fnew-workbench%25252Findex.html&loginType=account";


        // TODO step2 判断是否需要短信验证

        // TODO step3 判断是否需要图形验证码
    }
}
