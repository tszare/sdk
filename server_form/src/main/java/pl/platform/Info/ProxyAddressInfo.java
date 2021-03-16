package pl.platform.Info;

import lombok.Getter;
import lombok.Setter;
import pl.platform.mongo.base.Identifier;

import java.io.Serializable;



@Setter
@Getter
public class ProxyAddressInfo extends Identifier<String> implements Serializable {

    String proxyIp;
    String proxyPort;
    String name;
    String category;

}

