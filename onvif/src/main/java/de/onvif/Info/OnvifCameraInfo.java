package de.onvif.Info;

import de.onvif.mongo.base.Identifier;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

@Setter
@Getter
public class OnvifCameraInfo extends Identifier<String> implements Serializable {
    /** 
     * @Fields serialVersionUID : TODO(ειε«δΉ)
     */
    private static final long serialVersionUID = 5899598077149013717L;

    String user;
    String url;
    String password;
    String specialCode;
    String Manufacturer;
    String Model;
    String FirmwareVersion;
    String SerialNumber;
    String HardwareId;


}
