/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.carno.Struct;

import com.aws.carno.Enum.NetDEVEnum;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

import java.util.Arrays;
import java.util.List;

/**
 * @struct tagNETDEVPicData
 * @brief 交通类对外数据结构，最多包含8张照片
 * @attention 对外接口
 */
public class NETDEV_PIC_DATA_S extends Structure {
    public ByteByReference[] apcData = new ByteByReference[NetDEVEnum.NETDEV_TRAFFIC_PIC_MAX_NUM];  /* 数据指针 */
    public int[] aulDataLen = new int[NetDEVEnum.NETDEV_TRAFFIC_PIC_MAX_NUM];                       /* 数据长度 */
    public int[] aulPicType = new int[NetDEVEnum.NETDEV_TRAFFIC_PIC_MAX_NUM];                       /* 照片类型, 参照:IMOS_MW_IMAGE_VEHICLE */
    public byte[] acPassTime = new byte[NetDEVEnum.NETDEV_TRAFFIC_PIC_MAX_NUM * NetDEVEnum.NETDEV_UNIVIEW_MAX_TIME_LEN];     /* 经过时间 */
    public int ulPicNumber;                                                                          /* 照片张数*/
    
    /* 设备信息 */
    public int lApplicationType;                                                         /* 应用类型:对应相关产品 */
    public byte[] szCamID = new byte[NetDEVEnum.NETDEV_DEV_ID_MAX_LEN];                /* 设备编号:采集设备统一编号或卡口相机编码, 不可为空 */
    public byte[] szTollgateID = new byte[NetDEVEnum.NETDEV_DEV_ID_MAX_LEN];           /* 卡口编号:产生该信息的卡口代码 */
    public byte[] szTollgateName = new byte[NetDEVEnum.NETDEV_TOLLGATE_NAME_MAX_LEN];  /* 卡口名称:可选字段 */
    public int ulCameraType;                    /* 相机类型 0 全景 1特性 */
    public int ulRecordID;                      /* 车辆信息编号:由1开始自动增长(转换成字符串要求不超过16字节) */

    /* 时间、地点信息 */
    public byte[] szPassTime = new byte[NetDEVEnum.NETDEV_UNIVIEW_MAX_TIME_LEN];     /* 经过时刻:YYYYMMDDHHMMSS, 时间按24小时制 */
    public byte[] szPlaceName = new byte[NetDEVEnum.NETDEV_PLACE_NAME_MAX_LEN];      /* 地点名称 */
    public int lLaneID;                         /*  车道编号:从1开始, 车辆行驶方向最左车道为1，由左向右顺序编号  */
    public int lLaneType;                       /*  车道类型:0-机动车道，1-非机动车道 */

    /*  方向编号:1-东向西 2-西向东 3-南向北 4-北向南  5-东南向西北 6-西北向东南 7-东北向西南 8-西南向东北  */
    public int lDirection;
    public byte[] szDirectionName = new byte[NetDEVEnum.NETDEV_DIRECTION_NAME_MAX_LEN];     /* 方向名称:可选字段 */

    /* 车牌信息 */
    public byte[] szCarPlate = new byte[NetDEVEnum.NETDEV_CAR_PLATE_MAX_LEN];     /* 号牌号码:不能自动识别的用"-"表示 */
    public int[] aulLPRRect = new int[4];                                           /* 车牌坐标:XL=a[0], YL=a[1], XR=a[2], YR=a[3] */
    public int lPlateType;                                                          /* 号牌种类:按GA24.7编码 */
    public int lPlateColor;                                                         /* 号牌颜色:0-白色1-黄色 2-蓝色 3-黑色 4-其他 */
    public int lPlateNumber;                                                        /* 号牌数量 */

    /* 号牌一致:
        0-车头和车尾号牌号码不一致
        1-车头和车尾号牌号码完全一致
        2-车头号牌号码无法自动识别
        3-车尾号牌号码无法自动识别
        4-车头和车尾号牌号码均无法自动识别 */
    public int lPlateCoincide;
    public byte[] szRearVehiclePlateID = new byte[NetDEVEnum.NETDEV_CAR_PLATE_MAX_LEN];        /* 尾部号牌号码:被查控车辆车尾号牌号码，允许车辆尾部号牌号码不全。不能自动识别的用"-"表示 */
    public int lRearPlateColor;                                                                /* 尾部号牌颜色: 0-白色1-黄色 2-蓝色 3-黑色 4-其他 */
    public int lRearPlateType;                                                                 /* 尾部号牌种类: 按GA24.7编码; 或者1－单排 2－武警 3－警用 4－双排 5－其他 */

