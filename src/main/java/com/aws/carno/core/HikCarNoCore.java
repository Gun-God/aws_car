package com.aws.carno.core;

import com.aws.carno.Interface.HCNetSDK;
import com.aws.carno.Utils.osSelect;
import com.aws.carno.domain.AwsCarNo;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.aws.carno.service.AwsCarNoService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/28 17:25
 * @description 海康摄像头核心类
 */
public class HikCarNoCore {
    public static HikCarNoCore hikCarNoCore;
    static HCNetSDK hCNetSDK = null;
    static int lUserID = -1;//用户句柄 实现对设备登录
    static int lAlarmHandle = -1;//报警布防句柄
    static int lAlarmHandle_V50 = -1; //v50报警布防句柄
    static int lListenHandle = -1;//报警监听句柄
    static HCNetSDK.FMSGCallBack fMSFCallBack = null;
    static HCNetSDK.FMSGCallBack_V31 fMSFCallBack_v31 = null;
    static String preNo;
    public BlockingQueue<HCNetSDK.NET_ITS_PLATE_RESULT> msgQueue1 = new LinkedBlockingQueue<>();
    public final String[] trans_array={"其他车型","小型车","大型车","行人触发","二轮车触发","三轮车触发"};

    String ip;
    short port;

    @Autowired
    AwsCarNoService noService;
    @Autowired
    AwsPreCheckDataMapper preCheckDataMapper;
    @PostConstruct
    public void init(){
        hikCarNoCore=this;
        hikCarNoCore.preCheckDataMapper=this.preCheckDataMapper;
        hikCarNoCore.noService=this.noService;
    }
    //这个代码用于手动抓拍
    public class CarFMSGCallBack implements HCNetSDK.FMSGCallBack {

        public final String[] trans_array={"其他车型","小型车","大型车","行人触发","二轮车触发","三轮车触发"};

        //车辆类型 0 表示其它车型，1 表示小型车，2 表示大型车 ,3表示行人触发 ,4表示二轮车触发 5表示三轮车触发(3.5Ver)
        //报警信息回调函数
        public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {

            AwsCarNo carNo=new AwsCarNo();
            AwsPreCheckData pre=new AwsPreCheckData();



            System.out.println("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
            String sTime;
            String MonitoringSiteID;
            //lCommand是传的报警类型
            if (lCommand == HCNetSDK.COMM_ITS_PLATE_RESULT) {//交通抓拍结果(新报警信息)
                HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();

                strItsPlateResult.write();
//              获取车辆信息指针
                Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                strItsPlateResult.read();
                try {
                    //车牌信息
                    String sLicense = new String(strItsPlateResult.struPlateInfo.sLicense, "GB2312");
                    //车辆类型
                    int VehicleType = (int)strItsPlateResult.struVehicleInfo.byVehicleType;

                    //监控点编号
                    MonitoringSiteID = new String(strItsPlateResult.byMonitoringSiteID);
                    //车牌颜色
                    int Vehicle_Plate_Color =(int)strItsPlateResult.struPlateInfo.byColor;
                    //车道信息
//                    int laneInfo = (int)strItsPlateResult.byDriveChan;

                    System.out.println("车牌号：" + sLicense + ":车辆类型：" + VehicleType + "车速：" + strItsPlateResult.struVehicleInfo.wSpeed+"车道信息："+strItsPlateResult.byDriveChan+"车辆牌照颜色："+Vehicle_Plate_Color);
                    //入库操作
//                    carno表
                    carNo.setCarNo(sLicense);
                    //因为0蓝1黄 数据库种1蓝2黄
                    carNo.setColor(Vehicle_Plate_Color+1);
                    carNo.setCreateTime(new Date());
                    carNo.setCode(ip+"_"+trans_array[VehicleType]);

//                    precheck表
                    pre.setCarNo(sLicense);
                    pre.setCreateTime(new Date());
                    pre.setSpeed(Double.valueOf((double) strItsPlateResult.struVehicleInfo.wSpeed));
//                    msgQueue1. add

//                    车型需要由数字转换为车辆信息
//                    carNo.setCode(ProcessCarMesUtil.convertVehicleTypeToString(VehicleType));
//                    carNo.setLane()


                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }



                /**
                 * 报警图片保存，车牌，车辆图片
                 */
                for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
                    if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = "F:/pic/" + newName + "_type[" + strItsPlateResult.struPicInfo[i].byType + "]_ItsPlate.jpg";
                            fout = new FileOutputStream(filename);
                            File file = new File(filename);
                            if (!file.getParentFile().exists())
                                file.mkdirs();
                            if (!file.exists())
                                file.createNewFile();
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
                            byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();

//                      数据库操作
                            if(i==1)//只保留车远景    也可以判断strItsPlateResult.struPicInfo[i].byType==1
                           {
                               carNo.setImg(filename);
                               pre.setImg(filename);
                           }


                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
//                数据库操作
                hikCarNoCore.noService.insertCarNo(carNo);
                AwsPreCheckData perCheckData=hikCarNoCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo,preNo));
                if (perCheckData==null) {
                    //如果没有这条信息则插入
                    hikCarNoCore.preCheckDataMapper.insert(pre);
                    pre.setPreNo(preNo);
                }
                else {
                    //否则就说明称重那边有数据了，仅更新数据
                    hikCarNoCore.preCheckDataMapper.update(pre,new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo,preNo));
                }

