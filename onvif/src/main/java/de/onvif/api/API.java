package de.onvif.api;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jmx.snmp.Timestamp;
import com.sun.net.httpserver.HttpExchange;
import de.onvif.Info.OnvifCameraInfo;
import de.onvif.beans.DeviceInfo;
import de.onvif.discovery.DeviceDiscovery;
import de.onvif.mongo.dao.CameraInfoDao;
import de.onvif.mongo.service.CameraInfoService;
import de.onvif.soap.OnvifDevice;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.*;
import org.onvif.ver20.imaging.wsdl.ImagingPort;
import org.onvif.ver20.ptz.wsdl.PTZ;


import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import java.util.List;
import java.util.Random;
import java.util.TimeZone;


import org.onvif.ver10.schema.DateTime;
import org.onvif.ver10.schema.SetDateTimeType;
import org.opensaml.xmlsec.signature.J;
import org.springframework.beans.factory.annotation.Autowired;
import regis.http.client.MyScript;
import regis.http.client.RegisUtil;


import static de.onvif.api.Method.MoveSpeed;

@Slf4j
public class API {




    /**
     * 连接到设备
     *
     */

    @SneakyThrows
    public static String  connectDevice(String onvifUrl,String user, String password) {
        try {
            OnvifDevice device = new OnvifDevice(onvifUrl, user, password);
            return "connect success";
        } catch (IOException e1) {
            e1.getMessage();      //  *********加错误返回
            e1.printStackTrace();
        }
        return "connect refuse";
    }

    @SneakyThrows
    public static String  getVideoUrl(String onvifUrl,String user, String password,int streamType) {
        try {
            OnvifDevice device = new OnvifDevice(onvifUrl, user, password);
            String Url = device.getStreamUri(streamType);
            return Url;
        } catch (IOException e1) {
            e1.getMessage();      //  *********加错误返回
            e1.printStackTrace();
        }
        return "";

    }


    /**
     * 发现设备
     *
     */
    @SneakyThrows
    public static String deviceDiscovery() {
        List<String> list = DeviceDiscovery.discoverWsDevices();
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        for (java.lang.Object o : list) {

            String url = o.toString();
            array.add(url);
        }
        object.add("IP", array);
        try {
            if (object.size() == 0) {
                throw new IOException(" there is no device have been found, please check the internet connect ");
            }
        } catch (IOException e1) {
            e1.getMessage();      //  *********加错误返回
            e1.printStackTrace();
        }
        return object.toString();
    }





    @SneakyThrows
    public static String ptzControl(String url, String user,String password, int direct) {
        OnvifDevice device = new OnvifDevice(url, user, password);
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();
        PTZ ptzDevices = device.getPtz();
        Duration duration = DatatypeFactory.newInstance().newDuration("PT20S");
        if (direct==0){
            ptzDevices.continuousMove(profileToken,MoveSpeed(0.0f,0.0f,0.01f),duration);
            return "move out success";
        }else if (direct==1){
            ptzDevices.continuousMove(profileToken,MoveSpeed(0.0f,0.0f,-0.01f),duration);
            return "move in success";
        }else if (direct==2){
            ptzDevices.continuousMove(profileToken,MoveSpeed(0.2f,0.0f,0.0f),duration);
            return "move right success";
        }else if (direct==3){
            ptzDevices.continuousMove(profileToken,MoveSpeed(-0.2f,0.0f,0.0f),duration);

            return "move left success";
        }else if (direct==4){
            ptzDevices.continuousMove(profileToken,MoveSpeed(0.0f,0.2f,0.0f),duration);
            return "move up success";
        }else if (direct==5){
            ptzDevices.continuousMove(profileToken,MoveSpeed(0.0f,-0.2f,0.0f),duration);
            return "move down success";
        }
        return "please input correct parameter";
    }


    @SneakyThrows
    public static void  ptzStop(String url, String user, String password) {
        OnvifDevice device = new OnvifDevice(url, user, password);
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();

        PTZ ptzDevices = device.getPtz();
        ptzDevices.stop(profileToken,false,false);


    }


