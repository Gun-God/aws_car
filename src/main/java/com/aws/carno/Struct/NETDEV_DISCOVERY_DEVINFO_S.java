/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNetDEVDiscoveryDevInfo
 * @brief 设备发现的设备信息  Info of discovered device
 * @attention 无 None
 */
public class NETDEV_DISCOVERY_DEVINFO_S extends Structure
{
    public byte[] szDevAddr = new byte[NetDEVEnum.NETDEV_LEN_64];         /* 设备地址  Device address */
    public byte[] szDevModule = new byte[NetDEVEnum.NETDEV_LEN_64];       /* 设备型号  Device model */
    public byte[] szDevSerailNum = new byte[NetDEVEnum.NETDEV_LEN_64];    /* 设备序列号  Device serial number */
    public byte[] szDevMac = new byte[NetDEVEnum.NETDEV_LEN_64];          /* 设备MAC地址  Device MAC address */
    public byte[] szDevName = new byte[NetDEVEnum.NETDEV_LEN_64];         /* 设备名称  Device name */
    public byte[] szDevVersion = new byte[NetDEVEnum.NETDEV_LEN_64];      /* 设备版本  Device version */
    public int enDevType;                                                 /* 设备类型  Device type */
    public int dwDevPort;                                                 /* 设备端口号  Device port number */
    public byte[] szManuFacturer = new byte[NetDEVEnum.NETDEV_LEN_64];    /* 生产商 Device manufacture */
    public byte[] byRes = new byte[196];                                  /* 保留字段  Reserved */
    
    public static class ByValue extends NETDEV_DISCOVERY_DEVINFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_DISCOVERY_DEVINFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder()
    {
        return Arrays.asList(new String[]{"szDevAddr", "szDevModule", "szDevSerailNum", "szDevMac", "szDevName",
                             "szDevVersion", "enDevType", "dwDevPort", "szManuFacturer", "byRes"});
    }   
}