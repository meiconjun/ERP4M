package org.meiconjun.erp4m.constant;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * @ClassName LocalCacheConstants
 * @Description 本地内存缓存
 * @Author chunhui.lao
 * @Date 2022/5/28 15:13
 **/
public class LocalCacheConstants {
    /**
     * 美团酒店登录请求头信息 key为登录用户号
     */
    private static Map<String, Map<String, String>> meiTuanHotelheaderCache = new HashMap<>();

    /**
     * 获取美团酒店登录请求头信息
     * @param userNo 美团酒店登录用户号
     * @return
     */
    public Map<String, String> getMeiTuanHotelHeader(@NotNull String userNo) {
        return meiTuanHotelheaderCache.get(userNo);
    }

}