    @SneakyThrows
    public static String focus(String url, String user,String password,int direct){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        ImagingPort imaging = device.getImaging();

        List<VideoSource> videosources = media.getVideoSources();
        String videosourcesToken = videosources.get(0).getToken();
        FocusMove focusMove = new FocusMove();
        ContinuousFocus continuousFocus=new ContinuousFocus();
        if(direct==0){
            continuousFocus.setSpeed(-0.2f); //focus small
            focusMove.setContinuous(continuousFocus);
            imaging.move(videosourcesToken,focusMove);
            return "focus in successful";
        }else if(direct==1){
            continuousFocus.setSpeed(0.2f);
            focusMove.setContinuous(continuousFocus);
            imaging.move(videosourcesToken,focusMove);
            return " focus out successful";
        }
        return "please input correct parameter";

    }

    /**
     * 当前视频码流信息
     */

    @SneakyThrows
    public static String getEncodeInfo(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        List<Profile> profiles = device.getMedia().getProfiles();
        JsonArray currentEncodeInfo = new JsonArray();
        JsonObject encodingInfo= new JsonObject();



        for (int i = 0; i <profiles.size() ; i++) {
            JsonObject object=new JsonObject();
            VideoEncoderConfiguration encoderConfiguration = profiles.get(i).getVideoEncoderConfiguration();
            object.addProperty("码流"+i,encoderConfiguration.getEncoding().name());
            object.addProperty("Encording",encoderConfiguration.getEncoding().name());
            JsonObject resolution = new JsonObject();
            resolution.addProperty("Height",encoderConfiguration.getResolution().getHeight());
            resolution.addProperty("Width",encoderConfiguration.getResolution().getWidth());
            object.add("resolution",resolution);
            object.addProperty("Framerate",encoderConfiguration.getRateControl().getFrameRateLimit());
            object.addProperty("BitrateLimit",encoderConfiguration.getRateControl().getBitrateLimit());
            currentEncodeInfo.add(object);
        }
        encodingInfo.add("currentEncodeInfo",currentEncodeInfo);
        return encodingInfo.toString();

    }


    /**
     * 此接口返回的是设备当前所支持的码流调控范围
     */

    @SneakyThrows
    public static String encodeOptions(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();

        // ***** 获取当前摄像头支持的所有的编码格式及各种格式的相关参数修改范围   *******
        VideoEncoderConfigurationOptions videoEncoderConfigurationOptions=media.getVideoEncoderConfigurationOptions(null,profileToken);


        H264Options h264Options =  videoEncoderConfigurationOptions.getH264();

        log.info(RegisUtil.toJson(h264Options));

        return RegisUtil.toJson(h264Options);

    }



    /**
     * 设置视频的编码格式，steamType 0=H264，1=JPEG, 2=MPEG
     * streamOrder: 0 主码流， 1 辅码流
     */

    @SneakyThrows
    public static String setEncoder(String url, String user,String password,int streamOrder,int steamType){

        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        List<Profile> profiles = device.getMedia().getProfiles();
        VideoEncoderConfiguration encoderConfiguration = profiles.get(streamOrder).getVideoEncoderConfiguration();

        if (steamType==0){
            encoderConfiguration.setEncoding(VideoEncoding.H_264);
            media.setVideoEncoderConfiguration(encoderConfiguration,true);
            return "Set H264 success";
        }
        if (steamType==1){
            encoderConfiguration.setEncoding(VideoEncoding.JPEG);
            media.setVideoEncoderConfiguration(encoderConfiguration,true);
            return "Set JPEG success";
        }
        if (steamType==2){
            encoderConfiguration.setEncoding(VideoEncoding.MPEG_4);
            media.setVideoEncoderConfiguration(encoderConfiguration,true);
            return "Set MPEG success";
        }
        return "there are not any change, please input correct parameter";

    }





