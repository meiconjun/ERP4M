package org.meiconjun.erp4m.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 数据库配置文件
 * @date 2021/1/30 20:39
 */
@Component
@ConfigurationProperties(prefix = "database-config.jdbc")
@Data
public class DatabaseProperties {
    private String driver;

    private String url;

    private String username;

    private String password;
    // 别名方式，扩展插件，监控统计用的filter:stat，日志用的filter:log4j，防御sql注入的filter:wall
    private String filters;
    // 最大连接数
    private int maxActive;
    // 初始化连接数
    private int initialSize;
    // 获取连接最大等待时间
    private int maxWait;
    // 最小连接数
    private int minIdle;
    // 检测连接有效性的时间间隔
    private int timeBetweenEvictionRunsMillis;
    // 连接保持空闲而不被驱逐的最长时间
    private int minEvictableIdleTimeMillis;
    // 连接有效性，检测sql
    private String validationQuery;
    // 定时检测空闲连接有效性
    private boolean testWhileIdle;
    // 检测获取的连接的有效性
    private boolean testOnBorrow;
    // 检测要归还的连接的有效性
    private boolean testOnReturn;
    // 是否缓存preparedStatement，即PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
    private boolean poolPreparedStatements;
    //
    private int maxPoolPreparedStatementPerConnectionSize;
}
