package regis.haikang.root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.hikvision.artemis.sdk.config.ArtemisConfig;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "root")
@Setter
@Getter
public class ServerRoot implements SmartInitializingSingleton {
    private static Logger logger = LoggerFactory.getLogger(ServerRoot.class);
    private String serverIp;
    private String appKey;
    private String appSecret;

    @Override
    public void afterSingletonsInstantiated() {
        ArtemisConfig.host = this.getServerIp();// 代理API网关nginx服务器ip端口
        ArtemisConfig.appKey = this.getAppKey();// 秘钥appkey
        ArtemisConfig.appSecret = this.getAppSecret();// 秘钥appSecret
        logger.info(">>>ArtemisConfig {} {} {}\n\n", ArtemisConfig.host, ArtemisConfig.appKey, ArtemisConfig.appSecret);

    }
}
