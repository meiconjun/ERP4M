package org.meiconjun.erp4m.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.meiconjun.erp4m.bean.User;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: JWT工具类
 * @date 2021/9/16 16:46
 */
public class TokenUtil {
    private static Logger platformLogger = LogUtil.getPlatformLogger();
    private static Logger errorLogger = LogUtil.getExceptionLogger();

    private static String PRIMARY_KEY;

    static {
        platformLogger.info("初始化TOKEN私钥");
        try {
            InputStreamReader ir = new InputStreamReader(TokenUtil.class.getClassLoader().getResourceAsStream("key/TOKEN_PRIMARYKEY.key"));
            PRIMARY_KEY = IoUtil.read(ir);// 服务端私钥
            platformLogger.info("初始化TOKEN私钥成功");
        } catch (Exception e) {
            errorLogger.error("初始化TOKEN私钥失败", e);
        }
    }

    /**
     * 生成token
     * @param user 用户对象
     * @param valueTime token有效时间/毫秒
     * @return
     */
    public static String getToken(User user, int valueTime) {
        String token = "";
        Date expireDate = DateUtil.offsetMillisecond(new Date(), valueTime);
        token = JWT.create().withClaim("user", CommonUtil.objToJson(user)).withExpiresAt(expireDate).sign(Algorithm.HMAC256(PRIMARY_KEY));
        platformLogger.info("生成token成功, user_no[{}], token[{}]", user.getUser_no(), token);
        return token;
    }

    public static boolean verifyToken(String token) {
        boolean flag = true;
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(PRIMARY_KEY)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTDecodeException e) {
            flag = false;
        }
        return flag;
    }
}
