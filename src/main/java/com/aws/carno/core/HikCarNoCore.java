package com.aws.carno.core;

import com.aws.carno.Interface.HCNetSDK;
import com.aws.carno.Utils.DateUtil;
import com.aws.carno.Utils.StringUtil;
import com.aws.carno.Utils.osSelect;
import com.aws.carno.domain.*;
import com.aws.carno.mapper.AwsCarNoMapper;
import com.aws.carno.mapper.AwsPreCheckDataHistoryMapper;

import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/28 17:25
 * @description 海康摄像头核心类
 */
@Component
public class HikCarNoCore implements Runnable{
    public static HikCarNoCore hikCarNoCore;
    static HCNetSDK hCNetSDK = null;
    //    static int lUserID = -1;//用户句柄 实现对设备登录
    static int lUserID = -1;//用户句柄 实现对设备登录

    //  static int lAlarmHandle = -1;//报警布防句柄
    static int lAlarmHandle_V50 = -1; //v50报警布防句柄
    static int lListenHandle = -1;//报警监听句柄
    static HCNetSDK.FMSGCallBack fMSFCallBack = null;
    static HCNetSDK.FMSGCallBack_V31 fMSFCallBack_v31 = null;
    static String preNo;
    //    public BlockingQueue<HCNetSDK.NET_ITS_PLATE_RESULT> msgQueue1 = new LinkedBlockingQueue<>();
    public BlockingQueue<Map<String,Object>> msgQueue1 = new LinkedBlockingQueue<>();

    public final String[] trans_array={"其他车型","小型车","大型车","行人触发","二轮车触发","三轮车触发"};

    String ip;
    short port;
    String user_name;
    String password;

    //    @Autowired
//    AwsCarNoService noService;
    @Autowired
    public AwsTempCarnoDataMapper tempCarnoDataMapper;

    @Autowired
    public AwsCarNoMapper  carNoMapper;
    //    @Autowired
//    public AwsTempCarnoDataMapper tempCarnoDataMapper;
    @PostConstruct
    public void init(){
        hikCarNoCore=this;
        hikCarNoCore.tempCarnoDataMapper=this.tempCarnoDataMapper;
//        hikCarNoCore.tempCarnoDataMapper=this.tempCarnoDataMapper;
        hikCarNoCore.carNoMapper=this.carNoMapper;
//        hikCarNoCore.noService=this.noService;
    }
    public HikCarNoCore(){

    }

    @Override
    public void run() {

        processing_Data();
    }

    //校时
    @Scheduled(cron ="0 0 * * * ?")
    public void setCameraTime(){
        if(hCNetSDK!=null) {
            //登录
            StartCore.HikScanMaps.forEach((k,v)->{
                AwsScan test=v;
                System.out.println(test.getCode());
                lUserID=login_V40(test.getVideoIp(),(short) test.getPort().intValue(), test.getUserName(),test.getPassword());


            if(lUserID==-1)
            {
                System.out.println("登录失败");
                return;
            }

            HCNetSDK.NET_DVR_TIME time=new HCNetSDK.NET_DVR_TIME();
            LocalDateTime now = LocalDateTime.now();

            // 提取年、月、日、时、分、秒
            int year = now.getYear(); // 获取年份并取后两位
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            time.dwYear=year;
            time.dwMonth=month;
            time.dwDay=day;
            time.dwMinute=minute;
            time.dwSecond=second;
            time.dwHour=hour;
            int dwsize=time.size();
            time.write();
            Pointer ptime=time.getPointer();

            boolean setOK= hCNetSDK.NET_DVR_SetDVRConfig(lUserID,HCNetSDK.NET_DVR_SET_TIMECFG,0xffffffff,ptime,time.size());
            if(setOK)
            {
                System.out.println("设置成功");
            }
            else{
                System.out.println("设置失败 错误码"+hCNetSDK.NET_DVR_GetLastError());
            }
            //无论成功还是失败都要退出登录
                if (hCNetSDK.NET_DVR_Logout(lUserID)) {
                    System.out.println("注销成功");
                }

            });

        }
    }



