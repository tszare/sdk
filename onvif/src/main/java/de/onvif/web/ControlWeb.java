package de.onvif.web;


import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.onvif.Info.OnvifCameraInfo;
import de.onvif.api.API;
import de.onvif.mongo.service.CameraInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import regis.common.util.HandleResult;
import regis.http.client.RegisUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/onvif")
@Slf4j
public class ControlWeb {

    @Autowired
    CameraInfoService tmpDeviceService;

    /**
     *
     */

    @RequestMapping(value = "/getDeviceInfo", method = RequestMethod.GET)
    public HandleResult webGetDeviceInfo(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String json = RegisUtil.toJson(specificCamera);
        return HandleResult.success(json);
    }


    @RequestMapping(value = "/getAllDevice", method = RequestMethod.GET)
    public HandleResult webGetAllDevice() {
        List<OnvifCameraInfo> onvifCameraInfoList= tmpDeviceService.findAll();
        if (onvifCameraInfoList.size()==0){
            String msg = "当前服务器暂无任何相机";
            return HandleResult.errorParam(msg);
        }
        JsonArray deviceSpecialCodeArray =new JsonArray();
        for (OnvifCameraInfo onvifCameraInfo:onvifCameraInfoList) {
            deviceSpecialCodeArray.add(onvifCameraInfo.getSpecialCode());
        }

        log.info(String.valueOf(deviceSpecialCodeArray));
        return HandleResult.success(deviceSpecialCodeArray.toString());
    }


    @RequestMapping(value = "/getSnapshot", method = RequestMethod.GET)
    public HandleResult webGetSnapshot(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String picture= API.getSnapshot(url,user,password);
        return HandleResult.success(picture);
    }



