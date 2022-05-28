package org.meiconjun.erp4m.util;

import org.meiconjun.erp4m.bean.User;
import org.meiconjun.erp4m.common.CacheConstants;
import org.meiconjun.erp4m.dao.UserConfigDao;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 缓存数据读写封装
 * @date 2021/9/22 21:55
 */
@Component("cacheUtil")
@Transactional // 需要保证数据一致性 出错时缓存和数据库都要回滚
public class CacheUtil {
    private static Logger platformLogger = LogUtil.getPlatformLogger();

    private static Logger businessLogger = LogUtil.getBusinessLogger();

    private static Logger errorLogger = LogUtil.getExceptionLogger();

    @Resource
    private UserConfigDao userConfigDao;

    /**
     * 是否启用Redis缓存
     */
    private Boolean isEnabledRedis = null;

    private boolean isEnabledRedis() {
        if (isEnabledRedis == null) {
            isEnabledRedis = JedisUtil.isRedisEnable();
        }
        return isEnabledRedis;
    }

    /**
     * 设置用户缓存
     * @param user 用户对象
     */
    public void setUserCache(User user) {
        /** 写：先查cache，cache中不存在，直接更新DB；cache中存在，则先更新cache，然后cache服务自己更新DB（同步更新cache和db）*/
        if (isEnabledRedis()) {
            JedisUtil.watch(CacheConstants.USER_CACHE);
            JedisUtil.multi();// 开启事务
            if (JedisUtil.isContainsKey(CacheConstants.USER_CACHE)) {
                User cacheUser = (User) JedisUtil.getSpecificValueOfMap(CacheConstants.USER_CACHE, user.getUser_no());
                if (cacheUser != null) {
                    JedisUtil.setSpecificValueOfMap(CacheConstants.USER_CACHE, user.getUser_no(), user);
                }
            }
        }
        // 更新db
        try {
            userConfigDao.mergeUser(user);
        } catch (Exception e) {
            if (isEnabledRedis()) {
                JedisUtil.discard();//放弃事务
            }
            throw e;
        }
        if (isEnabledRedis()) {
            JedisUtil.exec();// 提交（执行）redis事务,在exec中发生错误的操作将被跳过，其他操作依然会成功，所以不满足原子性；而在调用exec之前发生错误则整个事务丢弃
        }
    }

    /**
     * 根据用户号获取单个用户缓存
     * @param user_no
     * @return
     */
    public User getSingleUserCache(String user_no) {
        /*读：从cache中读取数据，读取到则直接返回；读取不到的话，先从DB加载，写入到cache后返回响应*/
        User user = null;
        if (isEnabledRedis()) {
            if (JedisUtil.isContainsKey(CacheConstants.USER_CACHE)) {
                user = (User) JedisUtil.getSpecificValueOfMap(CacheConstants.USER_CACHE, user_no);
            }
        }
        if (user == null) {
            user = userConfigDao.selectUserByNo(user_no);
            if (isEnabledRedis()) {
                JedisUtil.setSpecificValueOfMap(CacheConstants.USER_CACHE, user_no, user);
            }
        }
        return user;
    }

    /**
     * 获取全量用户缓存
     * @return
     */
    /*public HashMap<String, User> getAllUserCache() {
        if (isEnabledRedis) {
            if (JedisUtil.isContainsKey(CacheConstants.USER_CACHE)) {

            }
        }
        return null;
    }*/
}
