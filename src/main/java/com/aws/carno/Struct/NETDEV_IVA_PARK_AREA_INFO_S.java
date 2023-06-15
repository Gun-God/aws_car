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
 * @struct tagNETDEVParkAreaInfo
 * @brief 车位区域信息
 * @attention 无
 */
public class NETDEV_IVA_PARK_AREA_INFO_S extends Structure {
    public int ulParkDetstaus;                     /**< 检测使能标志, 0表示不使能,1表示使能,2表示无效 */
    public int ulParkAreaID;                       /**< 车位号 */
    public NETDEV_POLYGON_S stParkAreaLocation;    /**< 车位区域坐标,多边形，最多支持6个点 */
    
    public static class ByValue extends NETDEV_IVA_PARK_AREA_INFO_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_IVA_PARK_AREA_INFO_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"ulParkDetstaus", "ulParkAreaID", "stParkAreaLocation"});
    }
}