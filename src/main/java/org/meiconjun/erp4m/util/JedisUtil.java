package org.meiconjun.erp4m.util;

import org.meiconjun.erp4m.config.RedisProperties;
import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: Redis相关操作接口
 * @date 2020/11/8 20:22
 */
public class JedisUtil {

    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    private static RedisTemplate redisTemplate;
    private static RedisProperties redisProperties;

    static {
        redisTemplate = SpringContextHolder.getBean("redisTemplate");
        redisProperties = SpringContextHolder.getBean("redisProperties");
    }

    /**
     * 当前环境是否启用redis
     * @return
     */
    public static boolean isRedisEnable() {
        String redisEnable = redisProperties.getRedisEnable();
        if ("on".equals(redisEnable)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 是否存在key对应的缓存
     * @param key
     * @return
     */
    public static boolean isContainsKey(String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 存储字符串
     * @param key 键
     * @param value 值
     * @return
     */
    public static boolean setStringValue(String key, String value) {
        return setStringValue(key, value, 0);
    }
    /**
     * 存储字符串
     * @param key 键
     * @param value 值
     * @param time 过期时间 单位/秒
     * @return
     */
    public static boolean setStringValue(String key, String value, long time) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(key, value);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("-------------redis存储key[{}]失败，value[{}]，" + e.getMessage(), e, key, value);
        }
        return false;
    }

    /**
     * get String类型的缓存
     * @param key
     * @return
     */
    public static String getStrValue(String key) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch (Exception e) {
            logger.error("-------------获取redis缓存[{}]失败，" + e.getMessage(), e, key);
        }
        return null;
    }

    /**
     * 存储列表型数据
     * @param key
     * @param valueList
     * @return
     */
    public static boolean setListValue(String key, List valueList) {
        return setListValue(key, valueList, 0);
    }

    public static List getListValue(String key) {
        try {
            ListOperations listOperations = redisTemplate.opsForList();
            return (List) listOperations.range(key, 0, listOperations.size(key));
        } catch (Exception e) {
            logger.error("-------------获取redis缓存[{}]失败，" + e.getMessage(), e, key);
        }
        return null;
    }
    /**
     * 存储列表型数据
     * @param key
     * @param valueList
     * @param time 过期时间 单位/秒
     * @return
     */
    public static boolean setListValue(String key, List valueList, long time) {
        try {
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.rightPushAll(key, valueList);
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("-------------redis存储key[{}]失败，value[{}]，" + e.getMessage(), e, key, valueList);
        }
        return false;
    }
    /**
     * 移除键对应的值
     * @param key
     * @return
     */
    public static boolean removeValue(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            logger.error("-------------移除缓存失败[{}]，" + e.getMessage(), e, key);
        }
        return false;
    }
    /**
     * 根据前缀移除所有以传入前缀开头的key-value
     *
     * @param pattern
     * @return
     */
    public static boolean removeKeys(String pattern) {
        try {
            Set<String> keySet = redisTemplate.keys(pattern + "*");
            redisTemplate.delete(keySet);
            return true;
        } catch (Exception e) {
            logger.error("移除key[" + pattern + "]前缀的缓存时失败", "err[" + e.getMessage() + "]", e);
        }
        return false;
    }
}