    //这个代码用于监听抓拍
    public class CarFMSGCallBack implements HCNetSDK.FMSGCallBack {
        public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
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
                Date d=new Date();
                String deviceIp="";
                String newIp="";
                try {
                    deviceIp = new String(pAlarmer.sDeviceIP, "UTF-8");
                    newIp= StringUtil.getIpInLong(deviceIp);
                    // System.err.println(newIp+"   "+newIp.length());
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                String random_end=StringUtil.randomNumeric(3);
                Map<String,Object> mc=new HashMap<>();
                mc.put("ip",newIp);
                mc.put("plateData",strItsPlateResult);
                mc.put("time",d);
                mc.put("randomEnd",random_end);
                msgQueue1.add(mc);
//                    msgQueue1.add(strItsPlateResult);


                String no = "fail";
//                try {
//                    no = new String(strItsPlateResult.struPlateInfo.sLicense, "GB2312");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                no=no.replaceAll("\\x00", "");
//                if(!no.contains("无"))
//                    no=no.substring(1);
                HCNetSDK.NET_DVR_TIME_V30 snapTime = strItsPlateResult.struSnapFirstPicTime;
                int year = snapTime.wYear; //
                int month = snapTime.byMonth; // 注意：月份是从0开始的
                int day = snapTime.byDay;
                int hour = snapTime.byHour;
                int min = snapTime.byMinute;
                int sec = snapTime.bySecond;
                no=year+""+month+""+day;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
                Date passTime = null;
                try {
                    passTime = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (strItsPlateResult.struPicInfo[0].dwDataLen > 0) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sf3 = new SimpleDateFormat("yyyyMM");
                    no=sf2.format(passTime);
                    String yue=sf3.format(passTime);
                    String newName = sf.format(passTime)+"_"+random_end;
                    FileOutputStream fout;
                    try {
//                        用passtime的年月日作为文件名
                        String filename = "F:"+File.separator+"pic"+File.separator+yue+File.separator+no+File.separator + newName +".jpg";

                        File file = new File(filename);
                        if (!file.getParentFile().getParentFile().exists())
                            file.getParentFile().getParentFile().mkdirs();
                        if (!file.getParentFile().exists())
                            file.getParentFile().mkdirs();
                        if (!file.exists())
                            file.createNewFile();
                        file.setWritable(true);
                        fout = new FileOutputStream(file);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strItsPlateResult.struPicInfo[0].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[0].dwDataLen);
                        byte[] bytes = new byte[strItsPlateResult.struPicInfo[0].dwDataLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                        System.err.println("写入图片完毕！");
//                      数据库操作
//                            if(i==1)//只保留车远景    也可以判断strItsPlateResult.struPicInfo[i].byType==1
//                            {
//                                file_url=filename;
//                            }
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }


            // return true;
        }
    }

//
//    //这个代码用于手动抓拍
//    public class CarFMSGCallBack implements HCNetSDK.FMSGCallBack {
//
//        public final String[] trans_array={"其他车型","小型车","大型车","行人触发","二轮车触发","三轮车触发"};
//
//        //车辆类型 0 表示其它车型，1 表示小型车，2 表示大型车 ,3表示行人触发 ,4表示二轮车触发 5表示三轮车触发(3.5Ver)
//        //报警信息回调函数
//        public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
//
//            AwsCarNo carNo = new AwsCarNo();
//            AwsPreCheckData pre = new AwsPreCheckData();
//            Date passTime = null;
//            String MonitoringSiteID;
//            int laneInfo = -1;
//
//            System.out.println("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
//            //lCommand是传的报警类型
//            if (lCommand == HCNetSDK.COMM_ITS_PLATE_RESULT) {//交通抓拍结果(新报警信息)
//                HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
//
//                strItsPlateResult.write();
////              获取车辆信息指针
//                Pointer pItsPlateInfo = strItsPlateResult.getPointer();
//                pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
//                strItsPlateResult.read();
//                try {
//                    //车牌信息
//                    String sLicense = new String(strItsPlateResult.struPlateInfo.sLicense, "GB2312");
//                    //车辆类型
//                    int VehicleType = (int) strItsPlateResult.struVehicleInfo.byVehicleType;
//                    //监控点编号
//                    MonitoringSiteID = new String(strItsPlateResult.byMonitoringSiteID);
//                    //车牌颜色
//                    int Vehicle_Plate_Color = (int) strItsPlateResult.struPlateInfo.byColor;
//                    //车道信息
//                    laneInfo = (int) strItsPlateResult.byDriveChan;
//                    //拍照时间
//                    //获取抓拍时间
//                    HCNetSDK.NET_DVR_TIME_V30 snapTime = strItsPlateResult.struSnapFirstPicTime;
//                    int year = snapTime.wYear; //
//                    int month = snapTime.byMonth - 1; // 注意：月份是从0开始的
//                    int day = snapTime.byDay;
//                    int hour = snapTime.byHour;
//                    int min = snapTime.byMinute;
//                    int sec = snapTime.bySecond;
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
//                    String time=year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
//                    passTime = dateFormat.parse(time);
//                    //这里的passTime先不要插入 先从数据库中查询 之后再比较
//                    System.out.println("摄像头数据：车牌号：" + sLicense + ":车辆类型：" + VehicleType + "车速：" + strItsPlateResult.struVehicleInfo.wSpeed + "车道信息：" + laneInfo + "车辆牌照颜色：" + Vehicle_Plate_Color + " 车道信息：" + laneInfo + " 抓拍时间：" + passTime + "监控点编号：" + MonitoringSiteID);
//                    //入库操作
////                    carno表
//                    carNo.setCarNo(sLicense);
//                    //因为0蓝1黄 数据库种1蓝2黄
//                    carNo.setColor(Vehicle_Plate_Color + 1);
//                    carNo.setCreateTime(passTime);
//                    carNo.setCode(ip + "_" + trans_array[VehicleType]);
//                    carNo.setLane(laneInfo);
////                    precheck表
//                    pre.setCarNo(sLicense);
//                    //pre.setCreateTime(new Date());
//                    pre.setSpeed(Double.valueOf((double) strItsPlateResult.struVehicleInfo.wSpeed));
//                    ;
////                    msgQueue1. add
//
////                    车型需要由数字转换为车辆信息
////                    carNo.setCode(ProcessCarMesUtil.convertVehicleTypeToString(VehicleType));
////                    carNo.setLane()
//
//
//                } catch (IOException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                /**
//                 * 报警图片保存，车牌，车辆图片
//                 */
//                String fiile_url = savePic(strItsPlateResult);
//                carNo.setImg(fiile_url);
//                pre.setImg(fiile_url);
//
//                //先获取离当前时间最近，且车道号一致的percheck数据，
//                int timeThreshold = 2; //时间有效阈值为2秒
//
//
//                //将percheck数据和目前拍摄数据的车道号进行匹配
//
////                对carno表实现数据库操作
////                hikCarNoCore.noService.insertCarNo(carNo);
//                //监听结束，关闭监听
//                 stopListen(lListenHandle);
//            }
//
//            return;
//        }
//    }
//


