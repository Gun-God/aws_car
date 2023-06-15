package com.aws.carno.Interface;

import com.aws.carno.Struct.*;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * SDK1.0提供接口
 */
public interface ImosSdkInterface extends StdCallLibrary
{
    String path = System.getProperty("user.dir");
    boolean is64Bit = Platform.is64Bit();
    ImosSdkInterface instance =
            (ImosSdkInterface) Native.loadLibrary(is64Bit ? (path + "\\windll\\X64\\dll\\NetDEVSDK.dll") :
                    (path + "\\windll\\X86\\dll\\NetDEVSDK.dll"), ImosSdkInterface.class);


    /**
     * 获取SDK的版本信息 Get SDK version information
     *
     * @return SDK版本信息 SDK version information
     * @note - 2个高字节表示主版本,2个低字节表示次版本.如0x00030000：表示版本为3.0. - The two high bytes
     * indicate the major version, and the two low bytes indicate the minor
     * version. For example, 0x00030000 means version 3.0.
     */
    int NETDEV_GetSDKVersion();
    
    /**
     * 设置日志路径业务 Set log path
     *
     * @param szLogPath 日志路径(不包含文件名) Log path (file name not included)
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note
     */
    int NETDEV_SetLogPath(String szLogPath);

    /**
     * SDK 初始化 SDK initialization
     *
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note 线程不安全 Thread not safe
     */
    int NETDEV_Init();

    /**
     * SDK 初始化 SDK initialization
     *
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note 线程不安全 Thread not safe
     */
    int NETDEV_Cleanup();

    /**
     * 用户登录 User login
     *
     * @param szDevIP 设备IP Device IP
     * @param wDevPort 设备服务器端口 Device server port
     * @param szUserName 用户名 Username
     * @param szPassword 密码 Password
     * @param pstDevInfo 设备信息结构体指针 Pointer to device information structure
     * @return 返回的用户登录句柄,返回 0 表示失败,其他值表示返回的用户登录句柄值. Returned user login ID. 0
     * indicates failure, and other values indicate the user ID.
     * @note
     */
    Pointer NETDEV_Login(String szDevIP,
                           short wDevPort,
                           String szUserName ,
                           String szPassword ,
                           NETDEV_DEVICE_INFO_S.ByReference pstDevInfo
                           );

    /**
     * 用户注销 User logout
     *
     * @param lpUserID 用户登录句柄 User login ID
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note
     */
    int NETDEV_Logout(Pointer lpUserID);
    
    interface NETDEV_ExceptionCallBack_PF extends StdCallCallback {
        /**
         * 接收异常.重连等消息的回调函数 Callback function to receive exception and
         * reconnection messages
         *
         * @param lpUserID 用户登录句柄 User login ID
         * @param dwType 异常或重连等消息的类型:NETDEV_EXCEPTION_TYPE_E Type of
         * exception or reconnection message: NETDEV_EXCEPTION_TYPE_E
         * @param lpExpHandle 出现异常的相应类型的句柄 Exception type handle
         * @param lpUserData 用户数据 User data
         * @note
         */
        void callback(Pointer lpUserID, int dwType, Pointer lpExpHandle, Pointer lpUserData);
    }
    
    /**
     * 注册sdk接收异常.重连等消息的回调函数 Callback function to register SDK, receive exception
     * and reconnection messages, etc.
     *
     * @param cbExceptionCallBack 接收异常消息的回调函数,回调当前异常的相关信息 Callback function
     * to receive exception messages, used to call back information about
     * current exceptions
     * @param lpUserData 用户数据 User data
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note
     */
    int NETDEV_SetExceptionCallBack(NETDEV_ExceptionCallBack_PF cbExceptionCallBack, Pointer lpUserData);

