package regis.web;

import javax.servlet.http.HttpServletRequest;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import org.dom4j.DocumentException;
import regis.dh.Info.DaHuaCameraInfo;
import regis.dh.mongo.service.DHDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import regis.DahuaClient;
import regis.DahuaServer;
import regis.common.util.HandleResult;
import regis.http.client.RegisUtil;

import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping(value = "/daHua")
@Slf4j
public class ControlWeb {

    @Autowired
    DHDataService tmpDeviceService;


    @RequestMapping(value = "/getDeviceInfo", method = RequestMethod.GET)
    public HandleResult GetDeviceInfo(HttpServletRequest request) throws IllegalAccessException {

        String specialCode = request.getParameter("specialCode");

        DaHuaCameraInfo specificCamera=tmpDeviceService.find(specialCode);
        if (specificCamera==null){
            String msg = "未找到该相机，请先添加设备";
            return HandleResult.errorParam(msg);
        }
        String json = RegisUtil.toJson(specificCamera);

        return HandleResult.success(json);
    }


    @RequestMapping(value = "/getAllDevice", method = RequestMethod.GET)
    public HandleResult webGetAllDevice() {
        List<DaHuaCameraInfo> daHuaCameraInfoList= tmpDeviceService.findAll();
        if (daHuaCameraInfoList.size()==0){
            String msg = "当前服务器暂无任何相机";
            return HandleResult.errorParam(msg);
        }
        JsonArray deviceSpecialCodeArray =new JsonArray();
        for (DaHuaCameraInfo daHuaCameraInfo:daHuaCameraInfoList) {
            deviceSpecialCodeArray.add(daHuaCameraInfo.getSpecialCode());
        }

        log.info(String.valueOf(deviceSpecialCodeArray));
        return HandleResult.success(deviceSpecialCodeArray.toString());
    }



    /**
     * @param request
     * @return HandlerResult
     * @description: 根据deviceId与cameraId获取视频截图
     */
    @RequestMapping(value = "/getSnapshot", method = RequestMethod.GET)
    public HandleResult webGetSnapshot(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode"); 
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strAlarmCameraID=specificCamera.getDeviceID();
        DahuaClient app = DahuaServer.app;
        app.getSnapshot(m_strAlarmCameraID);
        log.info("the picture saved at '/tmp/1.jpg'");
        return HandleResult.success("the picture saved at '/tmp/1.jpg'");
    }

    /**
     * @param request
     * @return HandlerResult
     * @description: 根据deviceId获取rtsp地址
     */
    @RequestMapping(value = "/getStreamUrl", method = RequestMethod.GET)
    public HandleResult webGetStreamUrl(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        int streamType = Integer.parseInt(request.getParameter("command"))+1 ;
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";
        DahuaClient app = DahuaServer.app;
        String result = app.GetRealStreamUrl(m_strRealCameraID,streamType);
        app.GetExternUrl(m_strRealCameraID);
        return HandleResult.success(result);
    }



    /**
     *
     * @description: 根据deviceId与Direction来移动摄像头
     *   command        = 0; 右
     *   command        = 1; 左
     *   command        = 2; 上
     *   command        = 3; 下

     */
    @RequestMapping(value = "/move", method = RequestMethod.GET)
    public HandleResult webMove(HttpServletRequest request) {

        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";
        int direct = Integer.parseInt(request.getParameter("command"));
        int realDirect;
        if(direct==0){realDirect =4;
        }else if(direct==1){realDirect =3;
        }else if(direct==2){realDirect=1;
        }else if(direct==3){realDirect =2;
        }else{
            String msg = String.format("direct[%s]，输入正确的方向", direct);
            return HandleResult.errorParam(msg);
        }
        DahuaClient app = DahuaServer.app;
        app.ptzMove(m_strRealCameraID, realDirect);
        return HandleResult.success();
    }

    /**
     * 根据相机唯一识别号去设备停止移动
     */
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public HandleResult webStop(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";

        DahuaClient app = DahuaServer.app;
        app.ptzStop(m_strRealCameraID);
        return HandleResult.success();
    }