    /**
     * @setType  resolution:设置码流的分辨率; quality:设置视频质量； frameRate：设置帧率；  GOVLength: 设置GOV
     * streamOrder 0：主码流； 1：辅码流
     * Order :   设置参数的目标值（参考encodeInfo接口得到的参数范围）
     */

    @SneakyThrows
    public static String setVideoParameters(String url, String user,String password,String setType, int streamOrder,int order){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(streamOrder).getToken();
        List<VideoEncoderConfiguration> encoderConfigurations = media.getVideoEncoderConfigurations();
        VideoEncoderConfiguration encoderConfiguration= encoderConfigurations.get(streamOrder);
        VideoEncoderConfigurationOptions videoEncoderConfigurationOptions=media.getVideoEncoderConfigurationOptions(null,profileToken);
        if (videoEncoderConfigurationOptions.getH264()==null){
            return "current video encoding is not H264, please choose H264 and then do this command";
        }
        if(setType.equals("resolution")){
            VideoResolution resolution = videoEncoderConfigurationOptions.getH264().getResolutionsAvailable().get(order);
            encoderConfiguration.setResolution(resolution);
            log.info("设置分辨率为"+order);
        }

        if(setType.equals("quality")){

            encoderConfiguration.setQuality(order);
            log.info("设置视频质量为"+order);
        }

        if(setType.equals("frameRate")){

            encoderConfiguration.getRateControl().setFrameRateLimit(order);
            log.info("设置视频帧数为"+order);
        }

        if(setType.equals("GOVLength")){

            encoderConfiguration.getH264().setGovLength(order);
            log.info("设置GOV长度为"+order);
        }
        media.setVideoEncoderConfiguration(encoderConfiguration,true);
        return "设置成功";

    }



    /**
     * 预设点
     */

    @SneakyThrows
    public static void setPreset(String url, String user,String password,String name){
        OnvifDevice device = new OnvifDevice(url, user, password);

        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();
        PTZ ptzDevices = device.getPtz();
        ptzDevices.setPreset(profileToken,name,new Holder<String>());


    }

    @SneakyThrows
    public static String getPreset(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();
        PTZ ptzDevices = device.getPtz();
        List<PTZPreset> presets = ptzDevices.getPresets(profileToken);
        return RegisUtil.toJson(presets);

    }



    @SneakyThrows
    public static void delPreset(String url, String user,String password,String presetToken){
        OnvifDevice device = new OnvifDevice(url, user, password);
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();
        PTZ ptzDevices = device.getPtz();
        ptzDevices.removePreset(profileToken,presetToken);
    }


    @SneakyThrows
    public static void goToPreset(String url, String user,String password,String presetToken){
        OnvifDevice device = new OnvifDevice(url, user, password);
        List<Profile> profiles = device.getMedia().getProfiles();
        String profileToken = profiles.get(0).getToken();
        PTZ ptzDevices = device.getPtz();
        ptzDevices.gotoPreset(profileToken,presetToken,new PTZSpeed());
    }



    /**
     * 获取时间
     */

    @SneakyThrows
    public static String getDeviceTime(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        DateTime time = device.getDate();
        log.info(RegisUtil.toJson(time));
        return RegisUtil.toJson(time);
    }


    /**
     * 设置时间
     * data输入格式: 字符串YY-MM-DD
     * time输入格式: 字符串hh:mm:ss
     */