    interface NETDEV_StatusReportCallBack_PF extends StdCallCallback {
        /**
         * SDK状态上报回调函数 \n
         *
         * @param lpUserID 用户登录句柄 User login ID
         * @param ulReportType 消息或者异常消息类型，参见 NETDEV_E_PARK_STATUS_REPORT 等
         * @param pParam 存放消息或异常消息数据的缓冲区指针
         * @note - 1、用户需要确保该回调函数尽快返回 - 2、不能在该回调函数中直接调用播放器的任何接口函数。 -
         * 3、参数pParam所指的缓冲区中存放的数据的类型视消息或者异常消息类型而定，用户需要根据消息或者异常消息类型对其做类型转换
         */
        void callback(Pointer lpUserID, int ulReportType, Pointer pParam);
    }
    
    /**
     * 设置状态回调\n
     *
     * @param cbStatusReportCallBack 状态回调函数
     * @return TRUE表示成功,其他表示失败
     * @note
     */
    int NETDEV_SetStatusCallBack(NETDEV_StatusReportCallBack_PF cbStatusReportCallBack);

    interface NETDEV_SOURCE_DATA_CALLBACK_PF extends StdCallCallback {
        /**
         * 拼帧前媒体流数据回调函数的指针类型 Type of pointer to media stream data callback
         * function before being framed
         *
         * @param lpPlayHandle 当前的实况播放句柄 Current live playing handle
         * @param ucBuffer 存放拼帧前媒体流数据缓冲区指针 Pointer to buffer that stores stream
         * data that is not framed
         * @param dwBufSize 缓冲区大小 Buffer size
         * @param dwMediaDataType 媒体数据类型,参见媒体类型枚举定义#NETDEV_MEDIA_DATA_FORMAT_E
         * Media data type, see definitions of enumeration
         * #NETDEV_MEDIA_DATA_TYPE_E
         * @param lpUserParam
         * 用户设置参数,即用户在调用#NDPlayer_SetSourceMediaDataCB函数时指定的用户参数 User-set
         * parameters, specified by users when they call the
         * #NDPlayer_SetSourceMediaDataCB function
         * @note 用户应及时处理输出的媒体流数据,确保函数尽快返回,否则会影响播放器内的媒体流处理. Users should handle
         * output stream data in a timely manner so that functions can be
         * returned quickly. Otherwise, stream processing in the player will be
         * affected.
         */
        void callback(Pointer lpPlayHandle, ByteByReference ucBuffer, int dwBufSize, int dwMediaDataType, Pointer lpUserParam);
    }
    
    /**
     * 启动实时预览 Start live preview
     *
     * @param lpUserID 用户登录句柄 User login ID
     * @param pstPreviewInfo
     * 预览参数,参考枚举：NETDEV_PROTOCAL_E,NETDEV_LIVE_STREAM_INDEX_E. Preview
     * parameter, see enumeration: NETDEV_PROTOCAL_E,
     * NETDEV_LIVE_STREAM_INDEX_E.
     * @param cbPlayDataCallBack 码流数据回调函数指针 Pointer to callback function of
     * stream data
     * @param lpUserData 用户数据 User data
     * @return 返回的用户登录句柄,返回 0 表示失败,其他值表示返回的用户登录句柄值. Returned user login ID. 0
     * indicates failure, and other values indicate the user ID.
     * @note
     */
    Pointer NETDEV_RealPlay(Pointer lpUserID,
                              NETDEV_PREVIEWINFO_S.ByReference pstPreviewInfo,
                              NETDEV_SOURCE_DATA_CALLBACK_PF cbPlayDataCallBack,
                              Pointer lpUserData
                              );

    /**
     * 停止实时预览 Stop live preview
     *
     * @param lpPlayHandle 预览句柄 Preview handle
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note 对应关闭NETDEV_RealPlay开启的实况 Stop the live view started by
     * NETDEV_RealPlay
     */
    int NETDEV_StopRealPlay(Pointer lpPlayHandle);
    
    /**
     * 停止回放业务 Stop playback service
     *
     * @param lpPlayHandle 回放句柄 Playback handle
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note
     */
    int NETDEV_StopPlayBack(Pointer lpPlayHandle);
    
