/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Enum;

/**
 * @enum tagNETDEVCfgCmd
 * @brief 参数配置命令字 枚举定义 Parameter configuration command words Enumeration definition
 * @attention 无 None
 */
public class NETDEV_CONFIG_COMMAND_E {
    public static final int NETDEV_GET_DEVICECFG = 100;              /* 获取设备信息,参见#NETDEV_DEVICE_BASICINFO_S  Get device information, see #NETDEV_DEVICE_BASICINFO_S */
    public static final int NETDEV_GET_OSD_CONTENT_CFG = 144;        /* 获取OSD配置信息(扩展，建议使用),参见#NETDEV_OSD_CONTENT_S  Get OSD configuration information, see #NETDEV_OSD_CONTENT_S */
    public static final int NETDEV_SET_OSD_CONTENT_CFG = 145;        /* 设置OSD配置信息(扩展，建议使用),参见#NETDEV_OSD_CONTENT_S  Set OSD configuration information, see #NETDEV_OSD_CONTENT_S */
    public static final int NETDEV_GET_OSD_CONTENT_STYLE_CFG = 146;  /* 获取OSD内容样式,参见#NETDEV_OSD_CONTENT_STYLE_S  Get OSD content style, see #NETDEV_OSD_CONTENT_STYLE_S */
    public static final int NETDEV_SET_OSD_CONTENT_STYLE_CFG = 147;  /* 设置OSD内容样式,参见#NETDEV_OSD_CONTENT_STYLE_S  Set OSD content style, see #NETDEV_OSD_CONTENT_STYLE_S */
    public static final int NETDEV_GET_NETWORKCFG = 170;             /* 获取网络配置信息,参见#NETDEV_NETWORKCFG_S  Get network configuration information, see #NETDEV_NETWORKCFG_S */
    public static final int NETDEV_SET_NETWORKCFG = 171;             /* 设置网络配置信息,参见#NETDEV_NETWORKCFG_S  Set network configuration information, see #NETDEV_NETWORKCFG_S */
    public static final int NETDEV_GET_PARKSTATUSINFO = 1000;        /* 获取车位状态信息 参见#NETDEV_PARKSTATUS_INFO_S */
    public static final int NETDEV_GET_CARPORTCFG = 1010;            /* 获取车位信息，参见#NETDEV_CARPORT_CFG_S */
    public static final int NETDEV_GET_INFOOSDCFG = 1030;            /* 获取叠加OSD配置 参见 NETDEV_INFOOSD_CFG_S */
    public static final int NETDEV_SET_INFOOSDCFG = 1031;            /* 设置叠加OSD配置 参见 NETDEV_INFOOSD_CFG_S */
}