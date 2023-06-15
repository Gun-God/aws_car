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
* @struct tagNETDEVParkStatus
* @brief 车位状态信息
* @attention
*/
public class NETDEV_PARK_STATUS_S extends Structure {
    public byte[] szCamID = new byte[NetDEVEnum.NETDEV_DEV_ID_MAX_LEN];               /**< 相机编号 */
    public int lParkID;                                                               /**< 车位编号 */
    public byte[] szSampleTime = new byte[NetDEVEnum.NETDEV_UNIVIEW_MAX_TIME_LEN];    /**< 采样时刻:YYYYMMDDHHMMSS, 时间按24小时制 */
    public byte[] cReserved = new byte[2];                                            /**< 保留字段 字节对齐用 */
    public int lParkingLotStatus;                                                     /**< 车位状态，0无车，1有车，2识别异常 */
    public byte[] szCarPlate = new byte[NetDEVEnum.NETDEV_CAR_PLATE_MAX_LEN];         /**< 车牌号码:不能自动识别的用"-"表示，车位无车时忽略该字段 */
    public int lLEDStatus;                                                            /**< 车位指示灯状态 0熄灭，1长亮，2快速闪烁，3慢速闪烁 */
    public int lLEDColor;                                                             /**< 车位指示灯颜色 1红色，2绿色，3黄色 */
    public int lCrossAlarm;                                                           /**< 跨车位告警 0未跨车位，1跨车位 */

    public static class ByValue extends NETDEV_PARK_STATUS_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_PARK_STATUS_S implements Structure.ByReference {}
    
    @Override
    protected List<String> getFieldOrder()
    {
        return Arrays.asList(new String[]{"szCamID", "lParkID", "szSampleTime", "cReserved", "lParkingLotStatus", "szCarPlate", "lLEDStatus", "lLEDColor", "lCrossAlarm"});
    }
}