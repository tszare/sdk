package pl.platform.web;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.internal.operation.ListDatabasesOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.platform.Info.PlatformCameraInfo;
import pl.platform.Info.ProxyAddressInfo;
import pl.platform.api.API;
import pl.platform.mongo.dao.PlatformCameraInfoDao;
import pl.platform.mongo.dao.ProxyAddressInfoDao;
import regis.common.util.HandleResult;
import regis.http.client.RegisUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.*;


@RestController
@RequestMapping(value = "/platform")
@Slf4j

public class WebControl {

    @Autowired
    ProxyAddressInfoDao proxyAddressInfoDao;
    @Autowired
    PlatformCameraInfoDao platformCameraInfoDao;


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




    @RequestMapping(value = "/catchNewProxyAddress", method = RequestMethod.GET)
    public HandleResult webCatchNewProxyAddress(HttpServletRequest request) {
        String proxyIp= request.getParameter("proxyIp");
        String proxyPort= request.getParameter("proxyPort");
        String category= request.getParameter("category");
        proxyAddressInfoDao.catchNewProxyAddress(proxyIp,proxyPort,category);
        return HandleResult.success(proxyIp);

    }

    @RequestMapping(value = "/setProxyName", method = RequestMethod.GET)
    public HandleResult webSetProxyName(HttpServletRequest request) {
        String proxyIp= request.getParameter("proxyIp");
        String proxyPort= request.getParameter("proxyPort");
        String name = request.getParameter("name");
        String result =  proxyAddressInfoDao.setProxyName(proxyIp,proxyPort,name);
        return HandleResult.success(result);

    }


    /**
     *从单proxy处获取当前所管理的所有相机编号
     **/
    @RequestMapping(value = "/catchNewDeviceFormOneProxy", method = RequestMethod.GET)
    public HandleResult webCatchNewDeviceFormOneProxy(HttpServletRequest request){
        String proxyIp = request.getParameter("proxyIp");
        String proxyPort= request.getParameter("proxyPort");
        ProxyAddressInfo proxyAddressInfo = proxyAddressInfoDao.findAddress(proxyIp,proxyPort);
        String category =  proxyAddressInfo.getCategory();
        JSONArray deviceSpecialCodeArray= JSON.parseObject(API.getProxyCamera(proxyIp,proxyPort,category)).getJSONArray("data");
        for(Object specialCode:deviceSpecialCodeArray){
            platformCameraInfoDao.catchNewDeviceFormOneProxy(proxyIp,proxyPort,category,specialCode.toString());
        }

        return HandleResult.success(deviceSpecialCodeArray.toString());
    }


    @RequestMapping(value = "/catchNewDeviceFormAllProxy", method = RequestMethod.GET)
    public HandleResult webCatchNewDeviceFormAllProxy(){
        List<ProxyAddressInfo> proxyAddressInfoList =  proxyAddressInfoDao.findAll();
        for(ProxyAddressInfo proxyAddressInfo:proxyAddressInfoList){
            String proxyIp = proxyAddressInfo.getProxyIp();
            String proxyPort = proxyAddressInfo.getProxyPort();
            String category =  proxyAddressInfo.getCategory();
            JSONArray deviceSpecialCodeArray= JSON.parseObject(API.getProxyCamera(proxyIp,proxyPort,category)).getJSONArray("data");
            for(Object specialCode:deviceSpecialCodeArray){
                platformCameraInfoDao.catchNewDeviceFormOneProxy(proxyIp,proxyPort,category,specialCode.toString());
            }
        }
        return HandleResult.success();
    }



    @RequestMapping(value="/getDeviceInfo", method=RequestMethod.GET)
    public HandleResult webGetDeviceInfo(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/getDeviceInfo";

        String info= JSONObject.parseObject(API.sentRequestToProxyWithOneParameter(url,specialCode)).getString("data");
        log.info(info);
        return HandleResult.success(info);

    }


    /**
     * 相机实时抓拍
     */

    @RequestMapping(value="/getSnapshot", method=RequestMethod.GET)
    public HandleResult webGetSnapshot(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/getSnapshot";
        String result =API.sentRequestToProxyWithOneParameter(url,specialCode);
        return HandleResult.success(result);

    }


    /**
     * streamType  请求的RTSP地址媒体流类型，
     * 0  =主码流
     * 1  =辅码流
     */


    @RequestMapping(value="/getStreamUrl",method=RequestMethod.GET)
    public HandleResult webGetStreamUrl(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        String streamType = request.getParameter("streamType");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/getStreamUrl";
        String result =API.sentRequestToProxyWithTwoParameter(url,specialCode,streamType);
        return HandleResult.success(result);

    }


