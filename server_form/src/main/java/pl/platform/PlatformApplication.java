package pl.platform;


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
@EnableScheduling
@EnableAsync
@ServletComponentScan



public class PlatformApplication extends SpringBootServletInitializer {

    private static Logger logger = LoggerFactory.getLogger(PlatformApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PlatformApplication.class);

    }
    public static void main(String[] args) {

        MyScript.renewPidFile();
        ApplicationContext ctx = SpringApplication.run(PlatformApplication.class, args);
        // SpringContextUtil.setApplicationContext(ctx);
        logger.info("**************Below Is CmdServer Startup Info**************\n\n");



    }
}