    @SneakyThrows
    public static void setDeviceTime(String url, String user,String password,String Data,String Time){
        OnvifDevice device = new OnvifDevice(url, user, password);

        LocalDate Data1 = LocalDate.parse(Data);
        int month= Data1.getMonthValue();
        int year= Data1.getYear();
        int day = Data1.getDayOfMonth();
        LocalTime Time1 = LocalTime.parse(Time);
        int hour = Time1.getHour();
        int min = Time1.getMinute();
        int second = Time1.getSecond();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date currentDate = new java.util.Date();
        boolean daylightSavings = calendar.getTimeZone().inDaylightTime(currentDate);
        org.onvif.ver10.schema.TimeZone timeZone = new org.onvif.ver10.schema.TimeZone();
        timeZone.setTZ("GMT+08:00");
        org.onvif.ver10.schema.Time time = new org.onvif.ver10.schema.Time();
        time.setHour(hour);
        time.setMinute(min);
        time.setSecond(second);

        org.onvif.ver10.schema.Date date = new org.onvif.ver10.schema.Date();
        date.setYear(year);
        date.setMonth(month);
        date.setDay(day);

        org.onvif.ver10.schema.DateTime utcDateTime = new org.onvif.ver10.schema.DateTime();
        utcDateTime.setDate(date);
        utcDateTime.setTime(time);

        device.getDevice().setSystemDateAndTime(SetDateTimeType.MANUAL, daylightSavings, timeZone, utcDateTime);


    }


    /**
     * 获取当前OSD
     */
    @SneakyThrows
    public static String getOSD(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        java.util.List<org.onvif.ver10.schema.OSDConfiguration> osdList = media.getOSDs(null);
        JSONArray dataArray = new JSONArray();
        JSONObject OSDData =new JSONObject();
        OSDData.put("isShowOSD",0);
        OSDData.put("osdXPos","");
        OSDData.put("osdYPos","");
        OSDData.put("timeToken","");
        OSDData.put("isShowChanName",0);
        OSDData.put("channelName","");
        OSDData.put("channelNameXPos","");
        OSDData.put("channelNameYPos","");
        OSDData.put("textToken","");
        for (OSDConfiguration osdConfiguration:osdList) {
            String exit = osdConfiguration.getTextString().getType();
            Vector position= osdConfiguration.getPosition().getPos();
            if(exit.equals("DateAndTime")){
                OSDData.replace("isShowOSD",1);
                OSDData.replace("osdXPos",position.getX());
                OSDData.replace("osdYPos",position.getY());
                OSDData.replace("timeToken",osdConfiguration.getToken());

            }else{
                OSDData.replace("isShowChanName",1);
                OSDData.replace("channelName",osdConfiguration.getTextString().getPlainText());
                OSDData.replace("channelNameXPos",position.getX());
                OSDData.replace("channelNameYPos",position.getY());
                OSDData.replace("textToken",osdConfiguration.getToken());
            }
            //dataArray.add(OSDData);
        }


        log.info(RegisUtil.toJson(dataArray)) ;
        return  OSDData.toString();

    }




    @SneakyThrows
    public static String setOSD(String url, String user,String password,int setType){
        Vector pos = new Vector();
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        VideoSourceConfiguration videoSourceConfiguration = media.getVideoSourceConfigurations().get(0);
        String videoSourceConfigurationtoken = videoSourceConfiguration.getToken();
        OSDConfiguration osdConfiguration = new OSDConfiguration();
        OSDTextConfiguration osdTextConfiguration = new OSDTextConfiguration();
        // ******* 设置具体字符串显示字符串内容
        if(setType==0){
            osdTextConfiguration.setTimeFormat("HH:mm:ss");
            osdTextConfiguration.setDateFormat("yyyy-MM-dd");
            osdTextConfiguration.setType("DateAndTime");
            pos.setX(-0.8f);
            pos.setY(-0.8f);
        }else{
            osdTextConfiguration.setPlainText("请输入需要显示的OSD文本");
            osdTextConfiguration.setType("Plain");
            pos.setX(-0.8f);
            pos.setY(0.8f);
        }
        osdConfiguration.setTextString(osdTextConfiguration);
        // **** 设置videosourseconfigurationtoken
        OSDReference osdReference = new OSDReference();
        osdReference.setValue(videoSourceConfigurationtoken);
        osdConfiguration.setVideoSourceConfigurationToken(osdReference);
        // ***** 设置显示类型
        osdConfiguration.setType(OSDType.TEXT);
        // **** 设置显示位置
        OSDPosConfiguration osdPosConfiguration = new OSDPosConfiguration();
        osdPosConfiguration.setPos(pos);
        osdPosConfiguration.setType("Custom");
        osdConfiguration.setPosition(osdPosConfiguration);
        //  *****设置 osdConfiguration的Token
        osdConfiguration.setToken("0");
        CreateOSD createOSD = new CreateOSD();
        createOSD.setOSD(osdConfiguration);

        return RegisUtil.toJson(media.createOSD(createOSD));



    }



