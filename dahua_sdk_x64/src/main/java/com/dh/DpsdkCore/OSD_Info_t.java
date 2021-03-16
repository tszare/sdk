package com.dh.DpsdkCore;

public class OSD_Info_t {

    public byte[]			szChannelId = new byte[dpsdk_constant_value.DPSDK_CORE_DEV_ID_LEN];				// 通道ID
    public byte[]			strOSDText = new byte[dpsdk_constant_value.DPSDK_CORE_OSDTEMPLAT_CONTENT_LEN];	// OSD信息

    public int				nForeground;			//前景色,默认0
    public int				nBackground;			//背景色,默认0
    public int				nLeft;					//显示位置，默认5402
    public int				nTop;					//默认797
    public int				nRight;					//默认5402
    public int				nBottom;				//默认797
    public int				enumAlign;				//OSD Text 对齐方式默认OSD_TEXT_ALIGN_LEFT,左对齐 参考定义OSDTextAlign_e
    public int				nRemainTime;			//osd叠加持续时间,0 为一直叠加
    public OSD_Info_t()
    {

    }



}