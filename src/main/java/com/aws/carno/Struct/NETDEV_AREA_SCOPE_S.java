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
 * @struct tagNETDEVAreaScope
 * @brief 区域 Area
 * @attention 无 None
 */
public class NETDEV_AREA_SCOPE_S extends Structure {
    public int dwLocateX;             /** 顶点x坐标值[0,10000] * Coordinates of top point x [0,10000] */
    public int dwLocateY;             /** 顶点y坐标值[0,10000] * Coordinates of top point y [0,10000] */
    
    public static class ByValue extends NETDEV_AREA_SCOPE_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_AREA_SCOPE_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"dwLocateX", "dwLocateY"});
    }
}
