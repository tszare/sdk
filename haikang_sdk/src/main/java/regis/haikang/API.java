package regis.haikang;


import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import regis.haikang.root.ServerRoot;


@Slf4j


public class API {
    /**
     * 请根据自己的appKey和appSecret更换static静态块中的三个参数. [1 host]
     * 如果你选择的是和现场环境对接,host要修改为现场环境的ip,https端口默认为443，http端口默认为80.例如10.33.25.22:443 或者10.33.25.22:80
     * appKey和appSecret请按照或得到的appKey和appSecret更改.
     * TODO 调用前先要清楚接口传入的是什么，是传入json就用doPostStringArtemis方法，下载图片doPostStringImgArtemis方法
     */



    private static final String ARTEMIS_PATH = "/artemis";



    public static String devicesInfo(Integer pageNo, Integer pageSize) {

        /**
         * 获取当前服务器的摄像头设备列表，这个获取的信息是从海康服务器中拉过来的信息
         * pageNo  当前页码，pageNo>0
         * pageSize 分页大小，0<pageNo<=1000
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/resource/v2/camera/search";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo", pageNo);
        jsonBody.put("pageSize", pageSize);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);


        return result;

    }


    public static String getSnapshot(String indexCode) {
        /**
         * 手动抓图
         * 根据监测点的唯一标识cameraIndexCode来进行手动抓图
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/manualCapture";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        String body = jsonBody.toJSONString();
        log.info("body {}", body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);


        return result;

    }

    public static String  ptzControl(String indexCode, Integer action, String command) {
        /**
         * 根据监控点编号进行云台操作
         * 根据监测点的唯一标识cameraIndexCode来控制指定设备
         * action 0移动 1停止
         * command：LEFT左转 RIGHT，UP，DOWN，
         * ZOOM_IN 焦距变大 ZOOM_OUT 焦距变小
         * LEFT_UP，LEFT_DOWN 左下 RIGHT_UP 右上 RIGHT_DOWN 右下
         * FOCUS_NEAR 焦点前移 FOCUS_FAR 焦点后移 IRIS_ENLARGE 光圈扩大 IRIS_REDUCE 光圈缩小
         * speed  云台速度，取值范围为1-100，默认50
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/ptzs/controlling";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        jsonBody.put("action", action);
        jsonBody.put("command", command);
        jsonBody.put("speed", 4);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);
        return result;

    }

    public static String nvrInfo(Integer pageNo, Integer pageSize) {
        /**
         * 获取当前服务器的编码设备列表，
         * pageNo  当前页码，pageNo>0
         * pageSize 分页大小，0<pageNo<=1000
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/resource/v1/encodeDevice/get";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo", pageNo);
        jsonBody.put("pageSize", pageSize);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);
        return result;

    }




    public static String videoUrl(String indexCode, Integer streamType) {
        /**
         * 获取监控点预览取流URL
         * cameraIndexCode
         * streamType 码流类型，0:主码流 1:子码流 2:第三码流 参数不填，默认为主码流
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v2/cameras/previewURLs";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        jsonBody.put("streamType", streamType);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);
        return result;

    }

    public static String setPreset(String indexCode, String presetName, Integer presetIndex) {
        /**
         * 设置预置点信息
         * cameraIndexCode
         * presetName 预设点名字
         * presetIndex 预设点编号
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/presets/addition";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        jsonBody.put("presetName", presetName);
        jsonBody.put("presetIndex", presetIndex);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);

        return result;

    }

    public static String goToPreset(String indexCode, Integer presetIndex) {
        /**
         * 移动到预设点
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/ptzs/controlling";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        jsonBody.put("action", 1);
        jsonBody.put("command", "GOTO_PRESET");
        jsonBody.put("presetIndex", presetIndex);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);
        return result;
    }

    public static String  delPreset(String indexCode, Integer presetIndex) {
        /**
         * 删除指定预设点
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/presets/deletion";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        jsonBody.put("presetIndex", presetIndex);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);

        return result;
    }

    public static String getPresets(String indexCode) {
        /**
         * 获取当前检测点的预设点
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/presets/searches";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);
        return result;

    }

    public static String getOSD(String indexCode) {
        /**
         * 获取当前检测点的OSD
         *
         */
        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/picParams/get";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", indexCode);
        String body = jsonBody.toJSONString();
        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);

        log.info(result);
        return result;
    }

    public static String setOSD(String body) {

        /**
         * 设置当前检测点的OSD
         */

        final String VechicleDataApi = ARTEMIS_PATH + "/api/video/v1/picParams/udpate";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        log.info("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);

        return result;
    }

    public static Integer totalNum(){
        String info = API.devicesInfo(1, 2);

        JSONObject cemeraData = JSONObject.parseObject(info).getJSONObject("data");
        Integer tatalNum =Integer.valueOf(String.valueOf(cemeraData.get("total")));
        return tatalNum;
    }


    public static JSONObject getBody() {

        /**
         * 设置当前检测点的OSD
         * channelName 通道名称，isShowChanName 是否显示通道名。
         * channelNameXPos，channelNameYPos 通道名显示的坐标，建议值在0-500,(默认456,496)
         * hourOSDType，0表示24小时制，1表示am/pm  （默认24小时制）
         * isShowOSD,osdXPos,osdYPos OSD是否显示和显示的位置  （默认显示，位置16，16,）
         * isShowWeek  是否显示星期   （默认不显示）
         */
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode", 0);
        jsonBody.put("channelName", "");
        jsonBody.put("isShowChanName", 0);
        jsonBody.put("channelNameXPos", 456);
        jsonBody.put("channelNameYPos", 496);
        jsonBody.put("hourOSDType", 0);
        jsonBody.put("isShowOSD", 1);
        jsonBody.put("osdXPos", 16);
        jsonBody.put("osdYPos", 16);
        jsonBody.put("osdType", 1);
        jsonBody.put("osdAttrib", 2);
        jsonBody.put("isShowWeek", 0);

        return jsonBody;
    }


    /**
     * 事件订阅
     *
     * @param bookCode
     */
    public static String Booking(int[] bookCode){


        final String VechicleDataApi = ARTEMIS_PATH + "/api/eventService/v1/eventSubscriptionByEventTypes";

        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", VechicleDataApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("eventTypes", bookCode);
        jsonBody.put("eventDest","http://192.168.14.211:8002/haiKang/bookingInfo");

        String body = jsonBody.toJSONString();

        System.out.println("body: " + body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json", null);
        return result;


    }

//    public static void main(String[] args) {
//        int[] x={132612};
//        String result = API.Booking(x);
//        log.info(result);
//    }


}