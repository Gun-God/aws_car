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
 * @struct tagNETDEVCarportCfg
 * @brief 车位信息
 * @attention 无
 */
public class NETDEV_CARPORT_CFG_S extends Structure {
    public byte[] szArea = new byte[64];
    public NETDEV_IVA_PARK_AREA_INFO_S[] astParkAreaInfo = new NETDEV_IVA_PARK_AREA_INFO_S[6];    /**< 车位区域信息 */
    
    public NETDEV_CARPORT_CFG_S() {
        super();
        read();
    }
    
    public NETDEV_CARPORT_CFG_S(Pointer pointer) {
        super(pointer);
        read();
    }
    
    public static class ByValue extends NETDEV_CARPORT_CFG_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_CARPORT_CFG_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"szArea", "astParkAreaInfo"});
    }
}