                //监听结束，关闭监听
                 stopListen(lListenHandle);

            }

            return;
        }
    }



    public String savePic(HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult) {
        String file_url="";
        for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
            if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                String newName = sf.format(new Date());
                FileOutputStream fout;
                try {
                    String filename = "E:/pic/" + newName + "_type[" + strItsPlateResult.struPicInfo[i].byType + "]_ItsPlate.jpg";
                    fout = new FileOutputStream(filename);
                    File file = new File(filename);
                    if (!file.getParentFile().exists())
                        file.mkdirs();
                    if (!file.exists())
                        file.createNewFile();
                    fout = new FileOutputStream(filename);
                    //将字节写入文件
                    long offset = 0;
                    ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
                    byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    fout.write(bytes);
                    fout.close();

//                      数据库操作
                    if(i==1)//只保留车远景    也可以判断strItsPlateResult.struPicInfo[i].byType==1
                    {
                        file_url=filename;
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return file_url;
    }


    public class CarFMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {


        //报警信息回调函数
        public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            System.out.println("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
            int laneInfo=-1;
            //lCommand是传的报警类型
            if (lCommand == HCNetSDK.COMM_ITS_PLATE_RESULT) {//交通抓拍结果(新报警信息)
                HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
                strItsPlateResult.write();
//              获取车辆信息指针
                Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                strItsPlateResult.read();

                msgQueue1.add(strItsPlateResult);

        }
            return true;
    }

    }

    public HikCarNoCore(String ip, short port,String user_name, String password) {
        this.ip = ip;
        this.port = port;
        //设置布防和报警回调函数
//        fMSFCallBack是监听的回调函数，fMSFCallBack_v31是布防的回调函数
        if (fMSFCallBack_v31== null) {
            fMSFCallBack_v31 = new CarFMSGCallBack_V31();
            Pointer pUser = null;
            if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_v31, pUser)) {
                System.out.println("设置回调函数V31失败!");
                return;
            } else {
                System.out.println("设置回调函数V31成功!");
            }
        }

// 10.19调试 login_V40("192.10.12.245", (short) 8000, "admin", "admin12345");
        login_V40(ip, port, user_name,password);
        //设防
        setAlarm();

//        在这里写个一直循环 处理数据
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    processing_Data();
                }
            });
            thread.start();
    }

    public void processing_Data() {

//        先从队列中加载数据
        try {

            System.out.println("--------------海康摄像头处理线程运行了--------------");
            while (true) {
                if (msgQueue1.size() > 0) {
                    AwsCarNo carNo = new AwsCarNo();
                    AwsPreCheckData pre = new AwsPreCheckData();
                    Date passTime = null;
                    String MonitoringSiteID;
                    int laneInfo = -1;
                    //获得数据
                    HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult;
                    strItsPlateResult = msgQueue1.take();

                    //车牌信息
                    String sLicense = new String(strItsPlateResult.struPlateInfo.sLicense, "GB2312");
                    //车辆类型
                    int VehicleType = (int) strItsPlateResult.struVehicleInfo.byVehicleType;
                    //监控点编号
                    MonitoringSiteID = new String(strItsPlateResult.byMonitoringSiteID);
                    //车牌颜色
                    int Vehicle_Plate_Color = (int) strItsPlateResult.struPlateInfo.byColor;
                    //车道信息
                    laneInfo = (int) strItsPlateResult.byDriveChan;
                    //拍照时间
                    //获取抓拍时间
                    HCNetSDK.NET_DVR_TIME_V30 snapTime = strItsPlateResult.struSnapFirstPicTime;
                    int year = snapTime.wYear; //
                    int month = snapTime.byMonth - 1; // 注意：月份是从0开始的
                    int day = snapTime.byDay;
                    int hour = snapTime.byHour;
                    int min = snapTime.byMinute;
                    int sec = snapTime.bySecond;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
                    passTime = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
                        //这里的passTime先不要插入 先从数据库中查询 之后再比较
                     System.out.println("摄像头数据：车牌号：" + sLicense + ":车辆类型：" + VehicleType + "车速：" + strItsPlateResult.struVehicleInfo.wSpeed + "车道信息：" + laneInfo + "车辆牌照颜色：" + Vehicle_Plate_Color + " 车道信息：" + laneInfo + " 抓拍时间：" + passTime + "监控点编号：" + MonitoringSiteID);
                        //入库操作
//                    carno表
                        carNo.setCarNo(sLicense);
                        //因为0蓝1黄 数据库种1蓝2黄
                        carNo.setColor(Vehicle_Plate_Color + 1);
                        carNo.setCreateTime(new Date());
                        carNo.setCode(ip + "_" + trans_array[VehicleType]);
                        carNo.setLane(laneInfo);
//                    precheck表
                        pre.setCarNo(sLicense);
                        //pre.setCreateTime(new Date());
                        pre.setSpeed(Double.valueOf((double) strItsPlateResult.struVehicleInfo.wSpeed));
                    /**
                     * 报警图片保存，车牌，车辆图片
                     */
                    String fiile_url = savePic(strItsPlateResult);
                    carNo.setImg(fiile_url);
                    pre.setImg(fiile_url);

                    //先获取离当前时间最近，且车道号一致的percheck数据，
                    int timeThreshold = 2; //时间有效阈值为2秒
                    AwsPreCheckData pCData = hikCarNoCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getLane, laneInfo).isNull(AwsPreCheckData::getCarNo).orderByDesc(AwsPreCheckData::getPassTime).last("limit 1"));

                    System.err.println("匹配信息:" + pCData);

                    //将percheck数据和目前拍摄数据的车道号进行匹配
                    if (pCData != null) {//当未查询到数据，则直接插入
                        pre.setLane(laneInfo);
                        pre.setPassTime(passTime);
                        hikCarNoCore.preCheckDataMapper.insert(pre);
                    } else {
                        //判断是否在时间阈值中,超出阈值则抛弃
                        // 创建日期格式
                        Date wpTime = pCData.getPassTime();
                        long interval = (passTime.getTime() - wpTime.getTime()) / 1000;
                        if (interval < timeThreshold && interval >= 0) {//则更新数据
                            hikCarNoCore.preCheckDataMapper.update(pre, new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo, pCData));
                        } else {//则插入数据
                            pre.setLane(laneInfo);
                            pre.setPassTime(passTime);
                            hikCarNoCore.preCheckDataMapper.insert(pre);
                        }
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
                    }
//                对carno表实现数据库操作
                    hikCarNoCore.noService.insertCarNo(carNo);

                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }


    /**
     * 设备登录V40 与V30功能一致
     *
     * @param ip   设备IP
     * @param port SDK端口，默认设备的8000端口
     * @param user 设备用户名
     * @param psw  设备密码
     */
    public static void login_V40(String ip, short port, String user, String psw) {
        //注册
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息

        String m_sDeviceIP = ip;//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        String m_sUsername = user;//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        String m_sPassword = psw;//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        m_strLoginInfo.wPort = port;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.byLoginMode = 0;  //ISAPI登录
        m_strLoginInfo.write();

        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID == -1) {
            System.out.println("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            System.out.println(ip + ":设备登录成功！");
            return;
        }
    }


    /**
     * 报警布防接口
     *
     * @param
     */
    public static void setAlarm() {
        if (lAlarmHandle < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 0;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 0;   //布防类型：0-客户端布防，1-实时布防
            m_strAlarmInfo.write();
            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
            System.out.println("lAlarmHandle: " + lAlarmHandle);
            if (lAlarmHandle == -1) {
                System.out.println("布防失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
                return;
            } else {
                System.out.println("布防成功");
            }
        } else {
            System.out.println("设备已经布防，请先撤防！");
        }
        return;

    }


    /**
     * 动态库加载
     *
     * @return
     */
    public static boolean createSDKInstance() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = "";
                try {
                    if (osSelect.isWindows())
                        //win系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "\\libs\\hik\\windows\\HCNetSDK.dll";
                    else if (osSelect.isLinux())
                        //Linux系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "libs/hik/linux/libhcnetsdk.so";
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


//    /**
//     * 开启监听
//     */
    public void startListen(String no) {
        preNo = no;
        if (fMSFCallBack == null) {
            fMSFCallBack = new CarFMSGCallBack();
        }
        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(ip, port, fMSFCallBack, null);
        if (lListenHandle == -1) {
            System.out.println("监听失败" + hCNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            System.out.println("监听成功");
        }
    }


    /**
     * 设备撤防，设备注销
     *
     * @param
     */
    public static void logout() {

        if (lAlarmHandle > -1) {
            if (hCNetSDK.NET_DVR_CloseAlarmChan(lAlarmHandle)) {
                System.out.println("撤防成功");
            }
        }
        if (lListenHandle > -1) {
            if (hCNetSDK.NET_DVR_StopListen_V30(lListenHandle)) {
                System.out.println("停止监听成功");
            }
        }
        if (lUserID > -1) {
            if (hCNetSDK.NET_DVR_Logout(lUserID)) {
                System.out.println("注销成功");
            }
        }
        hCNetSDK.NET_DVR_Cleanup();


        return;
    }


    public void stopListen(int lListenHandle) {
//        这里只停止监听
        if (hCNetSDK.NET_DVR_StopListen_V30(lListenHandle))
            System.out.println("退出监听");
    }

}
