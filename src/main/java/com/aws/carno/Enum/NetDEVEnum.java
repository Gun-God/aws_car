package com.aws.carno.Enum;

/**
 * SDK1.0 枚举值相关定义
 */
public class NetDEVEnum
{
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int NETDEV_E_SUCCEED = 0;                                  /* 成功  Succeeded*/
    
    /**@brief流ID */
    public static final int NETDEV_LIVE_STREAM_INDEX_MAIN  = 0;                    /* 主流  Main stream */
    public static final int NETDEV_LIVE_STREAM_INDEX_AUX   = 1;                    /* 辅流  Sub stream */
    public static final int NETDEV_LIVE_STREAM_INDEX_THIRD = 2;                    /* 第三流  Third stream */
    public static final int NETDEV_LIVE_STREAM_INDEX_FOUTH = 3;                    /* 第四流  Fouth stream */
    public static final int NETDEV_LIVE_STREAM_INDEX_INVALID = 4; 

    /*************************************** 媒体流相关 ********************************************/
    /**@brief 传输模式 */
    public static final int NETDEV_TRANSPROTOCAL_RTPUDP       =   0;      /* UDP */
    public static final int NETDEV_TRANSPROTOCAL_RTPTCP  =   1;           /* TCP */
    public static final int NETDEV_TRANSPROTOCAL_UNIVIEW1_TCP  =   2;     /* TCP＋Uniview1 报文模式 */
    public static final int NETDEV_TRANSPROTOCAL_TMS_IMAGE     =   9;     /* TMS照片模式 报文模式 */

    /**************************************** 照片结构 ***************************************/
    public static final int NETDEV_TRAFFIC_PIC_MAX_NUM = 8;           /**< 最大照片数 */
    public static final int NETDEV_UNIVIEW_MAX_TIME_LEN = 18;         /**< 照片结构时间信息最大长度 */
    public static final int NETDEV_PECCANCYTYPE_CODE_MAX_NUM = 16;
    public static final int NETDEV_CAR_VEHICLE_BRAND_LEN = 4;         /**< 车标编码最大长度 */
    public static final int NETDEV_CAR_PLATE_MAX_LEN = 32;            /**< 号牌号码最大长度 */
    public static final int NETDEV_DIRECTION_NAME_MAX_LEN = 64;       /**< 方向名称最大长度 */
    public static final int NETDEV_PLACE_NAME_MAX_LEN = 256;          /**< 照片结构地点名称最大长度 */
    public static final int NETDEV_TOLLGATE_NAME_MAX_LEN = 64;        /**< 卡口设备名称最大长度 */
    public static final int NETDEV_DEV_ID_MAX_LEN = 32;               /**< 设备编号最大长度 */
    
    public static final int IMOS_MW_TRAFFIC_PIC_MAX_NUM     =   8;          /**< 最大照片数 */
    public static final int IMOS_MW_UNIVIEW_MAX_TIME_LEN    =   18;         /**< 照片结构时间信息最大长度 */
    public static final int IMOS_MW_PLACE_NAME_MAX_LEN      =   256;        /**< 照片结构地点名称最大长度 */
    public static final int IMOS_MW_CAR_PLATE_MAX_LEN       =   32;         /**< 号牌号码最大长度 */
    public static final int IMOS_MW_DEV_ID_MAX_LEN          =   32;         /**< 设备编号最大长度 */
    public static final int IMOS_MW_TOLLGATE_NAME_MAX_LEN   =   64;         /**< 卡口设备名称最大长度 */
    public static final int IMOS_MW_DIRECTION_NAME_MAX_LEN  =   64;         /**< 方向名称最大长度 */
    public static final int IMOS_MW_CAR_VEHICLE_BRAND_LEN   =   4;          /**< 车标编码最大长度 */

    public static final int IMOS_MW_NAME_LEN = 64;               /**@brief 通用名称字符串长度 */
    public static final int IMOS_MW_RES_CODE_LEN = 48;           /**@brief 用户ID长度*/
    public static final int DEMO_LOG_SIZE = 2000;                /**@brief demo运行日志默认为2000*/
    public static final int MAX_PATH_SIZE = 260;                 /**@brief 最大路径长度*/
    
    /**@brief 图片格式 */
    public static final int IMOS_MW_PICTURE_FORMAT_BMP = 0;        /**< BMP 格式 */
    public static final int IMOS_MW_PICTURE_FORMAT_JPEG = 1;       /**< JPEG 格式，参数参见: IMOS_MW_CAPTURE_FORMAT_CFG_S */
    public static final int IMOS_MW_PICTURE_FORMAT_CUSTOM = 10;    /**< 随流格式, 不需要其他参数 */

