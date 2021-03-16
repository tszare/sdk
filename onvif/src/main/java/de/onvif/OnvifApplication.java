package de.onvif;


import de.onvif.platform.PlatformAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import regis.http.client.MyScript;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
//@SpringBootApplication
@EnableScheduling
@EnableAsync
@ServletComponentScan
public class OnvifApplication extends SpringBootServletInitializer {
    private static Logger logger = LoggerFactory.getLogger(OnvifApplication.class);

    //
    // @Autowired
    // protected FaceProperties faceProperties;
    // web.xml -- 打包成war时需要打开下方代码C
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OnvifApplication.class);

    }

    public static void main(String[] args) {

        MyScript.renewPidFile();
        ApplicationContext ctx = SpringApplication.run(OnvifApplication.class, args);
        PlatformAddress connectPlatform= ctx.getBean(PlatformAddress.class);
        // SpringContextUtil.setApplicationContext(ctx);
        logger.info("**************Below Is CmdServer Startup Info**************\n\n");


    }
}
