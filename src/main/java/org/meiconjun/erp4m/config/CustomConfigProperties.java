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
 * @Description: 项目配置文件
 * @date 2021/1/30 20:54
 */
@Component
@ConfigurationProperties(prefix = "custom-config")
@Setter
@Getter
@ToString
public class CustomConfigProperties {
    private HashMap<String, Object> custom;

    public HashMap<String, Object> getCustom() {
        return custom;
    };
}