    @RequestMapping(value = "/getStreamUrl", method = RequestMethod.GET)
    public HandleResult webGetStreamUrl(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        int streamType = Integer.parseInt(request.getParameter("command"));
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }

        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result = API.getVideoUrl(url,user,password,streamType);
        return HandleResult.success(result);
    }



    /**
     * PTZ 控制移动
     * direct =0 右移
     * direct =1 左移
     * direct =2 上移
     * direct =3 下移
     */
    @RequestMapping(value = "/move", method = RequestMethod.GET)
    public HandleResult webMove(HttpServletRequest request ) {

        int direct = Integer.parseInt(request.getParameter("command"))+2;

        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String command= API.ptzControl(url,user,password,direct);
        return HandleResult.success(command);
    }

    /**
     * PTZ 控制停止移动
     */

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public HandleResult webStop(HttpServletRequest request ) {

        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        API.ptzStop(url,user,password);
        return HandleResult.success();
    }

    /**
     * PTZ 控制
     * command =0 放大
     * command =1 缩小
     */

    @RequestMapping(value = "/zoom", method = RequestMethod.GET)
    public HandleResult webZoom(HttpServletRequest request ) {
        int direct = Integer.parseInt(request.getParameter("command"));
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result= API.ptzControl(url,user,password,direct);
        return HandleResult.success(result);
    }

    /**
     * 镜头聚焦
     * command =0 焦点前移
     * command =1 焦点后移
     */

    @RequestMapping(value = "/focus", method = RequestMethod.GET)
    public HandleResult webFocus(HttpServletRequest request ) {

        String specialCode= request.getParameter("specialCode");
        int direct = Integer.parseInt(request.getParameter("command"));
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result= API.focus(url,user,password,direct);
        return HandleResult.success(result);
    }


    @RequestMapping(value = "/getEncodeInfo", method = RequestMethod.GET)
    public HandleResult webGetEncodeInfo( HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result= API.getEncodeInfo(url,user,password);
        return HandleResult.success(result);
    }

    @RequestMapping(value = "/encodeOptions", method = RequestMethod.GET)
    public HandleResult webEncodeOptions(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result= API.encodeOptions(url,user,password);
        return HandleResult.success(result);
    }


    @RequestMapping(value = "/setEncoder", method = RequestMethod.GET)
    public HandleResult webSetEncoder(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        int streamOder = Integer.parseInt(request.getParameter("streamOrder"));
        int streamType = Integer.parseInt(request.getParameter("streamType"));
        String result= API.setEncoder(url,user,password,streamOder,streamType);
        return HandleResult.success(result);
    }


    @RequestMapping(value = "/setVideoParameters", method = RequestMethod.GET)
    public HandleResult webSetVideoParameters(HttpServletRequest request) {
        String setType = request.getParameter("setType");
        int streamOrder= Integer.parseInt(request.getParameter("streamOrder"));
        int order = Integer.parseInt(request.getParameter("order"));
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result= API.setVideoParameters(url,user,password,setType,streamOrder,order);
        return HandleResult.success(result);
    }



    @RequestMapping(value = "/getPreset", method = RequestMethod.GET)
    public HandleResult webGetPreset(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String presetList= API.getPreset(url,user,password);
        return HandleResult.success(presetList);
    }


    @RequestMapping(value = "/setPreset", method = RequestMethod.GET)
    public HandleResult webSetPreset(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String name = request.getParameter("command");
        API.setPreset(url,user,password,name);
        return HandleResult.success();
    }



    @RequestMapping(value = "/delPreset", method = RequestMethod.GET)
    public HandleResult webDelPreset(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String presetToken = request.getParameter("command");     //####   此处的command是预设点的编号
        API.delPreset(url,user,password,presetToken);
        return HandleResult.success();
    }

    @RequestMapping(value = "/goToPreset", method = RequestMethod.GET)
    public HandleResult webGotoPreset(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String presetToken = request.getParameter("command");
        API.goToPreset(url,user,password,presetToken);
        return HandleResult.success();
    }


    @RequestMapping(value = "/getDeviceTime", method = RequestMethod.GET)
    public HandleResult webGetDeviceTime(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result =API.getDeviceTime(url,user,password);
        return HandleResult.success(result);
    }




    @RequestMapping(value = "/setDeviceTime", method = RequestMethod.GET)
    public HandleResult webSetDeviceTime(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String data = request.getParameter("command");      //  command传递的是data
        String time = request.getParameter("index");        //  index传递的是time
        API.setDeviceTime(url,user,password,data,time);
        return HandleResult.success();
    }


    @RequestMapping(value = "/getOSD", method = RequestMethod.GET)
    public HandleResult webGetOSDs(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String result = API.getOSD(url,user,password);
        log.info(result);
        return HandleResult.success(result);
    }




    /**
     * setType ==0，  显示时间
     * setType ==1,   显示通道名
     */

    @RequestMapping(value = "/setOSD", method = RequestMethod.GET)

    public HandleResult webSetOSDs(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        int setType=Integer.parseInt(request.getParameter("command")) ;
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();

        JSONObject statues =JSONObject.parseObject(API.getOSD(url,user,password));

        if(statues.getInteger("isShowOSD")==1&&setType==0) {

            return HandleResult.success();
        }
        if(statues.getInteger("isShowChanName")==1&&setType==1) {

            return HandleResult.success();
        }


         String massage = API.setOSD(url,user,password,setType);





        return HandleResult.success(massage);
    }



    /**
     * isShowChanName==0,显示通道名
     * isShowOSD ==0，  显示时间
     * command      传入需要修改的OSD字符
     */



    @RequestMapping(value = "/setOSDName", method = RequestMethod.GET)

    public HandleResult webSetOSDName(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        String name =request.getParameter("command");

        String token =  JSONObject.parseObject(API.getOSD(url,user,password)).getString("textToken") ;
        if(token.equals("")){return HandleResult.errorResult("先创建文本OSD");
        }

        API.modifyOSD(url,user,password,token,name);

        return HandleResult.success();

    }



    /**
     * moveTarget ==0      移动时间OSD
     * moveTarget  ==1     移动文本OSD
     *
     */



    @RequestMapping(value = "/moveOSD", method = RequestMethod.GET)
    public HandleResult webMoveOSD(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        float x = Float.parseFloat(request.getParameter("xPos"));
        float y = Float.parseFloat(request.getParameter("yPos"));
        int moveTarget =Integer.parseInt(request.getParameter("command"));
        String OSDToken="";
        if(moveTarget ==1){
            OSDToken = JSONObject.parseObject(API.getOSD(url,user,password)).getString("textToken");

        }
        if(moveTarget ==0){
            log.info(API.getOSD(url,user,password));
            OSDToken = JSONObject.parseObject(API.getOSD(url,user,password)).getString("timeToken");
        }
        if(OSDToken.equals("")){return HandleResult.errorResult("先创建文本OSD");
        }
        API.moveOSD(url,user,password,OSDToken,x,y);
        return HandleResult.success();
    }

    /**
     * command==0, 是从getOSD中获取的token.
     */


    @RequestMapping(value = "/delOSD", method = RequestMethod.GET)
    public HandleResult webDelOSD(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        int setType =Integer.parseInt(request.getParameter("command"));
        String OSDToken="";
        if(setType ==1){
            OSDToken = JSONObject.parseObject(API.getOSD(url,user,password)).getString("textToken");
        }
        if(setType ==0){
            OSDToken = JSONObject.parseObject(API.getOSD(url,user,password)).getString("timeToken");
        }
        API.delOSD(url,user,password,OSDToken);
        return HandleResult.success();
    }






    @RequestMapping(value = "/reboot", method = RequestMethod.GET)
    public HandleResult webReboot(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        OnvifCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String url = specificCamera.getUrl();
        String user = specificCamera.getUser();
        String password = specificCamera.getPassword();
        API.reboot(url,user,password);
        return HandleResult.success();
    }


    @RequestMapping(value = "/deviceDiscovery", method = RequestMethod.GET)
    public HandleResult webDeviceDiscovery() {
        String result = API.deviceDiscovery();
        return HandleResult.success(result);
    }


    @RequestMapping(value = "/connectDevice", method = RequestMethod.GET)
    public HandleResult webConnectDevice(HttpServletRequest request) {
        String result =  API.connectDevice(request.getParameter("url"),request.getParameter("user"),request.getParameter("password"));
        return HandleResult.success(result);
    }







    //   !!!此处需要添加判断保存的相机中有没有同一个IP地址

    @RequestMapping(value = "/saveOnvifData", method = RequestMethod.GET)
    public HandleResult webSaveOnvifData(HttpServletRequest request) {
        String url = request.getParameter("url");
        String user = request.getParameter("user");
        String password =request.getParameter("password");

        OnvifCameraInfo onvifCameraInfo =  API.saveOnvifData(url,user,password);

        String result = tmpDeviceService.saveInfo(onvifCameraInfo);
        return HandleResult.success(result);
    }

}




