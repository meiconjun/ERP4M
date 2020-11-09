package org.meiconjun.erp4m.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: Redis连接池工具类(仅用于学习，后面直接用spring中的工具类替代)
 * @date 2020/11/5 22:58
 */
public class JedisPoolUtil {
    /**
     * redis连接池
     */
    private static JedisPool jedisPool;

    private static Logger logger = LoggerFactory.getLogger(JedisPoolUtil.class);
    /**
     * 连接池初始化方法，spring启动完成后执行
     */
//    @PostConstruct
    private void init() {
        logger.info("-----------------初始化Redis连接池----------------");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 设置最大空闲数
        jedisPoolConfig.setMaxIdle(Integer.valueOf(PropertiesUtil.getProperty("redisPoolMaxIdle")));
        // 设置最大连接数
        jedisPoolConfig.setMaxTotal(Integer.valueOf(PropertiesUtil.getProperty("redisPoolMaxTotal")));
        // 最大等待毫秒数
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(PropertiesUtil.getProperty("redisPoolMaxWaitMillis")));
        // redis服务器ip
        String redisIp = PropertiesUtil.getProperty("redisServerIp");
        logger.info("---------------获取Redis服务器IP[{}]--------------", redisIp);
        jedisPool = new JedisPool(jedisPoolConfig, redisIp);
    }

    /**
     * 从连接池中获取一个Redis连接
     * @return
     */
    public Jedis getRedisConnection() {
        return jedisPool.getResource();
    }
}
