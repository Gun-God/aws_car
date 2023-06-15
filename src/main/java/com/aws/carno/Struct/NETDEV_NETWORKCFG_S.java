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
 * @struct tagNETDEVNetworkInterfaces
 * @brief 网络配置信息 结构体定义 Network configuration information
 * @attention 无 None
 */
public class NETDEV_NETWORKCFG_S extends Structure
{
    public int dwMTU;                                                       /* MTU值  MTU value */
    public int dwIPv4DHCP;                                                   /* IPv4的DHCP  DHCP of IPv4 */
    public byte[] szIpv4Address = new byte[NetDEVEnum.NETDEV_LEN_32];       /* IPv4的IP地址  IP address of IPv4 */
    public byte[] szIPv4GateWay = new byte[NetDEVEnum.NETDEV_LEN_32];       /* IPv4的网关地址  Gateway of IPv4 */
    public byte[] szIPv4SubnetMask = new byte[NetDEVEnum.NETDEV_LEN_32];    /* IPv4的子网掩码  Subnet mask of IPv4 */
    public byte[] byRes = new byte[480];                                    /* 保留字段  Reserved */
    
    public static class ByValue extends NETDEV_NETWORKCFG_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_NETWORKCFG_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder()
    {
        return Arrays.asList(new String[]{"dwMTU", "dwIPv4DHCP", "szIpv4Address", "szIPv4GateWay", "szIPv4SubnetMask", "byRes"});
    }
}