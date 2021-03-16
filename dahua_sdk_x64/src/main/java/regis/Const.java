package regis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;
import regis.http.client.MyScript;
import regis.http.client.RegisUtil;

@Slf4j
public class Const {
    private static String projectPath = "";

    public synchronized static String getProjectPath() {
        if (RegisUtil.isNotBlank(projectPath)) {
            return projectPath;
        } else {
            URL tmpURL = Thread.currentThread().getContextClassLoader().getResource("");
            String filePath = MyScript.getAppPath();
            if (tmpURL == null) {

            } else {
                filePath = String.valueOf(tmpURL) + "../../";
                // log.info("filePath {}", filePath);
                filePath = filePath.replaceAll("file:/", "");
                filePath = filePath.replaceAll("%20", " ");
                if (filePath.indexOf(":") != 1) {
                    filePath = File.separator + filePath;
                }
            }
            File tmpFile = Paths.get(filePath).toFile();
            // log.info("filePath is " + filePath);
            if (!tmpFile.exists() || !tmpFile.isDirectory()) {
                log.error("getProjectPath error [" + filePath + "]");
                projectPath = "";
            } else {
                String tmpStr = "";
                try {
                    tmpStr = tmpFile.getCanonicalPath();
                    projectPath = tmpStr;
                } catch (IOException e) {
                    log.error("getProjectPath error [" + filePath + "]", e);
                    projectPath = "";
                }
            }
            return projectPath;
        }
    }
}
