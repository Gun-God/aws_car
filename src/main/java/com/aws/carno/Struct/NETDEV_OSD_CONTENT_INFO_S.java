package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVOSDContentInfo
 * @brief 通道OSD内容信息 Channel OSD content
 * @attention
 */
public class NETDEV_OSD_CONTENT_INFO_S extends Structure {
    public int bEnabled;                                        /* OSD区域使能 Enable OSD area*/
    public int udwOSDID;                                        /* OSD区域序号，范围[0,7] Area No., ranges from 0 to 7.*/
    public int udwAreaOSDNum;                                   /* 当前区域内OSD数目 Number of OSD in current area*/
    public int udwTopLeftX;                                     /* OSD区域横坐标,范围[0,9999] X-axis of OSD area, ranges from 0 to 999*/
    public int udwTopLeftY;                                     /* OSD区域纵坐标,范围[0,9999] Y-axisof OSD area, ranges from 0 to 999*/
    public int udwBotRightX;                                    /* old无此参数，OSD区域横坐标,范围[0,9999] X-axis of OSD area, ranges from 0 to 999*/
    public int udwBotRightY;                                    /* old无此参数，OSD区域纵坐标,范围[0,9999] Y-axisof OSD area, ranges from 0 to 999*/
    public NETDEV_CONTENT_INFO_S[] astContentInfo  = new NETDEV_CONTENT_INFO_S[NetDEVEnum.NETDEV_LEN_8];     /* 当前区域内OSD内容信息 OSD content in current area*/
    
    public NETDEV_OSD_CONTENT_INFO_S() {
        super();
        read();
    }
    
    public NETDEV_OSD_CONTENT_INFO_S(Pointer pointer) {
        super(pointer);
        read();
    }
    
    public static class ByValue extends NETDEV_OSD_CONTENT_INFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_OSD_CONTENT_INFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"bEnabled", "udwOSDID", "udwAreaOSDNum", "udwTopLeftX", "udwTopLeftY", "udwBotRightX", "udwBotRightY", "astContentInfo"});
    }
}