    /**
     * 实况抓拍 Live view snapshot
     *
     * @param lpPlayHandle 预览\回放句柄 Preview\playback handle
     * @param szFileName 保存图像的文件路径（包括文件名） File path to save images (including
     * file name)
     * @param dwCaptureMode 保存图像格式,参见#NETDEV_PICTURE_FORMAT_E Image saving
     * format, see #NETDEV_PICTURE_FORMAT_E
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note 文件名中可以不携带抓拍格式的后缀名 File format suffix is not required in the file
     * name
     */
    int NETDEV_CapturePicture(Pointer lpPlayHandle, String szFileName, int dwCaptureMode);
    
    interface NETDEV_PIC_UPLOAD_PF extends StdCallCallback {
        void callback(NETDEV_PIC_DATA_S.ByReference pstPicData, Pointer lpUserParam);
    }
    
    /**
     * 启动照片流\n
     *
     * @param lpUserID 用户登录句柄
     * @param hPlayWnd 播放窗口句柄,如果为0不播放
     * @param bReTran 是否断网重传:TRUE表示断网重传,FALSE表示断网不重传
     * @param pcReTranIP 重传码流接收端IP地址;不重传填空,""
     * @param pfnPicDataCBFun 照片上传回调
     * @param lpUserData 用户数据
     * @return TRUE表示成功，其他表示失败
     * @note
     */
    Pointer NETDEV_StartPicStream(Pointer lpUserID, Pointer hPlayWnd, int bReTran, 
                                     String pcReTranIP, NETDEV_PIC_UPLOAD_PF pfnPicDataCBFun,
                                     Pointer lpUserData);
    
    /**
     * 停止照片流\n
     *
     * @param lpPlayHandle 照片流句柄
     * @return TRUE表示成功,其他表示失败
     * @note 关闭NETDEV_StartPicStream对应开启的照片流
     */
    int NETDEV_StopPicStream(Pointer lpPlayHandle);
    
    /**
     * 手动前端抓拍(异步)\n
     *
     * @param lpUserID 用户登录句柄
     * @return TRUE表示成功,其他表示失败
     * @note
     */
    int NETDEV_Trigger(Pointer lpUserID);
    
    /**
     * 手动前端抓拍(同步)\n
     *
     * @param lpUserID 用户登录句柄
     * @param pstPicData 指向获取的图片信息的指针
     * @return TRUE表示成功,其他表示失败
     * @note
     */
    int NETDEV_TriggerSync(Pointer lpUserID, PointerByReference pstPicData);
    
