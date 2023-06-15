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
 * @struct tagNETDEVPolygon
 * @brief 多边形区域坐标结构
 * @attention 无
 */
public class NETDEV_POLYGON_S extends Structure {
    public int ulNum;                                                        /**< 有效点数 */
    public NETDEV_AREA_SCOPE_S[]  astPoint = new NETDEV_AREA_SCOPE_S[12];    /**< 多边形端点坐标 */
    
    public static class ByValue extends NETDEV_POLYGON_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_POLYGON_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"ulNum", "astPoint"});
    }
}
