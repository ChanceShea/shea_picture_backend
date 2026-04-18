package com.shea.picture.sheapicture.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * COS客户端配置类
 * @author : Shea.
 * @since : 2026/4/18 17:20
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    private String host;
    private String secretId;
    private String secretKey;
    private String region;
    private String bucket;

    @Bean
    public COSClient cosClient() {
        // 初始化用户身份信息
        COSCredentials cred = new BasicCOSCredentials(this.secretId, this.secretKey);
        // 设置bucket的地域
        Region region = new Region(this.region);
        ClientConfig config = new ClientConfig(region);
        // 建议使用https
        config.setHttpProtocol(HttpProtocol.https);
        // 生成cos客户端
        return new COSClient(cred, config);
    }
}
