package regis.dh.paltform;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "platform")
@Setter
@Getter
public class PlatformAddress implements SmartInitializingSingleton {
    private static Logger logger = LoggerFactory.getLogger(PlatformAddress.class);
    private String host;
    private String path;

    @Value("${server.port}")
    private String localPort;


    private   void getConnectPlatform() throws UnknownHostException {
        String path =this.getPath();
        String host = this.getHost();
        String localHost= InetAddress.getLocalHost().getHostAddress();
        String platformUrl = host+path+"?proxyIp={proxyIp}&proxyPort={proxyPort}&category={category}";
        logger.info(platformUrl);
        Map<String, String> map = new HashMap<>();
        map.put("proxyIp",localHost);
        map.put("proxyPort",localPort);
        map.put("category","daHua");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(platformUrl,String.class,map);

    }


    @SneakyThrows
    @Override
    public void afterSingletonsInstantiated() {
        getConnectPlatform();
    }
    //private String port;




}
