package com.aws.carno.Enum;

/**
 * Created by l05826 on 2019/2/19.
 */
public class ResultEnum {
    public static final int ERR_COMMON_SUCCEED = 0;                // 执行成功
    public static final int ERR_SDK_USERNONEXIST = 259;            // 用户不存在
    public static final int ERR_SDK_USERFULL = 458;                // 用户已满
    public static final int ERR_SDK_USER_PASSWORD_CHANGE = 262;    // 用户密码修改
    public static final int ERR_SDK_USER_PASSWD_INVALID = 460;     // 用户密码错误
    public static final int ERR_SDK_USER_NOT_AUTHORIZED = 457;     // 用户未授权
    public static final int  ERR_SDK_REINIT = 463;                 // SDK已初始化
    public static final int  ERR_SDK_NOTINIT = 462;                // SDK未初始化
    public static final int  ERR_SDK_LOG_CLOSE = 257;              //SDK日志关闭
    public static final int  ERR_SDK_COMMON_INVALID_PARAM = 358;   //输入参数非法

}