    /**
     * @param request
     * @return HandlerResult
     * command =0 焦点前移
     * command =1 焦点后移
     */
    @RequestMapping(value = "/focus", method = RequestMethod.GET)
    public HandleResult webFocus(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";

        int direct = Integer.parseInt(request.getParameter("command"));
        int command;
        if(direct==0){command=1;
        }else if(direct==1){command=4;
        }else{
            String msg = String.format("direct[%s]，输入参数", direct);
            return HandleResult.errorParam(msg);
        }

        DahuaClient app = DahuaServer.app;

        app.focusZoom(m_strRealCameraID, command);
        return HandleResult.success();
    }



    /**
     * @param request
     * @return HandlerResult
     * command =0 放大
     * command =1 缩小
     */
    @RequestMapping(value = "/zoom", method = RequestMethod.GET)
    public HandleResult webZoom(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";

        int direct = Integer.parseInt(request.getParameter("command"));
        int command;
        if(direct==0){command=0;
        }else if(direct==1){command=3;
        }else {
            String msg = String.format("direct[%s]，输入参数", direct);
            return HandleResult.errorParam(msg);
        }
        DahuaClient app = DahuaServer.app;

        app.focusZoom(m_strRealCameraID, command);
        return HandleResult.success();
    }


    /**
     * @param request
     * @return HandlerResult
     * @description: 根据deviceId获取当前预设点的信息
     */
    @RequestMapping(value = "/getPreset", method = RequestMethod.GET)
    public HandleResult PresetInformation(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";
        DahuaClient app = DahuaServer.app;
        return HandleResult.success(app.getPreset(m_strRealCameraID));
    }

    /**
     * @param request
     * presetName   新增的预设点名字
     * presetIndex  预设点编号
     * @return HandlerResult
     * @description: 根据deviceId，presetName和presetIndex来增加预设点
     */
    @RequestMapping(value = "/setPreset", method = RequestMethod.GET)
    public HandleResult webSetPreset(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";
        String presetName = request.getParameter("command");
        int presetIndex = Integer.parseInt(request.getParameter("index"));
        DahuaClient app = DahuaServer.app;
        app.setPreset(m_strRealCameraID, presetName, presetIndex);
        return HandleResult.success();
    }


    /**
     * @param request
     * presetIndex  预设点编号
     * @return HandlerResult
     * @description: 根据specialCode，和presetIndex来定位到预设点
     */
    @RequestMapping(value = "/goToPreset", method = RequestMethod.GET)
    public HandleResult webGoToPreset(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";
        int presetIndex = Integer.parseInt(request.getParameter("command"));
        DahuaClient app = DahuaServer.app;
        app.goToPreset(m_strRealCameraID, presetIndex);
        return HandleResult.success();
    }



    /**
     * @param request
     * presetIndex  预设点编号
     * @return HandlerResult
     * @description: 根据specialCode和presetIndex删除预设点
     */
    @RequestMapping(value = "/delPreset", method = RequestMethod.GET)
    public HandleResult webDelPreset(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";
        int presetIndex = Integer.parseInt(request.getParameter("command"));
        DahuaClient app = DahuaServer.app;
        app.delPreset(m_strRealCameraID, presetIndex);
        return HandleResult.success();
    }



    /**
     * @param request
     * OSD  显示字符串
     * @return HandlerResult
     * @description: 根据deviceId和Position修改预设点名字
     */
    @RequestMapping(value = "/setOSD", method = RequestMethod.GET)
    public HandleResult webSetOSD(HttpServletRequest request) {
        String specialCode = request.getParameter("specialCode");
        DaHuaCameraInfo specificCamera = tmpDeviceService.find(specialCode);
        if (specificCamera==null ) {
            String msg = String.format("specialCode[%s]，输入正确的识别号", specialCode);
            return HandleResult.errorParam(msg);
        }
        String m_strRealCameraID=specificCamera.getDeviceID()+ "$1$0$0";

        String name = request.getParameter("command");
        DahuaClient app = DahuaServer.app;
        app.setOSD(m_strRealCameraID, name);
        return HandleResult.success();
    }



    @RequestMapping(value = "/savaData", method = RequestMethod.GET)
    public HandleResult webSaveData(HttpServletRequest request) throws DocumentException {
//        CameraInfoService tmpDeviceService= new CameraInfoService();
//        tmpDeviceService.saveInfo();
        tmpDeviceService.savaData();
        return HandleResult.success();
    }






}
