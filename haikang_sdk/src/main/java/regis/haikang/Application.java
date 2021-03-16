package regis.haikang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import regis.haikang.platform.PlatformAddress;
import regis.haikang.root.ServerRoot;
import regis.http.client.MyScript;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
// @SpringBootApplication
@EnableScheduling
@EnableAsync
@ServletComponentScan
public class Application extends SpringBootServletInitializer {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);

    }
//    @Autowired
//    ConnectPlatform connectPlatform;

    public static void main(String[] args) {

        MyScript.renewPidFile();

        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        ServerRoot rootConfig1 = ctx.getBean(ServerRoot.class);

        PlatformAddress connectPlatform = ctx.getBean(PlatformAddress.class);






        logger.info("**************Below Is CmdServer Startup Info**************\n\n");

    }
}
