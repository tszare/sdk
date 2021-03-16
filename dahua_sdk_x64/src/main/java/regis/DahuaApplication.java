package regis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;
import regis.dh.paltform.PlatformAddress;
import regis.http.client.MyScript;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
@EnableAsync
// @SpringBootApplication
@ServletComponentScan

@Slf4j





public class DahuaApplication extends SpringBootServletInitializer {

    // @Autowired
    // protected FaceProperties faceProperties;

    // web.xml -- 打包成war时需要打开下方代码
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DahuaApplication.class);
    }

    public static void main(String[] args) {

        MyScript.renewPidFile();
        ApplicationContext ctx = SpringApplication.run(DahuaApplication.class, args);

        PlatformAddress connectPlatform = ctx.getBean(PlatformAddress.class);


        // ApplicationContext ctx =
        // SpringApplication.run(DahuaApplication.class, args);
        // SpringContextUtil.setApplicationContext(ctx);
        log.info("**************Below Is CmdServer Startup Info**************\n\n");
        DahuaServer server = new DahuaServer(1);
        server.start();
    }
}
