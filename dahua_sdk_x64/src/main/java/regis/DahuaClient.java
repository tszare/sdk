package regis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.util.Base64;
import com.dh.DpsdkCore.Alarm_Info_t;
import com.dh.DpsdkCore.Alarm_Query_Info_t;
import com.dh.DpsdkCore.Area_Detect_Info_t;
import com.dh.DpsdkCore.Device_Info_Ex_t;
import com.dh.DpsdkCore.GetLinkResource_Responce_t;
import com.dh.DpsdkCore.GetUserOrgInfo;
import com.dh.DpsdkCore.Get_Dep_Count_Info_t;
import com.dh.DpsdkCore.Get_ExternalRealStreamUrl_Info_t;
import com.dh.DpsdkCore.Get_RealStreamUrl_Info_t;
import com.dh.DpsdkCore.Get_RealStream_Info_t;
import com.dh.DpsdkCore.Get_RecordStream_Time_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Load_Dep_Info_t;
import com.dh.DpsdkCore.Login_Info_t;
import com.dh.DpsdkCore.OSDTextAlign_e;
import com.dh.DpsdkCore.OSD_Info_t;
import com.dh.DpsdkCore.Person_Count_Info_t;
import com.dh.DpsdkCore.Ptz_Direct_Info_t;
import com.dh.DpsdkCore.Ptz_Operation_Info_t;
import com.dh.DpsdkCore.Ptz_Prepoint_Info_t;
import com.dh.DpsdkCore.Ptz_Prepoint_Operation_Info_t;
import com.dh.DpsdkCore.Ptz_Single_Prepoint_Info_t;
import com.dh.DpsdkCore.Query_Record_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.Send_OSDInfo_t;
import com.dh.DpsdkCore.SetDoorCmd_Request_t;
import com.dh.DpsdkCore.Subscribe_Bay_Car_Info_t;
import com.dh.DpsdkCore.Traffic_Alarm_Info_t;
import com.dh.DpsdkCore.dpsdk_SetDoorCmd_e;
import com.dh.DpsdkCore.dpsdk_alarm_type_e;
import com.dh.DpsdkCore.dpsdk_check_right_e;
import com.dh.DpsdkCore.dpsdk_constant_value;
import com.dh.DpsdkCore.dpsdk_dev_type_e;
import com.dh.DpsdkCore.dpsdk_getgroup_operation_e;
import com.dh.DpsdkCore.dpsdk_mdl_type_e;
import com.dh.DpsdkCore.dpsdk_media_type_e;
import com.dh.DpsdkCore.dpsdk_protocol_version_e;
import com.dh.DpsdkCore.dpsdk_record_type_e;
import com.dh.DpsdkCore.dpsdk_retval_e;
import com.dh.DpsdkCore.dpsdk_sdk_type_e;
import com.dh.DpsdkCore.dpsdk_stream_type_e;
import com.dh.DpsdkCore.dpsdk_trans_type_e;
import com.dh.DpsdkCore.fDPSDKDevStatusCallback;
import com.dh.DpsdkCore.fDPSDKGeneralJsonTransportCallback;
import com.dh.DpsdkCore.fDPSDKGetAreaSpeedDetectCallback;
import com.dh.DpsdkCore.fDPSDKGetBayCarInfoCallbackEx;
import com.dh.DpsdkCore.fDPSDKNVRChnlStatusCallback;
import com.dh.DpsdkCore.fDPSDKTrafficAlarmCallback;
import com.dh.DpsdkCore.generaljson_trantype_e;
import com.dh.DpsdkCore.tvwall_screen_split_caps;
import com.dh.DpsdkCore.TvWall.Set_TvWall_Screen_Window_Source_t;
import com.dh.DpsdkCore.TvWall.TvWall_Layout_Info_t;
import com.dh.DpsdkCore.TvWall.TvWall_List_Info_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Close_Source_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Open_Window_t;
import com.dh.DpsdkCore.TvWall.TvWall_Screen_Split_t;
import com.dh.DpsdkCore.TvWall.TvWall_Task_Info_List_t;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import regis.http.client.MyScript;
import regis.http.client.RegisUtil;

@Slf4j
public class DahuaClient {

    public final static String davPath;
    public final static String dpsdklog;
    public final static String dumpfile;
    public final static String gpsXmlFileName;
    public final static String textXmlFileName;

    static {
        String startupPath = MyScript.getAppPath();
        // String rootDir = FileUtil.getRootDir(startupPath);
        String rootDir = startupPath;
        davPath = Paths.get(rootDir, "logs", "downoladtest.dav").toString();
        dpsdklog = Paths.get(rootDir, "logs", "dpsdkjavalog").toString();
        dumpfile = Paths.get(rootDir, "logs", "dpsdkjavadump").toString();
        gpsXmlFileName = Paths.get(rootDir, "logs", "gps.xml").toString();
        textXmlFileName = Paths.get(rootDir, "logs", "text.xml").toString();
    }
    public static int nDownloadSeq = 0;
    public static String StrCarNum;
    public static int m_nDLLHandle = -1;
    // 报警设备ID

    // public String m_strIp = "60.191.94.121"; //登录平台ip
    // public String m_strIp = "172.7.3.250"; //登录平台ip
    // public int m_nPort = 9000; //端口
    // public String m_strUser = "DPSDK"; //用户名
    // public String m_strPassword = "qwer1234"; //密码

    // 登录平台ip
    public final String m_strIp = "192.168.2.64";
    // 端口
    public final int m_nPort = 9000;
    // 用户名
    public final String m_strUser = "test";
    // 密码
    public final String m_strPassword = "test123456";

    Return_Value_Info_t nGroupLen = new Return_Value_Info_t();

    FileOutputStream writer = null;

    DPSDKAlarmCallback m_AlarmCB = new DPSDKAlarmCallback();

