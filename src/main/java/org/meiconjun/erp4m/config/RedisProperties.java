package org.meiconjun.erp4m.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: Redis配置文件
 * @date 2021/1/30 20:51
 */
@Component
@ConfigurationProperties(prefix = "redis-config.redis")
@Data
public class RedisProperties {
    // 是否启用redis on-是 其它-否
    private boolean redisEnable;
    // 最大空闲数
    private int redisPoolMaxIdle;
    // 最大连接数
    private int redisPoolMaxTotal;
    // 最大等待毫秒数
    private int redisPoolMaxWaitMillis;
    // redis服务器IP
    private String redisServerIp;
    // redis服务器端口
    private int redisServerPort;
}
