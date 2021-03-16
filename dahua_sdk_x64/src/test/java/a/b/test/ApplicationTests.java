package a.b.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;
import regis.DahuaApplication;

@RunWith(SpringRunner.class)
// 指定启动类
@SpringBootTest(classes = { DahuaApplication.class })
@WebAppConfiguration
@Slf4j
public class ApplicationTests {

    public void xmlTest2(String url, String userId) throws IOException {
    }

    @Test
    public void clearDataTest() throws IOException {
        String deviceId = "0814_0000_haikang";

        log.info("deviceId {}", deviceId);

    }

}
