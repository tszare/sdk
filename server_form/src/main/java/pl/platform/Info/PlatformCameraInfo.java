package pl.platform.Info;

import lombok.Getter;
import lombok.Setter;
import pl.platform.mongo.base.Identifier;


import java.io.Serializable;

@Setter
@Getter
public class PlatformCameraInfo extends Identifier<String> implements Serializable{
    /**
     * @Fields serialVersionUID : TODO(ειε«δΉ)
     *
     */
    private static final long serialVersionUID = 5899598077149013717L;

    String specialCode;
    String proxyHost;
    String category;



}
