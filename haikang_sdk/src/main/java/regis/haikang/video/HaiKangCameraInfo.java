package regis.haikang.video;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import regis.common.mongo.base.Identifier;

@Setter
@Getter
public class HaiKangCameraInfo extends Identifier<String> implements Serializable {
    /** 
     * @Fields serialVersionUID : TODO(ειε«δΉ)
     */
    private static final long serialVersionUID = 5899598077149013717L;

    String name;
    String indexCode;
    String regionIndexCode;
    String parentIndexCode;
    Integer cameraType;       // *******
    String decodeTag;
    String transType;
    String treatyType;
    String regionName;
    String regionPathName;
    String regionPath;
    String specialCode;




    String createTime;
    String updateTime;
}