    /**
     * command   = move(移动)，   =focus(变焦)，  =zoom(变倍)     =stop(持续移动的过程停止)
     * 移动状态下的direct =0（右移动）， =1（左移动）， =2（上移动），=3（下移动）
     * 变焦状态下的direct =0（焦点前移）， =1（焦点后移）
     * 变倍状态下的direct =0(放大)， =1（缩小）
     * 停止 无需direct参数
     */
    @RequestMapping(value="/ptzControl", method=RequestMethod.GET)
    public HandleResult webMove(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        String command = request.getParameter("command");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        if(command.equals("stop")){
            String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/"+command;
            return HandleResult.success(API.sentRequestToProxyWithOneParameter(url,specialCode));
        } else{
            String direct=request.getParameter("direct");
            String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/"+command;
            return HandleResult.success(API.sentRequestToProxyWithTwoParameter(url,specialCode,direct));
        }

    }




    /**
     * 获取预设点，此处一点预设点机制需要了解。相机的预设点是各自的服务器给予和保存的。预设点的信息和相机无关。
     * 例如：同一个相机，同时接入了海康和大华的服务器。在调用获取该相机的预设点信息的接口时，从大华服务器读取的信息和海康的信息不同。
     * 传递的参数只需specialCode.
     */

    @RequestMapping(value = "/getPreset", method = RequestMethod.GET)
    public HandleResult webGetPreset(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/getPreset";
        return HandleResult.success(JSON.parseObject(API.sentRequestToProxyWithOneParameter(url,specialCode)).getString("data"));
    }




    /**
     * 设置预设点，传参数，预设点名字和预设点的序号
     */
    @RequestMapping(value = "/setPreset", method = RequestMethod.GET)
    public HandleResult webPresetControl(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        String presetName = request.getParameter("presetName");
        String presetIndex = request.getParameter("presetIndex");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/setPreset";
        return HandleResult.success( API.sentRequestToProxyWithThreeParameter(url,specialCode,presetName,presetIndex));
    }



    /**
     * 根据预置点的编号来定位
     * 传递参数presetIndex
     */
    @RequestMapping(value = "/goToPreset", method = RequestMethod.GET)
    public HandleResult webGoToPreset(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        String presetIndex = request.getParameter("presetIndex");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/goToPreset";
        return HandleResult.success( API.sentRequestToProxyWithTwoParameter(url,specialCode,presetIndex));
    }


    /**
     * 根据预置点的编号来删除预设点
     * 传递参数presetIndex
     */
    @RequestMapping(value = "/delPreset", method = RequestMethod.GET)
    public HandleResult webDelPreset(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        String presetIndex = request.getParameter("presetIndex");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/delPreset";
        return HandleResult.success( API.sentRequestToProxyWithTwoParameter(url,specialCode,presetIndex));
    }





    /**
     * 获取相机OSD信息
     * 此接口onvif和haiKang支持调用, daHua不支持
     */

    @RequestMapping(value = "/getOSD", method = RequestMethod.GET)
    public HandleResult webGetOSD(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/getOSD";
      //  log.info(JSONObject.parseObject(API.sentRequestToProxyWithOneParameter(url,specialCode)).getString("data"));
        return HandleResult.success(JSONObject.parseObject(API.sentRequestToProxyWithOneParameter(url,specialCode)).getString("data") );
    }




    /**
     * 新建OSD指令
     * 此接口onvif和haiKang支持调用, daHua不支持
     * setType ==0   时间OSD
     * setType ==1   文本OSD
     */
    @RequestMapping(value = "/setOSD", method = RequestMethod.GET)
    public HandleResult webSetOSD(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String setType = request.getParameter("setType");
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/setOSD";
        log.info(API.sentRequestToProxyWithTwoParameter(url,specialCode,setType));
        return HandleResult.success(JSONObject.parseObject(API.sentRequestToProxyWithTwoParameter(url,specialCode,setType)).getString("data"));
    }


    /**
     * 删除OSD指令
     * 此接口onvif和haiKang支持调用, daHua不支持
     * setType ==0   时间OSD
     * setType ==1   文本OSD
     */
    @RequestMapping(value = "/delOSD", method = RequestMethod.GET)
    public HandleResult webDetOSD(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String setType = request.getParameter("setType");
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/delOSD";
        API.sentRequestToProxyWithTwoParameter(url,specialCode,setType);
        return HandleResult.success();
    }




    /**
     * 此接口onvif和haiKang支持调用, daHua不支持
     * 设置文本OSD显示字符
     */
    @RequestMapping(value = "/setOSDName", method = RequestMethod.GET)
    public HandleResult webOnvifDelOSD(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String name = request.getParameter("name");
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/setOSDName";

        return HandleResult.success(API.sentRequestToProxyWithTwoParameter(url,specialCode,name));
    }


