package regis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import regis.common.thread.CommonThread;

public class DahuaServer extends CommonThread {
    private static final Log log = LogFactory.getLog(DahuaServer.class);
    public static DahuaClient app = null;

    protected DahuaServer(int arg0) {
        super(arg0);

    }

    // public static

    @Override
    public void run() {
        app = new DahuaClient();
        app.OnCreate();// 初始化
        app.OnLogin();// 登陆
        app.LoadAllGroup();// 加载组织结构
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // do nothing
        } finally {
            app.OnLogout();
            app.OnDestroy();
            app = null;
        }

    }

}
