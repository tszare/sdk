

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jmx.snmp.Timestamp;
import de.onvif.Info.OnvifCameraInfo;
import de.onvif.OnvifApplication;
import de.onvif.mongo.service.CameraInfoService;

import de.onvif.soap.OnvifDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onvif.ver10.media.wsdl.Media;
import org.onvif.ver10.schema.IntRange;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver10.schema.VideoEncoderConfigurationOptions;
import org.onvif.ver10.schema.VideoResolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

import javax.xml.soap.SOAPException;


@RunWith(SpringRunner.class)
// 指定启动类
@SpringBootTest(classes = { OnvifApplication.class })

@WebAppConfiguration

@Slf4j
public class ApplicationTests {

    @Autowired
    CameraInfoService tmpDeviceService;



    @Autowired
    protected MongoTemplate mongoTemplate;

    @Test
    public void modefi() throws IOException {
        String indexCode = "044ded39f8e249a980441919902c793f";
        String name = "name";
        String property = "早上出门在下大雨";

        CameraInfoService cameraInfoService = new CameraInfoService();

        cameraInfoService.modify(indexCode,name,property);
        // tmpDeviceService.modify(indexCode,name,property);

    }



    @Test
    public void xmlTest3()throws IOException{
        OnvifCameraInfo entity = new OnvifCameraInfo();
      //  entity.setIndexCode("编号89757");

        tmpDeviceService.saveInfo(entity);
    }


    @Test
    public void xmlTest4()throws IOException{
        BigDecimal a = new BigDecimal(5);
        BigDecimal b = new BigDecimal(999);
        double c = a.divide(b,3, BigDecimal.ROUND_DOWN).doubleValue();

    }


    @Test
    public void time()throws IOException{
//        Timestamp d = new Timestamp(System.currentTimeMillis());
//        log.info(String.valueOf(d.getSysUpTime()));
        Random random = new Random();
        for (int i = 0; i < 500; i++) {

            int c = random.nextInt(100000);
            log.info(String.valueOf(c));
        }







    }




    Integer da = 3;
}