    /**
     * 移动OSD指令
     * 此接口onvif和haiKang支持调用, daHua不支持
     * setType ==0   时间OSD
     * setType ==1   文本OSD
     * xPos，yPOS是OSD在视频上显示的位置，按 x（-1,1），y(-1,1)的直接坐标系分布
     */
    @RequestMapping(value = "/moveOSD", method = RequestMethod.GET)
    public HandleResult webMoveOnvifOSD(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String target = request.getParameter("setType");
        String xPos =  request.getParameter("xPos");
        String yPos =  request.getParameter("yPos");
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/moveOSD";
        return HandleResult.success(JSONObject.parseObject(API.sentRequestToMoveOnvifOSD(url,specialCode,target,xPos,yPos)).getString("data"));
    }





    /**
     * 设置OSD显示时间（Onvif可调用接口）
     * Data输入格式: 字符串YY-MM-DD
     * Time输入格式: 字符串hh:mm:ss
     */
    @RequestMapping(value = "/setOnvifOSDTime", method = RequestMethod.GET)
    public HandleResult webSetOnvifOSDTime(HttpServletRequest request){
        String specialCode = request.getParameter("specialCode");
        String data = request.getParameter("Data");
        String time = request.getParameter("Time");
        PlatformCameraInfo platformCameraInfo =  platformCameraInfoDao.find(specialCode);
        String url = platformCameraInfo.getProxyHost()+platformCameraInfo.getCategory()+"/setOSDTime";

        return HandleResult.success(JSONObject.parseObject(API.sentRequestToProxyWithThreeParameter(url,specialCode,data,time)).getString("data"));
    }



    @RequestMapping(value="/flutter",method = RequestMethod.GET)
    public HandleResult testFlutter(HttpServletRequest request){
        JSONObject testPost1 =new JSONObject();
        testPost1.put("title","修真聊天群");
        testPost1.put("image","https://pics1.baidu.com/feed/91529822720e0cf3268fd7596012cb19bf09aaf7.jpeg?token=7df99aa53e2eb45ec8a46842e3231165&amp;s=ABA2BD090FD245E9C259D4DF030080A0");
        testPost1.put("profile","某天，宋书航意外加入了一个仙侠中二病资深患者的交流群，里面的群友们都以‘道友’相称，群名片都是各种府主、洞主、真人、天师。连群主走失的宠物犬都称为大妖犬离家出走。整天聊的是炼丹、闯秘境、炼功经验啥的。");

        JSONObject testPost2 =new JSONObject();
        testPost2.put("title","赘婿");
        testPost2.put("image","http://m.qpic.cn/psc?/V51Rd3oc0nnufu10bkEH2RQbrP09SW7Y/TmEUgtj9EK6.7V8ajmQrEPPAAWyfNI728oUUawul4fEBRyFA9PzBoHSx.lb9gU2IeDmLUjjaiCE7u8GOAGMgHtSEnAJYXc5CKlw57yW5iXY!/b&bo=uAHYAgAAAAABF1M!&rf=viewer_4");
        testPost2.put("proflie","武朝末年，岁月峥嵘，天下纷乱，金辽相抗，局势动荡，百年屈辱，终于望见结束的第一缕曙光，天祚帝、完颜阿骨打、吴乞买，成吉思汗铁木真、札木合、赤老温、木华黎、博尔忽...");

        JSONObject testPost3 =new JSONObject();
        testPost3.put("title","逆流纯真时代");
        testPost3.put("image","http://m.qpic.cn/psc?/V51Rd3oc0nnufu10bkEH2RQbrP09SW7Y/TmEUgtj9EK6.7V8ajmQrEOvGNHn3GfKU1DSgwexkq79i2.MU0AH.Qdh*IBrNBOr6GbYqZKLzpgoI8.YdOjDwp6EGTNxWvKM.x24QO.62ZX4!/b&bo=rgEVAgAAAAABF4g!&rf=viewer_4");
        testPost3.put("profile","时光是一艘航船,有人喜欢把90年代初的那几年,视为曾经那个纯真年代的最后一程。 岁月更迭中早已不再单纯的江澈,逆流归来...");

        JSONArray books=new JSONArray();
        books.add(testPost1);
        books.add(testPost2);
        books.add(testPost3);
        return  HandleResult.success(books);

    }



    @RequestMapping(value="/upGrade",method = RequestMethod.GET)
    public HandleResult appUpGrade(HttpServletRequest request){
        JSONObject Info =new JSONObject();
        List<String> contents = Arrays.asList(
                "1、独一无二的应用构建能力集合",
                "2、健全的空安全",
                "3、用于集成Dart和C的FFI",
                "4、FlutterWeb正式进入stable渠道。",
                "5、Flutter Desktop也正式进入stable渠道，即初始发布状态。");

        Info.put("title","新版本:flutter 2.0");
        Info.put("contents", contents);
        Info.put("force",false);
        Info.put("apkDownloadUrl","http://192.168.14.211:8004/platform/flutter");




        return  HandleResult.success(Info);

    }




}
