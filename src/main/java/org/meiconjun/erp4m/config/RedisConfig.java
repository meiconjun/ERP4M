package org.meiconjun.erp4m.config;

import org.meiconjun.erp4m.listener.RedisTestListener;
import org.meiconjun.erp4m.listener.TestConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: redis相关配置
 * @date 2021/1/31 21:22
 */
@Configuration
public class RedisConfig {
    @Resource
    private RedisProperties redisProperties;

    /**
     * jedis连接池配置
     * @return
     */
    @Bean(name = "poolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisProperties.getRedisPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisProperties.getRedisPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getRedisPoolMaxWaitMillis());
        return jedisPoolConfig;
    }

    /**
     * spring-data-redis2.0以上的配置
     */
    @Bean(name = "redisStandaloneConfiguration")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getRedisServerIp());
        redisStandaloneConfiguration.setPort(redisProperties.getRedisServerPort());
        return redisStandaloneConfiguration;
    }
    /**
     * 配置jedis链接工厂 spring-data-redis2.0中
     *             建议改为构造器传入一个RedisStandaloneConfiguration  单机
     *                                 RedisSentinelConfiguration  主从复制
     *                                 RedisClusterConfiguration  集群
     */
    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        return jedisConnectionFactory;
    }
    /**
     * 用于java与json的序列化和反序列化
     */
    @Bean(name = "jackson2JsonRedisSerializer")
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * 字符串的序列化
     */
    @Bean(name = "stringRedisSerializer")
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 映射序列化的java对象到redis
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory, StringRedisSerializer stringRedisSerializer,
                                       GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }

    /**
     * 所有键与值都是 String 类型的 RedisTemplate
     */
    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        return stringRedisTemplate;
    }
/*    @Bean(name = "redisTestListener")
    public RedisTestListener redisTestListener() {
        return new RedisTestListener();
    }*/

    @Bean(name= "messageListenerAdapter")
    public MessageListenerAdapter messageListenerAdapter(RedisSerializer<Object> redisSerializer, TestConsumer testConsumer) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(testConsumer, "consume");
        messageListenerAdapter.setSerializer(redisSerializer);
        return messageListenerAdapter;
    }


    @Bean(name = "redisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(MessageListenerAdapter messageListenerAdapter, StringRedisSerializer stringRedisSerializer, JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(jedisConnectionFactory);
        redisMessageListenerContainer.setTopicSerializer(stringRedisSerializer);
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new PatternTopic("redis/**"));
        return redisMessageListenerContainer;
    }
}