    /**
     * 修改OSD显示字幕
     */
    @SneakyThrows
    public static void modifyOSD(String url, String user,String password,String token,String name){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        GetOSD getOSD=new GetOSD();
        getOSD.setOSDToken(token);
        GetOSDResponse getOSDResponse = media.getOSD(getOSD);
        OSDConfiguration osdConfiguration = getOSDResponse.getOSD();
        osdConfiguration.getTextString().setPlainText(name);
        SetOSD motify = new SetOSD();
        motify.setOSD(osdConfiguration);
        media.setOSD(motify);

    }


    /**
     * 修改OSD显示位置
     */
    @SneakyThrows
    public static void moveOSD(String url, String user,String password,String OSDToken,Float x,Float y){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Vector pos = new Vector();
        pos.setX(x);
        pos.setY(y);
        Media media = device.getMedia();
        GetOSD getOSD=new GetOSD();
        getOSD.setOSDToken(OSDToken);
        try{
            GetOSDResponse getOSDResponse = media.getOSD(getOSD);
            OSDConfiguration osdConfiguration = getOSDResponse.getOSD();
            osdConfiguration.getPosition().setPos(pos);
            SetOSD move = new SetOSD();
            move.setOSD(osdConfiguration);
            media.setOSD(move);
        }catch (SOAPFaultException as){
        }

    }

    /**
     * 删除OSD
     * osdtoken: OSD令牌，可由获取OSD列表是获得。
     */
    @SneakyThrows
    public static void delOSD(String url, String user,String password,String OSDToken){
        OnvifDevice device = new OnvifDevice(url, user, password);
        Media media = device.getMedia();
        DeleteOSD deleteOSD = new DeleteOSD();
        deleteOSD.setOSDToken(OSDToken);
        media.deleteOSD(deleteOSD);
    }
    /**
     * 重启设备
     */

    @SneakyThrows
    public static void reboot(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        device.reboot();

    }

    /**
     * 截图
     */

    @SneakyThrows
    public static String getSnapshot(String url, String user,String password){
        OnvifDevice device = new OnvifDevice(url, user, password);
        String picture = device.getSnapshotUri();
        return picture;

    }





    /**
     *添加新的onvif camera属性，
     * 配置相机的IP地址，用户名和登录密码，并配置一唯一标识specialCode
     *
     */

    @SneakyThrows
    public static OnvifCameraInfo saveOnvifData(String url, String user,String password){

        OnvifDevice device = new OnvifDevice(url, user, password);
        DeviceInfo info =  device.getDeviceInfo();
        OnvifCameraInfo onvifCameraInfo = new OnvifCameraInfo();

        onvifCameraInfo.setFirmwareVersion(info.getFirmwareVersion());
        onvifCameraInfo.setHardwareId(info.getHardwareId());
        onvifCameraInfo.setManufacturer(info.getManufacturer());
        onvifCameraInfo.setModel(info.getModel());
        onvifCameraInfo.setSerialNumber(info.getSerialNumber());
        onvifCameraInfo.setUser(user);
        onvifCameraInfo.setUrl(url);
        onvifCameraInfo.setPassword(password);


        Timestamp d = new Timestamp(System.currentTimeMillis());
        String codePart1 =String.valueOf(d.getSysUpTime());
        Random random = new Random();
        String codePart2 =String.valueOf(random.nextInt(100000));
        String codePart3="On";
        String specialCode = codePart3+codePart1+codePart2;
        onvifCameraInfo.setSpecialCode(specialCode);

        return onvifCameraInfo;

    }





}
