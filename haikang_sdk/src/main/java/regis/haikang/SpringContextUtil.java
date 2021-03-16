package regis.haikang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

public class SpringContextUtil {
    protected static final Log log = LogFactory.getLog(SpringContextUtil.class);

    private static ApplicationContext applicationContext;

    // 设置上下文
    public static void setApplicationContext(ApplicationContext applicationContext1) {
        // log.info("1000000000");
        applicationContext = applicationContext1;
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
