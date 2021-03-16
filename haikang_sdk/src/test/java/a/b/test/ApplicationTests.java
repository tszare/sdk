package a.b.test;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;
import regis.haikang.Application;
import regis.haikang.video.HaiKangCameraInfo;

import regis.haikang.video.service.CameraInfoService;

@RunWith(SpringRunner.class)
// 指定启动类
@SpringBootTest(classes = { Application.class })

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
        HaiKangCameraInfo entity = new HaiKangCameraInfo();
        entity.setIndexCode("00dfbb23d17847e2979cdfd4aa92dfa3");
        tmpDeviceService.saveInfo(entity);
    }


    @Test
    public void xmlTest4()throws IOException{
        BigDecimal a = new BigDecimal(5);
        BigDecimal b = new BigDecimal(999);
        double c = a.divide(b,3, BigDecimal.ROUND_DOWN).doubleValue();





    }

    @Test
    public void xmlTest5()throws IOException{


        tmpDeviceService.findConfuse("name","动",1);

    }


    /**
     *
     *  生成了一千条数据来测试
     */
    @Test
    public void xmlTest7()throws IOException{


       // List allDev = tmpDeviceService.findConfuse("name","测试");

        for (int i = 0; i <1000 ; i++) {
            HaiKangCameraInfo entity = new HaiKangCameraInfo();
            entity.setSpecialCode(RandomStringUtils.randomNumeric(9));
            entity.setName("姓名");
            entity.setIndexCode(RandomStringUtils.randomNumeric(7));
            entity.setParentIndexCode(RandomStringUtils.randomNumeric(7));
            entity.setCameraType(1);
            entity.setDecodeTag(RandomStringUtils.randomNumeric(4));
            entity.setCreateTime(RandomStringUtils.randomNumeric(4));
            entity.setUpdateTime(RandomStringUtils.randomNumeric(4));
            entity.setTransType(RandomStringUtils.randomNumeric(4));
            entity.setTreatyType(RandomStringUtils.randomNumeric(4));
            entity.setRegionIndexCode(RandomStringUtils.randomNumeric(4));
            entity.setRegionName(RandomStringUtils.randomNumeric(4));
            entity.setRegionPath(RandomStringUtils.randomNumeric(4));
            entity.setRegionPathName(RandomStringUtils.randomNumeric(4));
            tmpDeviceService.create(entity);

        }

    }



















        Integer da = 3;
    }


