package org.meiconjun.erp4m.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 数据库配置文件
 * @date 2021/1/30 20:39
 */
@Component
@ConfigurationProperties(prefix = "database-config")
@Setter
@Getter
@ToString
public class DatabaseProperties {
    private HashMap<String, Object> jdbc;
}
