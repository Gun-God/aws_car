package com.aws.carno.core;

import com.aws.carno.Interface.HCNetSDK;
import com.aws.carno.Utils.osSelect;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/28 17:25
 * @description 海康摄像头核心类
 */
public class HikCarNoCore {
    static HCNetSDK hCNetSDK = null;
    static int lUserID = -1;//用户句柄 实现对设备登录
    static int lAlarmHandle = -1;//报警布防句柄
    static int lAlarmHandle_V50 = -1; //v50报警布防句柄
    static int lListenHandle = -1;//报警监听句柄
    static HCNetSDK.FMSGCallBack fMSFCallBack = null;
    static String preNo;

    String ip;
    short port;


    public class CarFMSGCallBack implements HCNetSDK.FMSGCallBack {
        //报警信息回调函数
        public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
            System.out.println("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
            String sTime;
            String MonitoringSiteID;
            //lCommand是传的报警类型
            if (lCommand == HCNetSDK.COMM_ITS_PLATE_RESULT) {//交通抓拍结果(新报警信息)
                HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
                strItsPlateResult.write();
                Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                strItsPlateResult.read();
                try {
                    int carType = strItsPlateResult.struVehicleInfo.byVehicleType;
                    String sLicense = new String(strItsPlateResult.struPlateInfo.sLicense, "GBK");
                    byte VehicleType = strItsPlateResult.byVehicleType;  //0-其他车辆，1-小型车，2-大型车，3- 行人触发，4- 二轮车触发，5- 三轮车触发，6- 机动车触发
                    MonitoringSiteID = new String(strItsPlateResult.byMonitoringSiteID);
                    System.out.println("车牌号：" + sLicense + ":车辆类型：" + VehicleType + ":布防点编号：" + MonitoringSiteID);
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
                            String filename = "../pic/" + preNo + File.separator + newName + "_type[" + strItsPlateResult.struPicInfo[i].byType + "]_ItsPlate.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
                            byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                //监听结束，关闭监听
                stopListen(lListenHandle);
                //                case HCNetSDK.COMM_UPLOAD_PLATE_RESULT://COMM_UPLOAD_PLATE_RESULT:
//                    HCNetSDK.NET_DVR_PLATE_RESULT strPlateResult = new HCNetSDK.NET_DVR_PLATE_RESULT();
//                    strPlateResult.write();
//                    Pointer pPlateInfo = strPlateResult.getPointer();
//
//                    //pAlarmInfo.getByteArray(0, strPlateResult.size())
//                    pPlateInfo.write(0, pAlarmInfo.getByteArray(0, strPlateResult.size()), 0, strPlateResult.size());
//                    strPlateResult.read();
//                    try {
//                        String srt3 = new String(strPlateResult.struPlateInfo.sLicense, "GBK");
//                        // sAlarmType = sAlarmType + "：交通抓拍上传，车牌："+ srt3;
//                    } catch (UnsupportedEncodingException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
//                    //     newRow[0] = dateFormat.format(new Date());
//                    //报警类型
//                    //   newRow[1] = sAlarmType;
//                    //报警设备IP地址
//                    //  sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
//                    // newRow[2] = sIP[0];
////                    alarmTableModel.insertRow(0, newRow);
////                    logger.info( strPlateResult.byResultType+"<-识别类型 ->"+
////                            strPlateResult.dwCarPicLen+"原图<-图片长度-><-近景图->"+strPlateResult.dwPicLen  );
//
//
//                    break;
            }
        }
    }

    public HikCarNoCore(String ip, short port) {
        this.ip = ip;
        this.port = port;
        //设置报警回调函数
        if (fMSFCallBack == null) {
            fMSFCallBack = new CarFMSGCallBack();
        }
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
                        strDllPath = System.getProperty("user.dir") + "\\libs\\hiv\\lib\\HCNetSDK.dll";
                    else if (osSelect.isLinux())
                        //Linux系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "libs/hiv//lib/libhcnetsdk.so";
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 开启监听
     */
    public void startListen(String no) {
        preNo = no;
        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(ip, port, fMSFCallBack, null);
        if (lListenHandle == -1) {
            System.out.println("监听失败" + hCNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            System.out.println("监听成功");
        }
    }

    public void stopListen(int lListenHandle) {
        if (hCNetSDK.NET_DVR_StopListen_V30(lListenHandle))
            System.out.println("推出监听");
    }

}