    public  String savePic(HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult) {
        String file_url="";
        for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
            if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                String newName = sf.format(new Date());
                FileOutputStream fout;
                try {
//                    String filename = "D:"+File.separator+"pic"+File.separator + newName + strItsPlateResult.struPicInfo[i].byType + ".jpg";
                    String filename = "D:"+File.separator+"pic"+File.separator + newName + strItsPlateResult.struPicInfo[i].byType + ".jpg";
                    File file = new File(filename);
                    if (!file.getParentFile().exists())
                        file.mkdirs();
                    if (!file.exists())
                        file.createNewFile();
                    file.setWritable(true);
                    fout = new FileOutputStream(file);
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

                //注意一下我这里注释掉了
                //msgQueue1.add(strItsPlateResult);

                String no = null;
                try {
                    no = new String(strItsPlateResult.struPlateInfo.sLicense, "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                no=no.replaceAll("\\x00", "");
                if(!no.contains("无"))
                    no=no.substring(1);
                HCNetSDK.NET_DVR_TIME_V30 snapTime = strItsPlateResult.struSnapFirstPicTime;
                int year = snapTime.wYear; //
                int month = snapTime.byMonth; // 注意：月份是从0开始的
                int day = snapTime.byDay;
                int hour = snapTime.byHour;
                int min = snapTime.byMinute;
                int sec = snapTime.bySecond;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
                Date passTime = null;
                try {
                    passTime = dateFormat.parse(year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (strItsPlateResult.struPicInfo[0].dwDataLen > 0) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(passTime);
                    FileOutputStream fout;
                    try {
                        String filename = "F:"+File.separator+"pic"+File.separator+no+File.separator + newName +".jpg";

                        File file = new File(filename);
                        if (!file.getParentFile().exists())
                            file.getParentFile().mkdirs();
                        if (!file.exists())
                            file.createNewFile();
                        file.setWritable(true);
                        fout = new FileOutputStream(file);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strItsPlateResult.struPicInfo[0].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[0].dwDataLen);
                        byte[] bytes = new byte[strItsPlateResult.struPicInfo[0].dwDataLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                        System.err.println("写入图片完毕！");

//                      数据库操作
//                            if(i==1)//只保留车远景    也可以判断strItsPlateResult.struPicInfo[i].byType==1
//                            {
//                                file_url=filename;
//                            }
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }


            return true;
        }

    }


    public HikCarNoCore(String ip, short port,String user_name, String password) {
        this.ip = ip;
        this.port = port;
        //设置监听和报警回调函数

        if (fMSFCallBack== null) {
            fMSFCallBack = new CarFMSGCallBack();

            if (fMSFCallBack==null) {
                System.out.println("设置监听回调函数失败!");
                return;
            } else {
                System.out.println("设置监听回调函数成功!");
            }
        }

        int lAlarmHandle=-1;
// 10.19调试 login_V40("192.10.12.245", (short) 8000, "admin", "admin12345");
        // int lUserId= login_V40(ip, port, user_name,password);
        startListen();

//        在这里写个一直循环 处理数据
//            Thread thread=new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    processing_Data();
//                }
//            });
//            thread.start();
    }

//    public HikCarNoCore(String ip, short port,String user_name, String password) {
//        this.ip = ip;
//        this.port = port;
//        //设置布防和报警回调函数
////        fMSFCallBack是监听的回调函数，fMSFCallBack_v31是布防的回调函数
//        if (fMSFCallBack_v31== null) {
//            fMSFCallBack_v31 = new CarFMSGCallBack_V31();
//            Pointer pUser = null;
//            if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_v31, pUser)) {
//                System.out.println("设置回调函数V31失败!");
//                return;
//            } else {
//                System.out.println("设置回调函数V31成功!");
//            }
//        }
//
//      if (fMSFCallBack== null) {
//            fMSFCallBack = new CarFMSGCallBack();
//
//            if (fMSFCallBack==null) {
//                System.out.println("设置监听回调函数失败!");
//                return;
//            } else {
//                System.out.println("设置监听回调函数成功!");
//            }
//        }
//
//       int lAlarmHandle=-1;
//// 10.19调试 login_V40("192.10.12.245", (short) 8000, "admin", "admin12345");
//       int lUserId= login_V40(ip, port, user_name,password);
//        //startListen();
//       // startListen2();
//       if (lUserId!=-1)
//        {
//            setAlarm(lAlarmHandle,lUserId);
//            //
//        }
//       else
//           System.out.println("登录失败");
//
//
//
//
//
//        //login_V40(ip, port, user_name,password);
////        if(ip=="192.168.3.3")
////        {
////            lUserID
////        }
//        //设防
////        setAlarm();
//
//
////        在这里写个一直循环 处理数据
////            Thread thread=new Thread(new Runnable() {
////                @Override
////                public void run() {
////
////                    processing_Data();
////                }
////            });
////            thread.start();
//    }

//    public void test_insert_temp()
//    {
//        AwsTempCarnoData tcData=new AwsTempCarnoData();
//        tcData.setCarNo("1111111");
//        hikCarNoCore.tempCarnoDataMapper.insert(tcData);
//    }


    public void processing_Data() {

//        先从队列中加载数据
        try {

            System.out.println("--------------海康摄像头处理线程运行了--------------");
            while (true) {
                if (msgQueue1.size() > 0) {
                    AwsCarNo carNo = new AwsCarNo();
                    AwsTempCarnoData pre = new AwsTempCarnoData();

                    Date passTime = null;
                    String MonitoringSiteID;
                    int laneInfo =2;
                    //获得数据
                    HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult;
                    Map<String,Object> mc=new HashMap<>();
                    mc=msgQueue1.take();
                    String nowIp=(String)mc.get("ip");
                    String random_end=(String)mc.get("randomEnd");
                    Object oj=mc.get("plateData");

                    strItsPlateResult=(HCNetSDK.NET_ITS_PLATE_RESULT)oj;
                    Date cam_cmpDate=(Date)mc.get("time");
                    //取出车道信息
                    AwsScan hikScan=StartCore.HikScanMaps.get(nowIp);
                    laneInfo=hikScan.getLane();
                    String orgCode=hikScan.getOrgCode();


                    //strItsPlateResult = msgQueue1.take();

                    //车牌信息
                    String sLicense = new String(strItsPlateResult.struPlateInfo.sLicense, "GB2312");
                    sLicense=sLicense.replaceAll("\\x00", "");
                    if(!sLicense.contains("无"))
                        sLicense=sLicense.substring(1);
                    //车辆类型
                    int VehicleType = (int) strItsPlateResult.struVehicleInfo.byVehicleType;
                    //监控点编号
                    MonitoringSiteID = new String(strItsPlateResult.byMonitoringSiteID);
                    //车牌颜色
                    int Vehicle_Plate_Color = (int) strItsPlateResult.struPlateInfo.byColor;
                    //车道信息
                    //  laneInfo = strItsPlateResult.byChanIndex;
                    //
                    //拍照时间
                    //获取抓拍时间
                    HCNetSDK.NET_DVR_TIME_V30 snapTime = strItsPlateResult.struSnapFirstPicTime;
                    int year = snapTime.wYear; //
                    int month = snapTime.byMonth ; // 注意：月份是从0开始的
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
                    carNo.setColor(Vehicle_Plate_Color);
                    carNo.setCreateTime(passTime);
                    carNo.setCode(nowIp + "_" + trans_array[VehicleType]);
                    carNo.setLane(laneInfo);
                    pre.setCarNo(sLicense);
                    pre.setPassTime(passTime);
                    pre.setLane(laneInfo);
                    pre.setColor(Vehicle_Plate_Color);
                    pre.setOrgCode(orgCode);
                    pre.setCreateTime(cam_cmpDate);
                    //pre.setCreateTime(new Date());
                    /**
                     * 报警图片保存，车牌，车辆图片
                     */

                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sf3 = new SimpleDateFormat("yyyyMM");

                    String newName = sf.format(passTime)+"_"+random_end;
                    String no=sf2.format(passTime);
                    String yue=sf3.format(passTime);
                    pre.setImg(yue+File.separator+no+File.separator+newName+".jpg");
                    carNo.setImg(yue+File.separator+no+File.separator+newName+".jpg");
                    hikCarNoCore.tempCarnoDataMapper.insert(pre);
                    hikCarNoCore.carNoMapper.insert(carNo);


                    //先获取离当前时间最近，且车道号一致的percheck数据，



//                对carno表实现数据库操作

//                    test_insert_temp();

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
    public static int login_V40(String ip, short port, String user, String psw) {
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

        int lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID == -1) {
            System.out.println("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
            return lUserID;
        } else {
            System.out.println(ip + ":设备登录成功！");
            return lUserID;
        }
    }


    /**
     * 报警布防接口
     *
     * @param
     */
    public static void setAlarm(int lAlarmHandle,int lUserId) {
        if (lAlarmHandle < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
//            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
//            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
//            m_strAlarmInfo.byLevel = 0;  //布防等级
//            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
//            m_strAlarmInfo.byDeployType = 0;   //布防类型：0-客户端布防，1-实时布防
//            m_strAlarmInfo.write();
//            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);

            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM_V50 m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM_V50();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 0;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 1;   //布防类型 0：客户端布防 1：实时布防
            m_strAlarmInfo.write();
            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V50(lUserId, m_strAlarmInfo, Pointer.NULL, 0);

            System.out.println("lAlarmHandle: " + lAlarmHandle);
            if (lAlarmHandle == -1) {
                System.out.println("布防失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
                return;
            } else {
                System.out.println("布防成功");
//                if (lAlarmHandle > -1) {
//                    if (hCNetSDK.NET_DVR_CloseAlarmChan(lAlarmHandle)) {
//                        System.out.println("撤防成功");
//                    }
//                }
            }
        } else {
            System.out.println("设备已经布防，请先撤防！");
            logout(lAlarmHandle,lUserId);
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
    public void startListen()
    {
        //preNo = no;
        if (fMSFCallBack == null) {
            fMSFCallBack = new CarFMSGCallBack();
        }
//        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30("192.168.10.248", port, fMSFCallBack, null);
        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(null, port, fMSFCallBack, null);
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
    public static void logout(int lAlarmHandle,int lUserId) {

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
        if (lUserId > -1) {
            if (hCNetSDK.NET_DVR_Logout(lUserId)) {
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
