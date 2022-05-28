package org.meiconjun.erp4m.util;

import org.meiconjun.erp4m.config.RedisProperties;
import org.meiconjun.erp4m.interceptor.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
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
        return redisProperties.isRedisEnable();
    }

    /**
     * 开启事务
     */
    public static void multi() {
        redisTemplate.multi();
    }

    /**
     * 放弃事务
     */
    public static void discard() {
        redisTemplate.discard();
    }
    /**
     * 提交事务
     */
    public static void exec() {
        redisTemplate.exec();
    }

    /**
     * 监控事务操作的key，在事务中key被其他线程改变，则事务被放弃
     */
    public static void watch(Object key) {
        redisTemplate.watch(key);
    }

    /**
     * 放弃所有监控
     */
    public static void unWatch() {
        redisTemplate.unwatch();
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
            setExpire(key, time);
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
            setExpire(key, time);
            return true;
        } catch (Exception e) {
            logger.error("-------------redis存储key[{}]失败" + e.getMessage(), e, key);
        }
        return false;
    }

    /**
     * 存储Map型数据
     * @param key 代表整个Map的key
     * @param map map对象
     * @return
     */
    public static boolean setMapValue(String key, HashMap map) {
        return setMapValue(key, map, 0);
    }
    /**
     * 存储Map型数据
     * @param key 代表整个Map的key
     * @param map map对象
     * @param time 超时时间
     * @return
     */
    public static boolean setMapValue(String key, HashMap map, long time) {
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(key, map);
            setExpire(key, time);
            return true;
        } catch (Exception e) {
            logger.error("-------------redis存储key[{}]失败" + e.getMessage(), e, key);
        }
        return false;
    }

    /**
     * 缓存指定Map中的某个值
     * @param mapKey
     * @param key
     * @param value
     * @return
     */
    public static boolean setSpecificValueOfMap(String mapKey, String key, Object value) {
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put(mapKey, key, value);
            return true;
        } catch (Exception e) {
            logger.error("-------------redis存储key[{}]失败" + e.getMessage(), e, key);
        }
        return false;
    }

    /**
     * 获取整个Map
     * @param key
     * @return
     */
    public static HashMap getMapValue (String key) {
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            return (HashMap) hashOperations.entries(key);
        } catch (Exception e) {
            logger.error("-------------获取redis缓存[{}]失败，" + e.getMessage(), e, key);
        }
        return null;
    }

    /**
     * 获取指定map中的指定值
     * @param mapKey 缓存Map的key
     * @param valueKey Map中值的key
     * @return
     */
    public static Object getSpecificValueOfMap(String mapKey, String valueKey) {
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            Object object = hashOperations.get(mapKey, valueKey);
            logger.info("获取缓存成功");
            return object;
        } catch (Exception e) {
            logger.error("-------------获取redis缓存[{}]失败，" + e.getMessage(), e, mapKey);
        }
        return null;
    }


    /**
     * 设置缓存超时时间
     * @param key
     * @param time
     */
    private static void setExpire(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
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