    /**
     * 获取设备的配置信息 Get configuration information of device
     *
     * @param lpUserID 用户登录句柄 User login ID
     * @param dwChannelID 通道号 Channel ID
     * @param dwCommand 设备配置命令,参见#NETDEV_CONFIG_COMMAND_E Device
     * configuration commands, see #NETDEV_CONFIG_COMMAND_E
     * @param lpOutBuffer 接收数据的缓冲指针 Pointer to buffer that receives data
     * @param dwOutBufferSize 接收数据的缓冲长度(以字节为单位),不能为0 Length (in byte) of
     * buffer that receives data, cannot be 0.
     * @param pdwBytesReturned 实际收到的数据长度指针,不能为NULL Pointer to length of
     * received data, cannot be NULL.
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note - 1.巡航路径ID不可修改. Route ID cannot be modified. - 2.新增巡航路径时,默认按顺序新增.
     * New routes are added one after another. -
     * 3.删除.开始.停止巡航路径时,pstCruiseInfo中只需要填写巡航路径ID即可. When deleting, starting or
     * stoping a patrol route, enter route ID in pstCruiseInfo.
     * 转换为Java代码后，此接口的lpOutBuffer参数需要分别转换为各个结构体的ByReference类型
     */
    int NETDEV_GetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, Object lpOutBuffer, int dwOutBufferSize, IntByReference pdwBytesReturned);
    
    int NETDEV_GetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, NETDEV_DEVICE_BASICINFO_S.ByReference lpOutBuffer, int dwOutBufferSize, IntByReference pdwBytesReturned);
    
    int NETDEV_GetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, NETDEV_NETWORKCFG_S.ByReference lpOutBuffer, int dwOutBufferSize, IntByReference pdwBytesReturned);
    
    int NETDEV_GetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, NETDEV_OSD_CONTENT_STYLE_S.ByReference lpOutBuffer, int dwOutBufferSize, IntByReference pdwBytesReturned);
    
    int NETDEV_GetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, NETDEV_OSD_CONTENT_S.ByReference lpOutBuffer, int dwOutBufferSize, IntByReference pdwBytesReturned);
    
    /**
     * 设置设备的配置信息 Modify device configuration information
     *
     * @param lpUserID 用户登录句柄 User login ID
     * @param dwChannelID 通道号 Channel ID
     * @param dwCommand 设备配置命令,参见#NETDEV_CONFIG_COMMAND_E Device
     * configuration commands, see #NETDEV_CONFIG_COMMAND_E
     * @param lpInBuffer 输入数据的缓冲指针 Pointer to buffer of input data
     * @param dwInBufferSize 输入数据的缓冲长度(以字节为单位) Length of input data buffer
     * (byte)
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note
     */
    int NETDEV_SetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, Object lpInBuffer, int dwInBufferSize);
    
    int NETDEV_SetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, NETDEV_OSD_CONTENT_S.ByReference lpInBuffer, int dwInBufferSize);
    
    int NETDEV_SetDevConfig(Pointer lpUserID, int dwChannelID, int dwCommand, NETDEV_OSD_CONTENT_STYLE_S.ByReference lpInBuffer, int dwInBufferSize);
    
    /**************远程参数配置  End remote parameter configuration ******************************/
    /**
     * 重启设备\n Restart device\n
     *
     * @param lpUserID 用户登录句柄 User login ID
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     */
    int NETDEV_Reboot(Pointer lpUserID);
    
    interface NETDEV_DISCOVERY_CALLBACK_PF extends StdCallCallback {
        void callback(NETDEV_DISCOVERY_DEVINFO_S.ByReference pstDevInfo, Pointer lpUserData);
    }
    
    /**
     * 注册设备发现回调函数 Registered device discovery callback function
     *
     * @param cbDiscoveryCallBack 回调函数 Callback function
     * @param lpUserData 用户数据 User data
     * @return TRUE表示成功,其他表示失败 TRUE means success, and any other value means
     * failure.
     * @note
     */
    int NETDEV_SetDiscoveryCallBack(NETDEV_DISCOVERY_CALLBACK_PF cbDiscoveryCallBack, Pointer lpUserData);
    
    /**
     * 设备发现 先注册设备发现相关的回调,再调用此接口发现设备,发现的设备信息在回调中反映 This interface is used for
     * device discovery. Please first register callback functions related to
     * device discovery and use this interface for device discovery. Discovered
     * device info will be included in the callback function.
     *
     * @param pszBeginIP 起始IP地址
     * @param pszEndIP 结束IP地址
     * @return TRUE表示成功,其他表示失败
     * @note 若pszBeginIP和pszEndIP都是"0.0.0.0",则搜索本网段设备
     */
    int NETDEV_Discovery(String pszBeginIP, String pszEndIP);
    
    /**
     * 卡口电警输出开关量\n
     *
     * @param lpUserID 用户登录句柄
     * @param stuSwitchStatusType
     * @return TRUE表示成功,其他表示失败
     * @note
     */
    int NETDEV_setSwitchStatusControlCfg(Pointer lpUserID, int stuSwitchStatusType);
    
    /**
     * 下载出入白名单信息 \n
     *
     * @param lpUserID 用户登录句柄
     * @param pcFile 文件保存路径
     * @return INT32 NETVMS_E_SUCCEED 表示成功，其他见相关错误码
     * @note 无
     */
    int NETDEV_ExportBlackWhiteListFile(Pointer lpUserID, String pcFile);

    int NETDEV_ExportConfig(Pointer lpUserID, String pcFilePath);

}
