/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Struct;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVDeviceInfo
 * @brief 设备信息 结构体定义 Device information Structure definition
 * @attention 无 None
 */
public class NETDEV_DEVICE_INFO_S extends Structure {
    public int dwDevType;                          /* 设备类型,参见枚举#NETDEV_DEVICETYPE_E  Device type, see enumeration #NETDEV_DEVICETYPE_E */
    public short wAlarmInPortNum;                  /* 报警输入个数  Number of alarm inputs */
    public short wAlarmOutPortNum;                 /* 报警输出个数  Number of alarm outputs */
    public int dwChannelNum;                       /* 通道个数  Number of Channels */
    public byte[] byRes = new byte[48];            /* 保留字段  Reserved */
    
    public static class ByValue extends NETDEV_DEVICE_INFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_DEVICE_INFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder()
    {
        return Arrays.asList(new String[]{"dwDevType", "wAlarmInPortNum", "wAlarmOutPortNum", "dwChannelNum", "byRes"});
    }
}
