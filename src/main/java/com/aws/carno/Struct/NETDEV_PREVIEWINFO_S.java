/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Struct;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVPriviewInfo
 * @brief 实况预览参数 结构体定义 Live view parameter Structure definition
 * @attention 无 None
 */
public class NETDEV_PREVIEWINFO_S extends Structure {
    public int dwChannelID;                    /* 通道ID  Channel ID */
    public int dwStreamType;                   /* 码流类型,参见枚举#NETDEV_LIVE_STREAM_INDEX_E  Stream type, see enumeration #NETDEV_LIVE_STREAM_INDEX_E */
    public int dwLinkMode;                     /* 传输协议,参见枚举#NETDEV_PROTOCAL_E  Transport protocol, see enumeration #NETDEV_PROTOCAL_E */
    public Pointer hPlayWnd;                   /* 播放窗口句柄 Play window handle */ 
    public int dwFluency;                      /* 图像播放流畅性优先类型,参见枚举#NETDEV_PICTURE_FLUENCY_E */
    public byte[] byRes = new byte[260];       /* 保留字段  Reserved */
    
    public static class ByValue extends NETDEV_PREVIEWINFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_PREVIEWINFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"dwChannelID", "dwStreamType", "dwLinkMode", "hPlayWnd", "dwFluency", "byRes"});
    }
}