    /**@brief 日志级别 */
    public static final int IMOS_SDK_LOG_CLOSE  =   0;                      /**< 关闭日志 */
    public static final int IMOS_SDK_LOG_DEBUG  =   1;                      /**< debug级别 */
    public static final int IMOS_SDK_LOG_INFO   =   2;                      /**< info级别 */
    public static final int IMOS_SDK_LOG_WARN   =   3;                      /**< warn级别 */
    public static final int IMOS_SDK_LOG_ERROR  =   4;                      /**< error级别 */
    public static final int IMOS_SDK_LOG_FATAL  =   5;                      /**< fatal级别 */
    
    /**************************************** 设备状态相关 ***************************************/
    public static final int IMOS_MW_STATUS_BASIC_INFO = 52;                 /**< 设备基本信息，对应参数类型: IMOS_MW_BASIC_DEVICE_INFO_S */
    public static final int IMOS_MW_STATUS_IVA_PARK_STATUS_REPORT = 160;    /**< 车位状态上报 对应参数:IMOS_MW_PARK_STATUS_S */
    public static final int IMOS_MW_STATUS_CFG_CHANGE = 172; /* 相机配置变更 */

    public static final int IMOS_MW_VERSION_LEN = 256;    /* 版本信息长度 */
    
    /*************************************** 设备维护相关 ********************************************/
    public static final int IMOS_MW_IPADDR_LEN = 64;    /* IP地址信息字符串长度,IPV4:"192.168.0.102",IPV6:"A1:0:8:100:200:0:0:321A",域名:"AS_server_hangzhou" */
    
    /*************************************** 网口相关 ********************************************/
    public static final int IMOS_MW_PPPOE_USERNAME_LEN = 32;               /*@brief PPPoE用户名长度  */
    
    /*************************************** 配置相关 ********************************************/
    /* 网口、串口、透明通道 */
    /* 新开辟数值范围2000 ~ 2999 */
    public static final int IMOS_MW_NETWORK_INTERFACE_CFG = 12;    /**< 获取/设置 网口配置，对应结构定义: IMOS_MW_NETWORK_INTERFACE_CFG_S */
    
    public static final int MAX_IMAGE_SIZE = (4 * 1024 * 1024);    /* 最大照片数据字节数 */
    
    /************************************ 电子警察相关定义 ***************************************/
    public static final int IMOS_MW_PECCANCYTYPE_CODE_MAX_NUM = 16;    /* 违章代码最大长度 */
    
    /**
     * @enum tagNETDEVPictureFormat
     * @brief 图片格式 枚举定义 Picture type Enumeration definition
     * @attention 无 None
     */
    public static final int NETDEV_PICTURE_BMP = 0;                  /* 图片格式为bmp格式  Picture format is bmp */
    public static final int NETDEV_PICTURE_JPG = 1;                  /* 图片格式为jpg格式  Picture format is jpg */
    
    /* 通用长度  Common length */
    public static final int NETDEV_LEN_4 = 4;         /**< 通用长度4  Common length */
    public static final int NETDEV_LEN_8 = 8;         /**< 通用长度8  Common length */
    public static final int NETDEV_LEN_16 = 16;       /**< 通用长度16  Common length */
    public static final int NETDEV_LEN_32 = 32;       /**< 通用长度32  Common length */
    public static final int NETDEV_LEN_64 = 64;       /**< 通用长度64  Common length */
    public static final int NETDEV_LEN_128 = 128;     /**< 通用长度128  Common length */
    public static final int NETDEV_LEN_132 = 132;     /**< 通用长度132  Common length */
    public static final int NETDEV_LEN_260 = 260;     /**< 通用长度260  Common length */
    public static final int NETDEV_LEN_1024 = 1024;   /**< 通用长度1024  Common length */
    
    /**
     * @enum tagNETDEVCaptureType
     * @brief 输出开关量类型枚举
     * @attention 卡口电警对应IO口F1-F4
     */
    public static final int NETDEV_SWITCH_STATUS_F1 = 0;               /*F1IO口*/
    public static final int NETDEV_SWITCH_STATUS_F2 = 1;               /*F2IO口*/
    public static final int NETDEV_SWITCH_STATUS_F3 = 2;               /*F3IO口*/
    public static final int NETDEV_SWITCH_STATUS_F4 = 3;               /*F4IO口*/
    
    public static final int NETDEV_OSD_TEXT_MAX_LEN = 64 + 4;
    
    /**
     * @enum tagNETDEVOSDContentType
     * @brief OSD内容类型 枚举定义 Enumeration of content type
     * @attention 无 None
     */
    public static final int NETDEV_OSD_CONTENT_TYPE_NOTUSE = 0;       /* 不使用 Not used*/
    public static final int NETDEV_OSD_CONTENT_TYPE_CUSTOM = 1;       /* 自定义 Custom*/
}
