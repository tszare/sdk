package regis.haikang.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jnlp.ApiDialog;
import jdk.internal.org.objectweb.asm.Handle;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

import regis.haikang.API;
import lombok.extern.slf4j.Slf4j;
import regis.common.util.HandleResult;
import regis.haikang.root.ServerRoot;
import regis.haikang.video.HaiKangCameraInfo;
import regis.haikang.video.service.CameraInfoService;
import regis.http.client.RegisUtil;
import regis.xinrui.common.DataCheck;

@RestController
@RequestMapping(value = "/haiKang")
@Slf4j
public class ControlWeb {


    @Autowired
    CameraInfoService tmpDeviceService;

    private String getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                if (RegisUtil.isNotBlank(value)) {
                    res.put(en, value);
                }
            }
        }
        return RegisUtil.toJson(res);
    }



    @RequestMapping(value = "/getDeviceInfo", method = RequestMethod.GET)
    public HandleResult webGetDeviceInfo(HttpServletRequest request) {
        String specialCode= request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String json = RegisUtil.toJson(specificCamera);
        return HandleResult.success(json);
    }



    @RequestMapping(value = "/getAllDevice", method = RequestMethod.GET)
    public HandleResult webGetAllDevice() {
        List<HaiKangCameraInfo> haiKangCameraInfos= tmpDeviceService.findAll();
        if (haiKangCameraInfos.size()==0){
            String msg = "当前服务器暂无任何相机";
            return HandleResult.errorParam(msg);
        }
        JsonArray deviceSpecialCodeArray =new JsonArray();
        for (HaiKangCameraInfo haiKangCameraInfo:haiKangCameraInfos) {
            deviceSpecialCodeArray.add(haiKangCameraInfo.getSpecialCode());
        }

        log.info(String.valueOf(deviceSpecialCodeArray));
        return HandleResult.success(deviceSpecialCodeArray.toString());
    }

    @RequestMapping(value = "/getSnapshot", method = RequestMethod.GET)
    public HandleResult webGetSnapshot(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        String result = API.getSnapshot(indexCode);
        log.info(result);
        return HandleResult.success(result);
    }




    @RequestMapping(value = "/devicesInfoFormServer", method = RequestMethod.GET)
    public HandleResult webGetDeviceInfomation(HttpServletRequest request) {

        if (request.getParameter("pageNo")==null || request.getParameter("pageSize")==null) {
            String msg = String.format("输入正确的pageNo和pageSize");
            return HandleResult.errorParam(msg);
        }
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        API.devicesInfo(pageNo, pageSize);


        return HandleResult.success();


    }


    @RequestMapping(value = "/getStreamUrl", method = RequestMethod.GET)
    public HandleResult webGetStreamUrl(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera == null) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        Integer streamType = Integer.parseInt(request.getParameter("command"));

        return HandleResult.success(API.videoUrl(indexCode, streamType));
    }



    /**
     * PTZ 控制移动
     * command =0 右移
     * command =1 左移
     * command =2 上移
     * command =3 下移
     */

    @RequestMapping(value = "/move", method = RequestMethod.GET)
    public HandleResult webMoves(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        Integer direct = Integer.valueOf(request.getParameter("command"));
        String command;
        if (direct==0 ) { command = "RIGHT";
        }else if(direct==1){command = "LEFT";
        }else if(direct==2){command = "UP";
        }else if(direct==3){command = "DOWN";
        }else{
            String msg = String.format("direct[%s]，请输入正确的direct参数");
            return HandleResult.errorParam(msg);
        }
        API.ptzControl(indexCode, 0, command);
        return HandleResult.success();
    }

    /**
     * PTZ 控制
     * command =0 放大
     * command =1 缩小
     */


    @RequestMapping(value = "/zoom", method = RequestMethod.GET)
    public HandleResult webZooms(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        Integer direct = Integer.valueOf(request.getParameter("command"));
        String command;
        if (direct==0 ) { command = "ZOOM_IN";
        }else if(direct==1) { command = "ZOOM_OUT";
        }else{
            String msg = String.format("direct[%s]，请输入正确的direct参数");
            return HandleResult.errorParam(msg);
        }
        API.ptzControl(indexCode, 0, command);
        return HandleResult.success();
    }



    /**
     * 镜头聚焦
     * command =0 焦点前移
     * command =1 焦点后移
     */
    @RequestMapping(value = "/focus", method = RequestMethod.GET)
    public HandleResult webFocus(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        Integer direct = Integer.valueOf(request.getParameter("command"));
        String command;
        if (direct==0 ) { command = "FOCUS_NEAR";
        }else if(direct==1) { command = "FOCUS_FAR";
        }else{
            String msg = String.format("direct[%s]，请输入正确的direct参数");
            return HandleResult.errorParam(msg);
        }
        return HandleResult.success(API.ptzControl(indexCode, 0, command));
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public HandleResult webStop(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        API.ptzControl(indexCode, 1, "UP");
        return HandleResult.success();
    }


    @RequestMapping(value = "/getPreset", method = RequestMethod.GET)
    public HandleResult webGetpreset(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        return HandleResult.success(JSON.parseObject(API.getPresets(indexCode)).getString("data"));
    }


    @RequestMapping(value = "/setPreset", method = RequestMethod.GET)
    public HandleResult setPresets(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        String presetName = request.getParameter("command");
        Integer presetIndex = Integer.valueOf(request.getParameter("index"));
        String result =  API.setPreset(indexCode, presetName, presetIndex);
        return HandleResult.success(result);
    }

    @RequestMapping(value = "/gotoPreset", method = RequestMethod.GET)
    public HandleResult gotopresets(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        Integer presetIndex = Integer.valueOf(request.getParameter("command"));
        String result =  API.goToPreset(indexCode, presetIndex);
        return HandleResult.success(result);
    }

    @RequestMapping(value = "/delPreset", method = RequestMethod.GET)
    public HandleResult delpresets(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        Integer presetIndex = Integer.valueOf(request.getParameter("command"));
        String result = API.delPreset(indexCode, presetIndex);
        return HandleResult.success(result);
    }



    @RequestMapping(value = "/getOSD", method = RequestMethod.GET)
    public HandleResult getOSDs(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        String result =JSONObject.parseObject(API.getOSD(indexCode)).getString("data") ;
        return HandleResult.success(result);
    }


    /**
     * setType ==0，  显示时间
     * setType ==1,   显示通道名
     */
    @RequestMapping(value = "/setOSD", method = RequestMethod.GET)
    public HandleResult wenSetOSDs(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        int setType =Integer.parseInt(request.getParameter("command"));
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        JSONObject body = JSONObject.parseObject(API.getOSD(indexCode)).getJSONObject("data");
        if(setType==0&& body.getInteger("isShowOSD")==0){
            body.replace("isShowOSD","1");
            API.setOSD(body.toString());
        }
        if(setType==1&& body.getInteger("isShowChanName")==0){
            body.replace("isShowChanName","1");
            API.setOSD(body.toString());
        }
        return HandleResult.success();
    }





    /**
     * command = 0   是时间OSD的token
     * command = 1    是文本OSD的token
     */

    @RequestMapping(value = "/delOSD", method = RequestMethod.GET)
    public HandleResult webDelOSDs(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        int setType =Integer.parseInt(request.getParameter("command"));
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String indexCode = specificCamera.getIndexCode();
        JSONObject body = JSONObject.parseObject(API.getOSD(indexCode)).getJSONObject("data");
        if(setType==0&& body.getInteger("isShowOSD")==1){
            body.replace("isShowOSD","0");
            API.setOSD(body.toString());
        }
        if(setType==1&& body.getInteger("isShowChanName")==1){
            body.replace("isShowChanName","0");
            API.setOSD(body.toString());
        }
        return HandleResult.success();
    }






    @RequestMapping(value = "/setOSDName", method = RequestMethod.GET)
    public HandleResult setOSDnames(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        String indexCode = specificCamera.getIndexCode();
        JSONObject body = JSONObject.parseObject(API.getOSD(indexCode)).getJSONObject("data");

        body.replace("channelName", request.getParameter("command"));
        String newbody = body.toJSONString();
        API.setOSD(newbody);

        return HandleResult.success();
    }




    /**
     * moveTarget ==0      移动时间OSD
     * moveTarget  ==1     移动文本OSD
     *
     */

    @RequestMapping(value = "/moveOSD", method = RequestMethod.GET)
    public HandleResult setChannelpos(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        int moveTarget=  Integer.parseInt(request.getParameter("command"));
        HaiKangCameraInfo specificCamera= tmpDeviceService.find(specialCode);
        String indexCode = specificCamera.getIndexCode();
        JSONObject body = JSONObject.parseObject(API.getOSD(indexCode)).getJSONObject("data");
        if(moveTarget==1){
            body.replace("channelNameXPos", Integer.valueOf(request.getParameter("xPos")));
            body.replace("channelNameYPos", Integer.valueOf(request.getParameter("yPos")));
        }
        if(moveTarget==0){
            body.replace("osdXPos", Integer.valueOf(request.getParameter("xPos")));
            body.replace("osdYPos", Integer.valueOf(request.getParameter("yPos")));
        }

        String newbody = body.toJSONString();
        API.setOSD(newbody);

        return HandleResult.success();
    }





    @RequestMapping(value = "/dataModify",method = RequestMethod.GET)
    public HandleResult modifyDataProperty(HttpServletRequest request){
        String indexCode = request.getParameter("indexCode");
        String target = request.getParameter("target");
        String property = request.getParameter("property");
        tmpDeviceService.modify(indexCode,target,property);
        return HandleResult.success();
    }


    @RequestMapping(value="/dataResearch",method = RequestMethod.GET)
    public HandleResult researchData(HttpServletRequest request){
        String target = request.getParameter("target");
        String property = request.getParameter("property");
        Integer page = Integer.valueOf(request.getIntHeader("page")) ;
        Page defiInfo = tmpDeviceService.findConfuse(target,property,page);
        return HandleResult.success();


    }

    @RequestMapping(value ="/dataDelete",method = RequestMethod.GET)
    public HandleResult deleteData(HttpServletRequest request){
        String indexCode = request.getParameter("indexCode");
        tmpDeviceService.deleteInfo(indexCode);
        return HandleResult.success();
    }




    @RequestMapping(value ="/findAndSaveNewDev",method = RequestMethod.GET)
    public HandleResult NewDeviceWithoutDB(HttpServletRequest request){
        List<JSONObject> newDeviceList = tmpDeviceService.findAndSaveNewDev();
        return HandleResult.success();
    }


    @RequestMapping(value ="/bookingInfo",method = RequestMethod.POST)
    public HandleResult webbookingInfo(HttpServletRequest request) throws IOException {
        String paramJson = IOUtils.toString(request.getInputStream(), "UTF-8");
        String massage = "收到新的消息";
        log.info(massage);
        log.info(paramJson);
        return HandleResult.success("200Ok");
    }




    @RequestMapping(value ="/startBook",method = RequestMethod.POST)
    public HandleResult webbookingStar(HttpServletRequest request)  {
        int code = Integer.parseInt(request.getParameter("code"));
        int[] x={code};
        String result =API.Booking(x);
        String massage = "开启服务";
        log.info(massage+">>>>>>>>>>>>>"+result);
        return HandleResult.success("200Ok");
    }



}