    /* 车辆信息 */
    public int[] aulVehicleXY = new int[4];                                                    /* 车辆坐标:XL=a[0], YL=a[1], XR=a[2], YR=a[3] */
    public byte[] szVehicleBrand = new byte[NetDEVEnum.NETDEV_CAR_VEHICLE_BRAND_LEN];          /* 车辆厂牌编码(自行编码) 考虑到字节对齐定义长度为4，实际使用长度为2 */
    public int lVehicleBody;                                                                   /* 车辆外型编码(自行编码) */
    public int lVehicleType;                                                                   /* 车辆类型 0-未知，1-小型车 2-中型车 3-大型车 4-其他 */
    public int lVehicleLength;                                                                 /* 车外廓长(以厘米为单位) */
    public int lVehicleColorDept;                                                              /* 车身颜色深浅:0-未知，1-浅，2-深 */

    /* 车身颜色:
        A：白，B：灰，C：黄，D：粉，E：红，F：紫，G：绿，H：蓝，
        I：棕，J：黑，K：橙，L：青，M：银，N：银白，Z：其他(!) */
    public byte cVehicleColor;                                                                  /* 车身颜色 */
    /* 识别，注:后面的UCHAR紧跟CHAR */
    public byte ucPlateScore;                                                                   /* 此次识别中，整牌的置信度，100最大 */
    public byte ucRearPlateScore;                                                               /* 尾部号码置信度，100最大 */
    public byte ucPicType;                                                                      /* 0:实时照片，1:历史照片 */
    public int lIdentifyStatus;                                                                 /* 识别状态:0－识别成功 1－不成功 2－不完整(!)  3-表示需要平台识别 */
    public int lIdentifyTime;                                                                   /* 识别时间, 单位毫秒 */
    public int lDressColor;                                                                     /* 行人衣着颜色(!) */
    public int lDealTag;                                                                        /* 处理标记:0-初始状态未校对 1-已校对和保存 2-无效信息 3-已处理和保存(!) */

    /* 车速 */
    public int lVehicleSpeed;                                                                    /* 车辆速度: 单位km/h, -1-无测速功能 */
    public int lLimitedSpeed;                                                                    /* 执法限速: 车辆限速, 单位km/h */
    public int lMarkedSpeed;                                                                     /* 标识限速 */
    public int lDriveStatus;                                                                     /* 行驶状态:0-正常 1-嫌疑或按GA408.1编码 */

    /* 红灯信息 */
    public int lRedLightTime;                                                                    /* 红灯时间 */
    public byte[] szRedLightStartTime = new byte[NetDEVEnum.NETDEV_UNIVIEW_MAX_TIME_LEN];        /* 红灯开始时间:YYYYMMDDHHMMSS, 精确到毫秒, 时间按24小时制 */
    public byte[] szRedLightEndTime = new byte[NetDEVEnum.NETDEV_UNIVIEW_MAX_TIME_LEN];          /* 红灯结束时间:YYYYMMDDHHMMSS, 精确到毫秒, 时间按24小时制 */
    public byte[] szDriveStatus = new byte[NetDEVEnum.NETDEV_PECCANCYTYPE_CODE_MAX_NUM + 4];     /* 行驶状态:0-正常 1-嫌疑或按GA408.1编码, 支持字符串，为了兼容不删除lDriveStatus  */
    public int lTriggerType;                                                                     /* 抓拍类型 参考NETDEV_CAPTURE_TYPE_E */
    
    public NETDEV_PIC_DATA_S() {
        super();
        read();
    }
    
    public NETDEV_PIC_DATA_S(Pointer pointer) {
        super(pointer);
        read();
    }

    public static class ByValue extends NETDEV_PIC_DATA_S implements Structure.ByValue {}
    public static class ByReference extends NETDEV_PIC_DATA_S implements Structure.ByReference {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[]{"apcData", "aulDataLen", "aulPicType", "acPassTime", "ulPicNumber", "lApplicationType", "szCamID", "szTollgateID",
                "szTollgateName", "ulCameraType", "ulRecordID", "szPassTime", "szPlaceName", "lLaneID", "lLaneType", "lDirection", "szDirectionName",
                "szCarPlate", "aulLPRRect", "lPlateType", "lPlateColor", "lPlateNumber", "lPlateCoincide", "szRearVehiclePlateID", "lRearPlateColor",
                "lRearPlateType", "aulVehicleXY", "szVehicleBrand", "lVehicleBody", "lVehicleType", "lVehicleLength", "lVehicleColorDept", "cVehicleColor",
                "ucPlateScore", "ucRearPlateScore", "ucPicType", "lIdentifyStatus", "lIdentifyTime", "lDressColor", "lDealTag", "lVehicleSpeed",
                "lLimitedSpeed", "lMarkedSpeed", "lDriveStatus", "lRedLightTime", "szRedLightStartTime", "szRedLightEndTime", "szDriveStatus", "lTriggerType"});
    }
}