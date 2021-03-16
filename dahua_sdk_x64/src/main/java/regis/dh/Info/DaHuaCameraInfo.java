package regis.dh.Info;

import regis.dh.mongo.base.Identifier;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DaHuaCameraInfo extends Identifier<String> implements Serializable {
//    /**
//     * @Fields serialVersionUID : TODO(变量含义)
//     */
//    private static final long serialVersionUID = 5899598077149013717L;

    String user;
    String DeviceID;

    String ParentCoding;

    String ParentName;
    String regionPath;

    String password;
    String specialCode;
    String Manufacturer;

    String Name;
    String ServerIP;
    String DeviceIP;
    String DevicePort;


}
