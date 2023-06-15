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
 * @struct tagNETDEVParkStatusInfo
 * @brief 所有车位状态信息
 * @attention 无
 */
public class NETDEV_PARKSTATUS_INFO_S extends Structure {
    public int ulParkNum;                                                        /**< 车位数量 */
    public NETDEV_PARK_STATUS_S[] astParkSatus = new NETDEV_PARK_STATUS_S[6];    /**< 车位状态信息 */
    
    public NETDEV_PARKSTATUS_INFO_S() {
        super();
        read();
    }
    
    public NETDEV_PARKSTATUS_INFO_S(Pointer pointer) {
        super();
        read();
    }
    
    public static class ByValue extends NETDEV_PARKSTATUS_INFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_PARKSTATUS_INFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"ulParkNum", "astParkSatus"});
    }
}