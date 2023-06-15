/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVDeviceBasicInfo
 * @brief 设备基本信息 结构体定义 Basic device information Structure definition
 * @attention 无 None
 */
public class NETDEV_DEVICE_BASICINFO_S extends Structure {
    public byte[] szDevModel = new byte[NetDEVEnum.NETDEV_LEN_64];           /* 设备型号  Device model */
    public byte[] szSerialNum = new byte[NetDEVEnum.NETDEV_LEN_64];          /* 硬件序列号  Hardware serial number */
    public byte[] szFirmwareVersion = new byte[NetDEVEnum.NETDEV_LEN_64];    /* 软件版本号  Software version */
    public byte[] szMacAddress = new byte[NetDEVEnum.NETDEV_LEN_64];         /* IPv4的Mac地址  MAC address of IPv4 */
    public byte[] szDeviceName = new byte[NetDEVEnum.NETDEV_LEN_64];         /* 设备名称  Device name */
    public byte[] byRes = new byte[448];                                     /* 保留字段  Reserved */
    
    public NETDEV_DEVICE_BASICINFO_S() {
        super();
        read();
    }
    
    public NETDEV_DEVICE_BASICINFO_S(Pointer pointer) {
        super(pointer);
        read();
    }
    
    public static class ByValue extends NETDEV_DEVICE_BASICINFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_DEVICE_BASICINFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"szDevModel", "szSerialNum", "szFirmwareVersion", "szMacAddress", "szDeviceName", "byRes"});
    }
}