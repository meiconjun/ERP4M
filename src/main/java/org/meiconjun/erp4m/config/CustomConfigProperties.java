package org.meiconjun.erp4m.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 项目配置文件
 * @date 2021/1/30 20:54
 */
@Component
@ConfigurationProperties(prefix = "custom-config.custom")
@Data
public class CustomConfigProperties {
    //是否使用nginx文件服务 true or false 修改后 旧文件失效
    private boolean enabledNginx;
    // 项目文件存放根路径
    private String fileSavePath;
    // nginx代理文件存放根目录
    private String nginxFilePath;
    // nginx代理服务器IP
    private String nginxServerIp;
    // nginx代理服务器端口
    private String nginxServerPort;
    // nginx代理路径
    private String nginxServerRoot;

    // 新建用户初始密码
    private String defaultPassword;

    // 项目部署名称
    private String projectDeployedName;

    // 设定文件上传的最大值 50M
    private int maxUploadSize;
    // 设定文件上传时写入内存的最大值，如果小于这个参数不会生成临时文件，默认为10240
    private int maxInMemorySize;
    // 设定默认编码
    private String defaultEncoding;
}