    DPSDKMediaDataCallback m_MediaCB = new DPSDKMediaDataCallback();
    DPSDKMediaDataCallback m_MediaDownloadCB = new DPSDKMediaDataCallback() {
        @Override
        public void invoke(int nPDLLHandle, int nSeq, int nMediaType, byte[] szNodeId, int nParamVal, byte[] szData,
                int nDataLen) {
            if (nMediaType == 2 && nDataLen == 0) {
                // 录像下载结束,开线程调用停止录像，否则接口会超时
                nDownloadSeq = nSeq;
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            if (writer != null) {
                                writer.flush();
                                writer.close();
                                writer = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int nRet = IDpsdkCore.DPSDK_CloseRecordStreamBySeq(m_nDLLHandle, nDownloadSeq, 10000);
                        if (nRet == 0) {
                            nDownloadSeq = -1;
                        }
                        log.info(String.format("下载结束，停止下载nRet = %d", nRet));
                    }

                });
                t.start();
            }
            try {
                if (davPath != null) {
                    if (writer == null) {
                        writer = new FileOutputStream(davPath, true);
                    }
                    if (nDataLen > 0) {
                        writer.write(szData);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public fDPSDKDevStatusCallback fDeviceStatus = new fDPSDKDevStatusCallback() {
        @Override
        public void invoke(int nPDLLHandle, byte[] szDeviceId, int nStatus) {
            String status = "离线";
            if (nStatus == 1) {
                status = "在线";
                Device_Info_Ex_t deviceInfo = new Device_Info_Ex_t();
                int nRet = IDpsdkCore.DPSDK_GetDeviceInfoExById(m_nDLLHandle, szDeviceId, deviceInfo);
                if (deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_NVR
                        || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_EVS
                        || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_SMART_NVR
                        || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_MATRIX_NVR6000) {
                    nRet = IDpsdkCore.DPSDK_QueryNVRChnlStatus(m_nDLLHandle, szDeviceId, 10 * 1000);

                    if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                        // log.info("查询NVR通道状态成功，deviceID = %s", new
                        // String(szDeviceId));
                    } else {
                        log.info(String.format("查询NVR通道状态失败，deviceID = %s, nRet = %d", new String(szDeviceId), nRet));
                    }
                    //
                }
            }
            // log.info("Device Status Report!, szDeviceId = %s, nStatus = %s",
            // new
            // String(szDeviceId),status);
            //
        }
    };

    public fDPSDKNVRChnlStatusCallback fNVRChnlStatus = new fDPSDKNVRChnlStatusCallback() {
        @Override
        public void invoke(int nPDLLHandle, byte[] szCameraId, int nStatus) {
            String status = "离线";
            if (nStatus == 1) {
                status = "在线";
            }
            // log.info("NVR Channel Status Report!, szCameraId = %s, nStatus =
            // %s", new String(szCameraId),status);
            //
        }
    };

    public fDPSDKGeneralJsonTransportCallback fGeneralJson = new fDPSDKGeneralJsonTransportCallback() {
        @Override
        public void invoke(int nPDLLHandle, byte[] szJson) {
            // log.info(String.format("General Json Return, ReturnJson = %s",
            // new
            // String(szJson)));
            Map<String, Object> map = RegisUtil.toEntry(new String(szJson));
            Map<String, Object> paramMap = (Map<String, Object>) map.get("params");
            String picInfo = (String) paramMap.get("PicInfo");
            //log.info(picInfo.length());

            byte[] bytes = Base64.decodeFast(picInfo);
            //log.info(bytes.length);
            try {
                Files.write(bytes, new File("/tmp/1.jpg"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    // 卡口过车数据回调
    public fDPSDKGetBayCarInfoCallbackEx fBayCarInfo = new fDPSDKGetBayCarInfoCallbackEx() {
        @Override
        public void invoke(int nPDLLHandle, byte[] szDeviceId, int nDeviceIdLen, int nDevChnId, byte[] szChannelId,
                int nChannelIdLen, byte[] szDeviceName, int nDeviceNameLen, byte[] szDeviceChnName, int nChanNameLen,
                byte[] szCarNum, int nCarNumLen, int nCarNumType, int nCarNumColor, int nCarSpeed, int nCarType,
                int nCarColor, int nCarLen, int nCarDirect, int nWayId, long lCaptureTime, long lPicGroupStoreID,
                int nIsNeedStore, int nIsStoraged, byte[] szCaptureOrg, int nCaptureOrgLen, byte[] szOptOrg,
                int nOptOrgLen, byte[] szOptUser, int nOptUserLen, byte[] szOptNote, int nOptNoteLen, byte[] szImg0Path,
                int nImg0PathLen, byte[] szImg1Path, int nImg1PathLen, byte[] szImg2Path, int nImg2PathLen,
                byte[] szImg3Path, int nImg3PathLen, byte[] szImg4Path, int nImg4PathLen, byte[] szImg5Path,
                int nImg5PathLen, byte[] szImgPlatePath, int nImgPlatePathLen, int icarLog, int iPlateLeft,
                int iPlateRight, int iPlateTop, int iPlateBottom) {
            try {
                StrCarNum = new String(szCarNum, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info(String.format(
                    "Bay Car Info Report, DeviceId=%s, szChannelId=%s, szDeviceChnName=%s, szCarNum=%s, szImg0Path=%s",
                    new String(szDeviceId), new String(szChannelId), new String(szDeviceChnName), StrCarNum,
                    new String(szImg0Path)));

        }
    };

    // 违章报警回调
    public fDPSDKTrafficAlarmCallback fTrafficAlarmCallback = new fDPSDKTrafficAlarmCallback() {
        @Override
        public void invoke(int nPDLLHandle, Traffic_Alarm_Info_t trafficAlarmInfo) {
            try {
                StrCarNum = new String(trafficAlarmInfo.szCarNum, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info(String.format(
                    "TrafficAlarm Car Info Report, DeviceId=%s, szChannelId=%s, szDeviceChnName=%s, szCarNum=%s, szImg0Path=%s",
                    new String(trafficAlarmInfo.szDeviceId).trim(), new String(trafficAlarmInfo.szCameraId).trim(),
                    new String(trafficAlarmInfo.szDeviceName).trim(), StrCarNum.trim(),
                    new String(trafficAlarmInfo.szPicUrl0).trim()));

        }
    };

    // 区间测速回调
    public fDPSDKGetAreaSpeedDetectCallback fGetAreaSpeedDetectCallback = new fDPSDKGetAreaSpeedDetectCallback() {
        @Override
        public void invoke(int nPDLLHandle, Area_Detect_Info_t areaSpeedDetectInfo) {
            try {
                StrCarNum = new String(areaSpeedDetectInfo.szCarNum, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info(String.format("AreaSpeedDetectInfo Report, szCarNum=%s, szPicName0=%s", StrCarNum.trim(),
                    new String(areaSpeedDetectInfo.szPicName0).trim()));

        }
    };

    /*
     * 创建DPSDK
     */
    public void OnCreate() {
        int nRet = -1;
        Return_Value_Info_t res = new Return_Value_Info_t();
        nRet = IDpsdkCore.DPSDK_Create(dpsdk_sdk_type_e.DPSDK_CORE_SDK_SERVER, res);

        m_nDLLHandle = res.nReturnValue;

        nRet = IDpsdkCore.DPSDK_SetLog(m_nDLLHandle, dpsdklog.getBytes());

        nRet = IDpsdkCore.DPSDK_StartMonitor(m_nDLLHandle, dumpfile.getBytes());
        if (m_nDLLHandle > 0) {
            // 设置设备状态上报监听函数
            nRet = IDpsdkCore.DPSDK_SetDPSDKDeviceStatusCallback(m_nDLLHandle, fDeviceStatus);
            // 设置NVR通道状态上报监听函数
            nRet = IDpsdkCore.DPSDK_SetDPSDKNVRChnlStatusCallback(m_nDLLHandle, fNVRChnlStatus);
            // 设置通用JSON回调
            nRet = IDpsdkCore.DPSDK_SetGeneralJsonTransportCallback(m_nDLLHandle, fGeneralJson);

            nRet = IDpsdkCore.DPSDK_SetDPSDKGetBayCarInfoCallbackEx(m_nDLLHandle, fBayCarInfo);

            nRet = IDpsdkCore.DPSDK_SetDPSDKTrafficAlarmCallback(m_nDLLHandle, fTrafficAlarmCallback);

            nRet = IDpsdkCore.DPSDK_SetDPSDKGetAreaSpeedDetectCallback(m_nDLLHandle, fGetAreaSpeedDetectCallback);
        }

        log.info("创建DPSDK, 返回 m_nDLLHandle = " + m_nDLLHandle);
    }

    /*
     * 登录
     */
    public void OnLogin() {
        Login_Info_t loginInfo = new Login_Info_t();
        loginInfo.szIp = m_strIp.getBytes();
        loginInfo.nPort = m_nPort;
        loginInfo.szUsername = m_strUser.getBytes();
        loginInfo.szPassword = m_strPassword.getBytes();
        loginInfo.nProtocol = dpsdk_protocol_version_e.DPSDK_PROTOCOL_VERSION_II;
        loginInfo.iType = 1;

        int nRet = IDpsdkCore.DPSDK_Login(m_nDLLHandle, loginInfo, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("登录成功，nRet = %d", nRet));
        } else {
            log.info(String.format("登录失败，nRet = %d", nRet));
        }

    }

    /*
     * 加载所有组织树
     */
    public void LoadAllGroup() {
        int nRet = IDpsdkCore.DPSDK_LoadDGroupInfo(m_nDLLHandle, nGroupLen, 180000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("加载所有组织树成功，nRet = %d， nDepCount = %d", nRet, nGroupLen.nReturnValue));
        } else {
            log.info(String.format("加载所有组织树失败，nRet = %d", nRet));
        }

    }

    /*
     * 获取所有组织树串
     */
    public String GetDeviceInfo() {
        byte[] szGroupBuf = new byte[nGroupLen.nReturnValue];
        int nRet = IDpsdkCore.DPSDK_GetDGroupStr(m_nDLLHandle, szGroupBuf, nGroupLen.nReturnValue, 10000);

        String GroupBuf = "";
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {

            try {
                GroupBuf = new String(szGroupBuf, "UTF-8");

            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info(String.format("获取所有组织树串成功，nRet = %d， szGroupBuf = [\n%s\n]", nRet, GroupBuf));

        } else {
            log.info(String.format("获取所有组织树串失败，nRet = %d", nRet));
        }

        return GroupBuf;

    }










    /*
     * 获取用户组织信息
     */
    public void GetUserOrgInfo() {
        GetUserOrgInfo userOrgInfo = new GetUserOrgInfo();
        int nRet = IDpsdkCore.DPSDK_GetUserOrgInfo(m_nDLLHandle, userOrgInfo, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("获取用户组织信息成功，nRet = %d， UserOrgInfo = %s", nRet, userOrgInfo.strUserOrgInfo));
        } else {
            log.info(String.format("获取用户组织信息失败，nRet = %d", nRet));
        }

    }

    /*
     * 获取Ftp信息
     */
    public void GetFtpInfo() {
        byte[] szFtpInfo = new byte[64];
        int nRet = IDpsdkCore.DPSDK_GetFTPInfo(m_nDLLHandle, szFtpInfo, 64);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            String FtpInfo = "";
            try {
                FtpInfo = new String(szFtpInfo, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info(String.format("获取Ftp信息成功，nRet = %d， szFtpInfo = [%s]", nRet, FtpInfo));
        } else {
            log.info(String.format("获取Ftp信息失败，nRet = %d", nRet));
        }

    }

    public void GetGPSXML() {
        Return_Value_Info_t nGpsXMLLen = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_AskForLastGpsStatusXMLStrCount(m_nDLLHandle, nGpsXMLLen, 10 * 1000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS && nGpsXMLLen.nReturnValue > 0) {
            byte[] LastGpsIStatus = new byte[nGpsXMLLen.nReturnValue - 1];
            nRet = IDpsdkCore.DPSDK_AskForLastGpsStatusXMLStr(m_nDLLHandle, LastGpsIStatus, nGpsXMLLen.nReturnValue);

            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("获取GPS XML成功，nRet = %d， LastGpsIStatus = [%s]", nRet,
                        new String(LastGpsIStatus)));
                try {
                    File file = new File(gpsXmlFileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream out = new FileOutputStream(file);
                    out.write(LastGpsIStatus);
                    out.close();
                } catch (IOException e1) {
                    log.error("fatalError", e1);
                }
            } else {
                log.info(String.format("获取GPS XML失败，nRet = %d", nRet));
            }
        } else if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS && nGpsXMLLen.nReturnValue == 0) {
            log.info(String.format("获取GPS XML  XMLlength = 0"));
        } else {
            log.info(String.format("获取GPS XML失败，nRet = %d", nRet));
        }

    }

    /*
     * 查询NVR设备的通道状态
     */
    // public void QureyNVRChannelStatus() {
    // Device_Info_Ex_t deviceInfo = new Device_Info_Ex_t();
    // int nRet = IDpsdkCore.DPSDK_GetDeviceInfoExById(m_nDLLHandle,
    // m_strNVRDeviceID.getBytes(), deviceInfo);
    // if (deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_NVR ||
    // deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_EVS
    // || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_TYPE_SMART_NVR
    // || deviceInfo.nDevType == dpsdk_dev_type_e.DEV_MATRIX_NVR6000) {
    // nRet = IDpsdkCore.DPSDK_QueryNVRChnlStatus(m_nDLLHandle,
    // m_strNVRDeviceID.getBytes(), 10 * 1000);
    //
    // if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
    // log.info(String.format("查询NVR通道状态成功，nRet = %d", nRet));
    // } else {
    // log.info(String.format("查询NVR通道状态失败，nRet = %d", nRet));
    // }
    //
    // }
    // }

    /*
     * 加载组织树
     */
    public void LoadGroup() {
        String strCoding = "001";
        Load_Dep_Info_t depInfo = new Load_Dep_Info_t();
        depInfo.nOperation = dpsdk_getgroup_operation_e.DPSDK_CORE_GEN_GETGROUP_OPERATION_CHILD;
        depInfo.szCoding = strCoding.getBytes();
        Return_Value_Info_t nLen = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_LoadDGroupInfoLayered(m_nDLLHandle, depInfo, nLen, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("加载组织树成功，nRet = %d， nGroupLen = %d", nRet, nLen.nReturnValue));
        } else {
            log.info(String.format("加载组织树失败，nRet = %d", nRet));
        }

        Get_Dep_Count_Info_t depCountInfo = new Get_Dep_Count_Info_t();
        depCountInfo.szCoding = strCoding.getBytes();
        nRet = IDpsdkCore.DPSDK_GetDGroupCount(m_nDLLHandle, depCountInfo);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("获取子组织子设备个数成功，nRet = %d， nDepCount = %d, nDeviceCount= %d", nRet,
                    depCountInfo.nDepCount, depCountInfo.nDeviceCount));
        } else {
            log.info(String.format("获取子组织子设备失败，nRet = %d", nRet));
        }

    }

    int GetRealVideoStream(String m_strRealCamareID) {
        Return_Value_Info_t nRealSeq = new Return_Value_Info_t();
        Get_RealStream_Info_t getInfo = new Get_RealStream_Info_t();
        getInfo.szCameraId = m_strRealCamareID.getBytes();
        getInfo.nStreamType = dpsdk_stream_type_e.DPSDK_CORE_STREAMTYPE_MAIN;
        // 不检查权限，请求视频流，无需加载组织结构
        getInfo.nRight = dpsdk_check_right_e.DPSDK_CORE_NOT_CHECK_RIGHT;
        getInfo.nMediaType = dpsdk_media_type_e.DPSDK_CORE_MEDIATYPE_VIDEO;
        getInfo.nTransType = dpsdk_trans_type_e.DPSDK_CORE_TRANSTYPE_TCP;
        getInfo.nTrackID = 501; // 拉国标码流

        int nRet = IDpsdkCore.DPSDK_GetRealStream(m_nDLLHandle, nRealSeq, getInfo, m_MediaCB, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("打开实时视频成功，nRet = %d， nSeq = %d", nRet, nRealSeq.nReturnValue));
        } else {
            log.info(String.format("打开实时视频失败，nRet = %d", nRet));
        }

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            return nRealSeq.nReturnValue;
        } else {
            return -1;
        }
    }

    void CloseReal(int nRealSeq) {
        int nRet = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_nDLLHandle, nRealSeq, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("关闭实时视频成功，nRet = %d， nSeq = %d", nRet, nRealSeq));
        } else {
            log.info(String.format("关闭实时视频失败，nRet = %d", nRet));
        }

    }

    public void GetExternUrl(String m_strRealCamareID) {
        Get_ExternalRealStreamUrl_Info_t pExternalRealStreamUrlInfo = new Get_ExternalRealStreamUrl_Info_t();
        pExternalRealStreamUrlInfo.szCameraId = m_strRealCamareID.getBytes();
        pExternalRealStreamUrlInfo.nMediaType = 1;
        pExternalRealStreamUrlInfo.nStreamType = 1;
        pExternalRealStreamUrlInfo.nTrackId = 8011;
        pExternalRealStreamUrlInfo.nTransType = 1;
        pExternalRealStreamUrlInfo.bUsedVCS = 0;
        pExternalRealStreamUrlInfo.nVcsbps = 0;
        pExternalRealStreamUrlInfo.nVcsfps = 0;
        pExternalRealStreamUrlInfo.nVcsResolution = 0;
        pExternalRealStreamUrlInfo.nVcsVideocodec = 0;
        int nRet = IDpsdkCore.DPSDK_GetExternalRealStreamUrl(m_nDLLHandle, pExternalRealStreamUrlInfo, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info("GetExternUrl>>>" + new String(pExternalRealStreamUrlInfo.szUrl).trim());
        } else {
            log.info(String.format("获取URL失败，nRet = %d", nRet));
        }

    }

    public String  GetRealStreamUrl(String m_strRealCamareID,int streamType) {
        Get_RealStreamUrl_Info_t pRealStreamUrlInfo = new Get_RealStreamUrl_Info_t();
        pRealStreamUrlInfo.szCameraId = m_strRealCamareID.getBytes();

        pRealStreamUrlInfo.nMediaType = streamType;

        pRealStreamUrlInfo.nStreamType = 1;
        pRealStreamUrlInfo.nTransType = 1;
        int nRet = IDpsdkCore.DPSDK_GetRealStreamUrl(m_nDLLHandle, pRealStreamUrlInfo, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            String url = new String(pRealStreamUrlInfo.szUrl).trim();
            int token = pRealStreamUrlInfo.nToken;
            log.info("获取URL>>>\n" + url);
            log.info("获取Token>>>" + token);

            return url;
        } else {
            log.info(String.format("获取URL失败，nRet = %d", nRet));
            return String.format("获取URL失败，nRet = %d", nRet);
        }
    }

    /*
     * 报警布控
     */
    public void SetAlarm() {
        int nRet = IDpsdkCore.DPSDK_SetDPSDKAlarmCallback(m_nDLLHandle, m_AlarmCB);// 设置报警监听函数
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("开启报警监听成功，nRet = %d", nRet));
        } else {
            log.info(String.format("开启报警监听失败，nRet = %d", nRet));
        }

        // Alarm_Enable_Info_t alarmInfo = new Alarm_Enable_Info_t(1);
        // alarmInfo.sources[0].szAlarmDevId = m_strAlarmCamareID.getBytes();
        // alarmInfo.sources[0].nVideoNo = 0;
        // alarmInfo.sources[0].nAlarmInput = 0;
        // alarmInfo.sources[0].nAlarmType =
        // dpsdk_alarm_type_e.DPSDK_CORE_ALARM_TYPE_VIDEO_LOST;
        // int nRet = IDpsdkCore.DPSDK_EnableAlarm(m_nDLLHandle,
        // alarmInfo,10000);

        // if(nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS)
        // {
        // log.info("报警布控成功，nRet = %d", nRet);
        // }else
        // {
        // log.info("报警布控失败，nRet = %d", nRet);
        // }
        //
    }

    /*
     * 报警查询
     */
    public void OnQueryAlarm(String m_strQueryAlarmCamareID) {
        Date tmStart = new Date(2015 - 1900, 6 - 1, 3, 0, 0, 0);
        Date tmEnd = new Date(2015 - 1900, 6 - 1, 3, 12, 0, 0);

        // log.info("%s",tmStart.toLocaleString());
        //
        // log.info("%s",tmEnd.toLocaleString());
        //
        Alarm_Query_Info_t stuQueryInfo = new Alarm_Query_Info_t();
        stuQueryInfo.szCameraId = m_strQueryAlarmCamareID.getBytes();
        stuQueryInfo.nAlarmType = dpsdk_alarm_type_e.DPSDK_CORE_ALARM_TYPE_VIDEO_SHELTER;
        stuQueryInfo.uStartTime = tmStart.getTime() / 1000;// 转换成秒
        stuQueryInfo.uEndTime = tmEnd.getTime() / 1000;

        // log.info("查询报警时间：%d - %d",stuQueryInfo.uStartTime,
        // stuQueryInfo.uEndTime );
        //

        Return_Value_Info_t nCount = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_QueryAlarmCount(m_nDLLHandle, stuQueryInfo, nCount, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("查询报警数量成功，nRet = %d， nCount= %d", nRet, nCount.nReturnValue));
        } else {
            log.info(String.format("查询报警数量失败，nRet = %d", nRet));
        }

        if (nCount.nReturnValue > 0) {
            Alarm_Info_t stuAlarmInfo = new Alarm_Info_t(nCount.nReturnValue);

            nRet = IDpsdkCore.DPSDK_QueryAlarmInfo(m_nDLLHandle, stuQueryInfo, stuAlarmInfo, 0, nCount.nReturnValue,
                    10000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("查询报警信息成功，nRet = %d， nCount= %d", nRet, stuAlarmInfo.nRetCount));
                for (int i = 0; i < stuAlarmInfo.nRetCount; i++) {

                    Date dTime = new Date(stuAlarmInfo.pAlarmInfo[i].uAlarmTime * 1000);
                    log.info(String.format("序号=%d，类型 = %d，时间=%s，事件=%d，设备ID=%s，通道号=%d， 处理状态= %d", i + 1,
                            stuAlarmInfo.pAlarmInfo[i].nAlarmType, dTime.toLocaleString(),
                            stuAlarmInfo.pAlarmInfo[i].nEventType,
                            new String(stuAlarmInfo.pAlarmInfo[i].szDevId).trim(), stuAlarmInfo.pAlarmInfo[i].uChannel,
                            stuAlarmInfo.pAlarmInfo[i].nDealWith));
                }
            } else {
                log.info(String.format("查询报警信息失败，nRet = %d", nRet));
            }

        }
    }

    public void OnSendOSDInfo() {
        String strDeviceId = new String("1000001");
        String strMsg = new String("qqq");
        Send_OSDInfo_t stuSendOSDInfo = new Send_OSDInfo_t();
        stuSendOSDInfo.szDevId = strDeviceId.getBytes();
        stuSendOSDInfo.szMessage = strMsg.getBytes();
        int nRet = IDpsdkCore.DPSDK_SendOSDInfo(m_nDLLHandle, stuSendOSDInfo, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("发送OSD信息成功，nRet = %d", nRet));
        } else {
            log.info(String.format("发送OSD信息失败，nRet = %d", nRet));
        }

    }

    public void OnGetTvWallList(String m_strTvWallSourceCamareID) {
        Return_Value_Info_t nCount = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_GetTvWallListCount(m_nDLLHandle, nCount, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("查询电视墙列表数量成功，nRet = %d， nCount= %d", nRet, nCount.nReturnValue));
        } else {
            log.info(String.format("查询电视墙列表数量失败，nRet = %d", nRet));
        }

        int nCurTvWallId = -1;
        if (nCount.nReturnValue > 0) {
            TvWall_List_Info_t pTvWallListInfo = new TvWall_List_Info_t(nCount.nReturnValue);

            nRet = IDpsdkCore.DPSDK_GetTvWallList(m_nDLLHandle, pTvWallListInfo);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("获取电视墙列表信息成功，nRet = %d， nCount= %d", nRet, pTvWallListInfo.nCount));
                for (int i = 0; i < pTvWallListInfo.nCount; i++) {

                    log.info(String.format("序号=%d，nTvWallId = %d，nState=%s，szName=%s", i + 1,
                            pTvWallListInfo.pTvWallInfo[i].nTvWallId, pTvWallListInfo.pTvWallInfo[i].nState,
                            new String(pTvWallListInfo.pTvWallInfo[i].szName).trim()));

                    if (i == 0) {
                        nCurTvWallId = pTvWallListInfo.pTvWallInfo[i].nTvWallId;
                    }
                }
            } else {
                log.info(String.format("获取电视墙列表信息失败，nRet = %d", nRet));
            }

        }
        if (nCurTvWallId > 0) {
            GetTvWallLayout(m_strTvWallSourceCamareID, nCurTvWallId);
        }

    }

    public void GetTvWallLayout(String m_strTvWallSourceCamareID, int nTvWallId) {
        log.info(String.format("nTvWallId=%d;", nTvWallId));
        Return_Value_Info_t nCount = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_GetTvWallLayoutCount(m_nDLLHandle, nTvWallId, nCount, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("查询电视墙布局成功，nRet = %d， nCount= %d", nRet, nCount.nReturnValue));
        } else {
            log.info(String.format("查询电视墙布局失败，nRet = %d", nRet));
        }

        int nScreenId = -1;
        float fLeft = 0;
        float fTop = 0;
        float fWidth = 0;
        float fHeight = 0;
        if (nCount.nReturnValue > 0) {
            TvWall_Layout_Info_t pTvWallLayoutInfo = new TvWall_Layout_Info_t(nCount.nReturnValue);
            pTvWallLayoutInfo.nTvWallId = nTvWallId;

            nRet = IDpsdkCore.DPSDK_GetTvWallLayout(m_nDLLHandle, pTvWallLayoutInfo);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("获取电视墙布局信息成功，nRet = %d， nCount= %d", nRet, pTvWallLayoutInfo.nCount));
                for (int i = 0; i < pTvWallLayoutInfo.nCount; i++) {
                    if (i == 0) {
                        nScreenId = pTvWallLayoutInfo.pScreenInfo[i].nScreenId;
                    }

                    log.info(String.format(
                            "序号=%d，nScreenId = %d，szName=%s, szDecoderId=%s,fLeft=%f,fTop=%f,fWidth=%f,fHeight=%f,bBind=%d",
                            i + 1, pTvWallLayoutInfo.pScreenInfo[i].nScreenId,
                            new String(pTvWallLayoutInfo.pScreenInfo[i].szName).trim(),
                            new String(pTvWallLayoutInfo.pScreenInfo[i].szDecoderId).trim(),
                            pTvWallLayoutInfo.pScreenInfo[i].fLeft, pTvWallLayoutInfo.pScreenInfo[i].fTop,
                            pTvWallLayoutInfo.pScreenInfo[i].fWidth, pTvWallLayoutInfo.pScreenInfo[i].fHeight,
                            pTvWallLayoutInfo.pScreenInfo[i].bBind ? 1 : 0));
                    fLeft = pTvWallLayoutInfo.pScreenInfo[i].fLeft;
                    fTop = pTvWallLayoutInfo.pScreenInfo[i].fTop;
                    fWidth = pTvWallLayoutInfo.pScreenInfo[i].fWidth / 2;
                    fHeight = pTvWallLayoutInfo.pScreenInfo[i].fHeight / 2;
                }
            } else {
                log.info(String.format("获取电视墙布局信息失败，nRet = %d", nRet));
            }

        }

        if (nScreenId > 0) {
            {
                TvWall_Screen_Split_t pInfo = new TvWall_Screen_Split_t();
                pInfo.nTvWallId = nTvWallId;
                pInfo.nScreenId = nScreenId;
                pInfo.enSplitNum = tvwall_screen_split_caps.Screen_Split_4;

                nRet = IDpsdkCore.DPSDK_SetTvWallScreenSplit(m_nDLLHandle, pInfo, 1000);
                if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                    log.info(String.format("分割窗口成功，nRet = %d", nRet));
                } else {
                    log.info(String.format("分割窗口失败，nRet = %d", nRet));
                    TvWall_Screen_Open_Window_t pOpenWindowInfo = new TvWall_Screen_Open_Window_t();
                    pOpenWindowInfo.nTvWallId = nTvWallId;
                    pOpenWindowInfo.nScreenId = nScreenId;
                    pOpenWindowInfo.fLeft = fLeft;
                    pOpenWindowInfo.fTop = fTop;
                    pOpenWindowInfo.fWidth = fWidth;
                    pOpenWindowInfo.fHeight = fHeight;
                    nRet = IDpsdkCore.DPSDK_TvWallScreenOpenWindow(m_nDLLHandle, pOpenWindowInfo, 10000);
                    if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                        log.info(String.format("窗口开窗成功，nRet = %d，nWindowId = %d。", nRet, pOpenWindowInfo.nWindowId));
                    } else {
                        log.info(String.format("窗口开窗失败，nRet = %d", nRet));
                    }
                }

            }
            SetWndSource(m_strTvWallSourceCamareID, nTvWallId, nScreenId, 0);
        }
    }

    void SetWndSource(String m_strTvWallSourceCamareID, int nTvWallId, int nScreenId, int nWndId) {
        {
            Set_TvWall_Screen_Window_Source_t pInfo = new Set_TvWall_Screen_Window_Source_t();
            pInfo.nTvWallId = nTvWallId;
            pInfo.nScreenId = nScreenId;
            pInfo.nWindowId = nWndId;
            pInfo.szCameraId = m_strTvWallSourceCamareID.getBytes();
            pInfo.enStreamType = dpsdk_stream_type_e.DPSDK_CORE_STREAMTYPE_MAIN;
            pInfo.nStayTime = 30;

            int nRet = IDpsdkCore.DPSDK_SetTvWallScreenWindowSource(m_nDLLHandle, pInfo, 1000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("设置视频源成功，nRet = %d", nRet));
            } else {
                log.info(String.format("设置视频源失败，nRet = %d", nRet));
            }

        }

        {
            TvWall_Screen_Close_Source_t pInfo = new TvWall_Screen_Close_Source_t();
            pInfo.nTvWallId = nTvWallId;
            pInfo.nScreenId = nScreenId;
            pInfo.nWindowId = nWndId;
            int nRet = IDpsdkCore.DPSDK_CloseTvWallScreenWindowSource(m_nDLLHandle, pInfo, 1000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("关闭视频源成功，nRet = %d", nRet));
            } else {
                log.info(String.format("关闭视频源失败，nRet = %d", nRet));
            }

        }

        {
            int nRet = IDpsdkCore.DPSDK_ClearTvWallScreen(m_nDLLHandle, nTvWallId, 1000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("清屏成功，nRet = %d", nRet));
            } else {
                log.info(String.format("清屏失败，nRet = %d", nRet));
            }

        }
    }

    public void MapTaskToTvWall(int nTvWallId) {
        Return_Value_Info_t nCount = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_GetTvWallTaskListCount(m_nDLLHandle, nTvWallId, nCount, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("查询电视墙任务列表数量成功，nRet = %d， nCount= %d", nRet, nCount.nReturnValue));
        } else {
            log.info(String.format("查询电视墙任务列表数量失败，nRet = %d", nRet));
        }

        int nCurTaskId = -1;
        if (nCount.nReturnValue > 0) {
            TvWall_Task_Info_List_t pTvWallTaskListInfo = new TvWall_Task_Info_List_t(nCount.nReturnValue);
            pTvWallTaskListInfo.nCount = nCount.nReturnValue;
            nRet = IDpsdkCore.DPSDK_GetTvWallTaskList(m_nDLLHandle, nTvWallId, pTvWallTaskListInfo, 10000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("获取电视墙任务列表信息成功，nRet = %d， nCount= %d", nRet, pTvWallTaskListInfo.nCount));
                for (int i = 0; i < pTvWallTaskListInfo.nCount; i++) {

                    log.info(String.format("序号=%d，nTaskId=%d，szName=%s, szDes=%s", i + 1,
                            pTvWallTaskListInfo.pTvWallTaskInfo[i].nTaskId,
                            new String(pTvWallTaskListInfo.pTvWallTaskInfo[i].szName).trim(),
                            new String(pTvWallTaskListInfo.pTvWallTaskInfo[i].szDes).trim()));

                    if (i == 0) {
                        nCurTaskId = pTvWallTaskListInfo.pTvWallTaskInfo[i].nTaskId;
                    }
                }
            } else {
                log.info(String.format("获取电视墙任务列表信息失败，nRet = %d", nRet));
            }

        }

        if (nCurTaskId >= 0) {
            Return_Value_Info_t nTaskInfoLen = new Return_Value_Info_t();
            nRet = IDpsdkCore.DPSDK_GetTvWallTaskInfoLen(m_nDLLHandle, nTvWallId, nCurTaskId, nTaskInfoLen, 10000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS && nTaskInfoLen.nReturnValue > 0) {
                byte[] szTaskInfoBuf = new byte[nGroupLen.nReturnValue];
                nRet = IDpsdkCore.DPSDK_GetTvWallTaskInfoStr(m_nDLLHandle, szTaskInfoBuf, nTaskInfoLen.nReturnValue);
                if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                    String TaskInfoBuf = "";
                    try {
                        TaskInfoBuf = new String(szTaskInfoBuf, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    log.info(String.format("查询电视墙任务信息成功，szTaskInfoBuf = %s", TaskInfoBuf));

                }

                nRet = IDpsdkCore.DPSDK_MapTaskToTvWall(m_nDLLHandle, nTvWallId, nCurTaskId, 10000);
                if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                    log.info(String.format("电视墙任务上墙成功。"));

                } else {
                    log.info(String.format("电视墙任务上墙失败，nRet = %d.", nRet));
                }

            }
        }
    }

    public String GetStringAsUTF8(byte[] data) {
        String str = "";
        try {
            str = new String(data, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            log.error("fatalError", e);
        }
        return str;
    }

    public void OnSetDoorCmd() {
        SetDoorCmd_Request_t pInfo = new SetDoorCmd_Request_t();
        String strDeviceID = "1000000$4$0$0"; // 设备ID
        pInfo.szCameraId = strDeviceID.getBytes();
        pInfo.cmd = dpsdk_SetDoorCmd_e.DPSDK_CORE_DOOR_CMD_ALWAYS_OPEN;
        pInfo.start = 10;
        pInfo.end = 110;

        int nRet = IDpsdkCore.DPSDK_SetDoorCmd(m_nDLLHandle, pInfo, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("DPSDK_SetDoorCmd:成功，nRet = %d", nRet));
        } else {
            log.info(String.format("DPSDK_SetDoorCmd:失败，nRet = %d", nRet));
        }

    }

    public void OnGetLinkResource() {
        Return_Value_Info_t nCount = new Return_Value_Info_t();
        int nRet = IDpsdkCore.DPSDK_QueryLinkResource(m_nDLLHandle, nCount, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("DPSDK_QueryLinkResource成功，nRet = %d， nCount= %d", nRet, nCount.nReturnValue));
        } else {
            log.info(String.format("DPSDK_QueryLinkResource失败，nRet = %d", nRet));
        }

        if (nCount.nReturnValue > 0) {
            GetLinkResource_Responce_t pResponse = new GetLinkResource_Responce_t(nCount.nReturnValue);

            nRet = IDpsdkCore.DPSDK_GetLinkResource(m_nDLLHandle, pResponse);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("DPSDK_GetLinkResource成功，nRet = %d， nCount= %d", nRet, pResponse.nLen));

                log.info(String.format("pXmlData=%s", new String(pResponse.pXmlData).trim()));

            } else {
                log.info(String.format("DPSDK_GetLinkResource失败，nRet = %d", nRet));
            }

        }
    }

    public void getSnapshot(String m_strRealCamareID) {

        JsonObject nap = new JsonObject();
        nap.addProperty("method", "dev.snap");
        JsonObject para = new JsonObject();
        para.addProperty("DevID", m_strRealCamareID);
        para.addProperty("DevChannel", 0);
        para.addProperty("PicNum", 1);
        para.addProperty("SnapType", 2);
        para.addProperty("CmdSrc", 1);
        nap.add("params", para);
        nap.addProperty("id", 88);
        Gson gson = new Gson();
        String szJson = gson.toJson(nap);

        int mdltype = dpsdk_mdl_type_e.DPSDK_MDL_DMS;
        int trantype = generaljson_trantype_e.GENERALJSON_TRAN_REQUEST;

        int nRet = IDpsdkCore.DPSDK_GeneralJsonTransport(m_nDLLHandle, szJson.getBytes(), mdltype, trantype, 30 * 1000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("DPSDK_GeneralJsonTransport:成功，nRet = %d", nRet));
        } else {
            log.info(String.format("DPSDK_GeneralJsonTransport:失败，nRet = %d", nRet));
        }

    }

    public int StartDownLoadRecordByTime(String m_strDownloadCamID, long begintime, long endtime) {
        Query_Record_Info_t queryInfo = new Query_Record_Info_t();
        Return_Value_Info_t nRecordCount = new Return_Value_Info_t();
        queryInfo.szCameraId = m_strDownloadCamID.getBytes();
        queryInfo.nRecordType = dpsdk_record_type_e.DPSDK_CORE_PB_RECORD_UNKONWN;// 下载模式
        queryInfo.nRight = dpsdk_check_right_e.DPSDK_CORE_NOT_CHECK_RIGHT; // 不检查权限，请求视频流，无需加载组织结构
        queryInfo.nSource = 2;// 设备录像
        queryInfo.uBeginTime = begintime;// 转换成秒;
        queryInfo.uEndTime = endtime;
        int nRet = IDpsdkCore.DPSDK_QueryRecord(m_nDLLHandle, queryInfo, nRecordCount, 60 * 1000);

        if (nRet != 0) {
            log.info(String.format("录像查询失败，nRet= %d", nRet));
            return -1;
        }

        if (nRecordCount.nReturnValue == 0) {
            log.info("没有录像！！！！！");

            return -1;
        }

        Return_Value_Info_t nDownLoadSeq = new Return_Value_Info_t();
        Get_RecordStream_Time_Info_t getInfo = new Get_RecordStream_Time_Info_t();
        getInfo.szCameraId = m_strDownloadCamID.getBytes();
        getInfo.nMode = 2;// 下载模式
        getInfo.nRight = dpsdk_check_right_e.DPSDK_CORE_NOT_CHECK_RIGHT; // 不检查权限，请求视频流，无需加载组织结构
        getInfo.nSource = 2;// 设备录像

        log.info(String.format("开始录像下载   begintime = %d， endtime = %d", begintime, endtime));
        getInfo.uBeginTime = begintime;// 转换成秒;
        getInfo.uEndTime = endtime;

        nRet = IDpsdkCore.DPSDK_GetRecordStreamByTime(m_nDLLHandle, nDownLoadSeq, getInfo, m_MediaDownloadCB, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("开始录像下载成功，nRet = %d， nSeq = %d", nRet, nDownLoadSeq.nReturnValue));
        } else {
            log.info(String.format("开始录像下载失败，nRet = %d", nRet));
        }

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            return nDownLoadSeq.nReturnValue;
        } else {
            return -1;
        }
    }

    public void StopDownLoadRecordByTime(int nDownloadSeq) {
        int nRet = IDpsdkCore.DPSDK_CloseRecordStreamBySeq(m_nDLLHandle, nDownloadSeq, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            // 关闭文件
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                    writer = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.info(String.format("停止下载成功，nRet = %d， nSeq = %d", nRet, nDownloadSeq));
        } else {
            log.info(String.format("停止下载失败，nRet = %d", nRet));
        }

    }

    public void SubscribeALLBayCarInfo() {
        Subscribe_Bay_Car_Info_t pGetInfo = new Subscribe_Bay_Car_Info_t(1);
        pGetInfo.nSubscribeFlag = 1;
        pGetInfo.nChnlCount = 0;
        int nRet = IDpsdkCore.DPSDK_SubscribeBayCarInfo(m_nDLLHandle, pGetInfo, 5000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("订阅所有卡口过车信息成功，nRet = %d", nRet));
        } else {
            log.info(String.format("订阅所有卡口过车信息失败，nRet = %d", nRet));
        }

    }

    public void QueryPersonCount(String m_strPersonCountCamID) {
        Return_Value_Info_t nQuerySeq = new Return_Value_Info_t();
        Return_Value_Info_t nTotalCount = new Return_Value_Info_t();
        Date tmStart = new Date(2017 - 1900, 3 - 1, 31, 0, 0, 0);
        Date tmEnd = new Date(2017 - 1900, 3 - 1, 31, 23, 59, 59);
        int nStartTime = (int) (tmStart.getTime() / 1000);
        int nEndTime = (int) (tmEnd.getTime() / 1000);
        int nGranularity = 2;
        int nRet = IDpsdkCore.DPSDK_QueryPersonCount(m_nDLLHandle, m_strPersonCountCamID.getBytes(), nQuerySeq,
                nTotalCount, nStartTime, nEndTime, nGranularity, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("查询人数统计总数成功，nRet = %d", nRet));
        } else {
            log.info(String.format("查询人数统计总数失败，nRet = %d", nRet));
        }

        if (nTotalCount.nReturnValue > 0) {
            Person_Count_Info_t[] pPersonInfo = new Person_Count_Info_t[nTotalCount.nReturnValue];
            nRet = IDpsdkCore.DPSDK_QueryPersonCountBypage(m_nDLLHandle, m_strPersonCountCamID.getBytes(),
                    nQuerySeq.nReturnValue, 0, nTotalCount.nReturnValue, pPersonInfo, 10000);
            if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
                log.info(String.format("分页查询统计结果成功，nRet = %d", nRet));
                for (int i = 0; i < nTotalCount.nReturnValue; i++) {

                    log.info(String.format(
                            "统计通道号=%d, 规则名称=%s, 开始时间=%d, 结束时间=%d, 进入人数小计=%d, 出去人数小计=%d, 平均保有人数=%d, 最大保有人数=%d",
                            pPersonInfo[i].nChannelID, pPersonInfo[i].szRuleName, pPersonInfo[i].nStartTime,
                            pPersonInfo[i].nEndTime, pPersonInfo[i].nEnteredSubTotal, pPersonInfo[i].nExitedSubtotal,
                            pPersonInfo[i].nAvgInside, pPersonInfo[i].nMaxInside));
                }
            } else {
                log.info(String.format("分页查询统计结果失败，nRet = %d", nRet));
            }

            IDpsdkCore.DPSDK_StopQueryPersonCount(m_nDLLHandle, m_strPersonCountCamID.getBytes(),
                    nQuerySeq.nReturnValue, 100000);
        }
    }



    public void ptzMove(String m_strRealCamareID, int direct) {

        Ptz_Direct_Info_t ptz = new Ptz_Direct_Info_t();
        ptz.szCameraId = m_strRealCamareID.getBytes();
        ptz.bStop = new Boolean(false);
        ptz.nStep = 2;
        ptz.nDirect = direct;

        int nRet = IDpsdkCore.DPSDK_PtzDirection(m_nDLLHandle, ptz, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            System.out.printf("move:成功，nRet = %d", nRet);
        } else {
            System.out.printf("move:失败，nRet = %d", nRet);
        }
        System.out.println();

    }

    public void ptzStop(String m_strRealCamareID) {

        Ptz_Direct_Info_t ptz = new Ptz_Direct_Info_t();
        ptz.szCameraId = m_strRealCamareID.getBytes();
        ptz.bStop = new Boolean(true);

        int nRet = IDpsdkCore.DPSDK_PtzDirection(m_nDLLHandle, ptz, 10000);

        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            System.out.printf("move:成功，nRet = %d", nRet);
        } else {
            System.out.printf("move:失败，nRet = %d", nRet);
        }
        System.out.println();

    }

    public void setOSD(String m_strRealCamareID, String OSD) {
        OSD_Info_t strOSDInfo = new OSD_Info_t();
        strOSDInfo.szChannelId = m_strRealCamareID.getBytes();
        strOSDInfo.strOSDText = OSD.getBytes();
        strOSDInfo.enumAlign = OSDTextAlign_e.OSD_TEXT_ALIGN_CENTER;
        strOSDInfo.nForeground = 0;
        strOSDInfo.nBackground = 0;
        strOSDInfo.nLeft = 1072;
        strOSDInfo.nTop = 200;
        strOSDInfo.nRight = 1072;
        strOSDInfo.nBottom = 200;
        strOSDInfo.nRemainTime = 0; // osd叠加持续时间,0 为一直叠加
        int nRet = IDpsdkCore.DPSDK_SetOSDInfo(m_nDLLHandle, strOSDInfo, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            System.out.printf("Set OSD successful, nRet = %d", nRet);
        } else {
            System.out.printf("Set OSD failed, nRet = %d", nRet);
        }
        System.out.println();

    }





    public String focusZoom(String m_strRealCamareID, int command) {
        Ptz_Operation_Info_t operationInfo = new Ptz_Operation_Info_t();
        operationInfo.szCameraId = m_strRealCamareID.getBytes();
        operationInfo.bStop = new Boolean(false);
        operationInfo.nStep = 2;
        operationInfo.nOperation = command;
        int nRet = IDpsdkCore.DPSDK_PtzCameraOperation(m_nDLLHandle, operationInfo, 10000);
        operationInfo.bStop = new Boolean(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IDpsdkCore.DPSDK_PtzCameraOperation(m_nDLLHandle, operationInfo, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            return  ("Command:成功，nRet ="+ nRet);
        } else {
            return  ("Command:失败，nRet ="+ nRet);
        }
    }

    public String getPreset(String m_strRealCamareID) {
        Ptz_Prepoint_Info_t prePointInfo = new Ptz_Prepoint_Info_t();
        prePointInfo.szCameraId = m_strRealCamareID.getBytes();
        prePointInfo.pPoints = new Ptz_Single_Prepoint_Info_t[dpsdk_constant_value.DPSDK_CORE_POINT_COUNT];
        int nRet = IDpsdkCore.DPSDK_QueryPrePoint(m_nDLLHandle, prePointInfo, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            JsonArray preset = new JsonArray();
            for (int i = 0; i < prePointInfo.nCount; i++) {
                JsonObject object = new JsonObject();
                String s = new String(prePointInfo.pPoints[i].szName);
                int n = prePointInfo.pPoints[i].nCode;
                s = s.trim();
                object.addProperty("编号", n);
                object.addProperty("name", s);
                preset.add(object);
            }
            return preset.toString();

        } else {
            return "there is no preset exits in current system";
        }

    }

    public void setPreset(String m_strRealCamareID, String presetName, int presetIndex) {
        Ptz_Single_Prepoint_Info_t prepoint = new Ptz_Single_Prepoint_Info_t();
        prepoint.nCode = presetIndex;
        prepoint.szName= presetName.getBytes();
        Ptz_Prepoint_Operation_Info_t operation = new Ptz_Prepoint_Operation_Info_t();

        operation.nCmd = 2;
        operation.szCameraId = m_strRealCamareID.getBytes();
        operation.pPoints = prepoint;

        int nRet = IDpsdkCore.DPSDK_PtzPrePointOperation(m_nDLLHandle, operation, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            System.out.printf("preset operation:成功，nRet = %d", nRet);
        } else {
            System.out.printf("preset operation:失败，nRet = %d", nRet);

        }
    }
    public void goToPreset(String m_strRealCamareID, int presetIndex) {
        Ptz_Single_Prepoint_Info_t prepoint = new Ptz_Single_Prepoint_Info_t();
        prepoint.nCode = presetIndex;
        Ptz_Prepoint_Operation_Info_t operation = new Ptz_Prepoint_Operation_Info_t();
        operation.nCmd = 1;
        operation.szCameraId = m_strRealCamareID.getBytes();
        operation.pPoints = prepoint;

        int nRet = IDpsdkCore.DPSDK_PtzPrePointOperation(m_nDLLHandle, operation, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            System.out.printf("preset operation:成功，nRet = %d", nRet);
        } else {
            System.out.printf("preset operation:失败，nRet = %d", nRet);

        }
    }


    public void delPreset(String m_strRealCamareID, int presetIndex) {

        Ptz_Single_Prepoint_Info_t prepoint = new Ptz_Single_Prepoint_Info_t();
        prepoint.nCode = presetIndex;
        Ptz_Prepoint_Operation_Info_t operation = new Ptz_Prepoint_Operation_Info_t();
        operation.nCmd = 3;
        operation.szCameraId = m_strRealCamareID.getBytes();
        operation.pPoints = prepoint;
        int nRet = IDpsdkCore.DPSDK_PtzPrePointOperation(m_nDLLHandle, operation, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            System.out.printf("preset operation:成功，nRet = %d", nRet);
        } else {
            System.out.printf("preset operation:失败，nRet = %d", nRet);
        }

    }

    public void SubscribeAreaSpeedDetectInfo() {
        int nSubscribeFlag = 1;
        int nRet = IDpsdkCore.DPSDK_SubscribeAreaSpeedDetectInfo(m_nDLLHandle, nSubscribeFlag, 5000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("订阅区间测速上报成功，nRet = %d", nRet));
        } else {
            log.info(String.format("订阅区间测速上报失败，nRet = %d", nRet));
        }

    }

    /*
     * 登出
     */
    public void OnLogout() {
        int nRet = IDpsdkCore.DPSDK_Logout(m_nDLLHandle, 10000);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("登出成功，nRet = %d", nRet));
        } else {
            log.info(String.format("登出失败，nRet = %d", nRet));
        }

    }

    /*
     * 释放内存
     */
    public void OnDestroy() {
        int nRet = IDpsdkCore.DPSDK_Destroy(m_nDLLHandle);
        if (nRet == dpsdk_retval_e.DPSDK_RET_SUCCESS) {
            log.info(String.format("释放内存成功，nRet = %d", nRet));
        } else {
            log.info(String.format("释放内存失败，nRet = %d", nRet));
        }

    }

}
