package com.aws.carno.core;

import com.aws.carno.Enum.NetDEVEnum;
import com.aws.carno.Interface.ImosSdkInterface;
import com.aws.carno.Struct.NETDEV_DEVICE_INFO_S;
import com.aws.carno.Struct.NETDEV_PIC_DATA_S;
import com.aws.carno.Utils.NetDEVSdk;
import com.aws.carno.domain.AwsCarNo;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.domain.AwsTempCarnoData;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.aws.carno.service.AwsCarNoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author :hyw
 * @version 1.0
 * @description 宇视摄像头核心类
 * @date : 2023/06/12 12:09
 */
@Slf4j
@Component
public class UnvCarNoCore {
    public static UnvCarNoCore unvCarNoCore;
    private static final Logger LOGGER = LogManager.getLogger(UnvCarNoCore.class.getName());
    private static ImosSdkInterface ITF = null;
    private static final String CURRENTDIRECTORY = System.getProperty("user.dir");
    private final ImosSdkInterface.NETDEV_PIC_UPLOAD_PF multiPicDataCallBackFun;
    //    private int m_lChannelID = 1;
    private String tfLocalIP="192.168.10.248";
    private Pointer m_lpDevHandle;
    private Pointer m_lpPicHandle;
    //过车唯一标识，流水号
    private String preNo;
    private String ip;


    @Autowired
    AwsCarNoService noService;
    @Autowired
    public AwsTempCarnoDataMapper tempCarnoDataMapper;
    @Autowired
    AwsPreCheckDataMapper preCheckDataMapper;
    @PostConstruct
    public void init(){
        unvCarNoCore=this;
        unvCarNoCore.preCheckDataMapper=this.preCheckDataMapper;
        unvCarNoCore.tempCarnoDataMapper=this.tempCarnoDataMapper;
        unvCarNoCore.noService=this.noService;
    }




    //TODO 获取到抓拍数据（包括车辆颜色，车牌号）后，对数据进行进一步的处理
    class multiPicDataCall implements ImosSdkInterface.NETDEV_PIC_UPLOAD_PF {

        @Override
        public void callback(NETDEV_PIC_DATA_S.ByReference pstPicData, Pointer lpUserParam) {
            AwsCarNo carNo = new AwsCarNo();
            AwsTempCarnoData pre = new AwsTempCarnoData();
            int i;
            Pointer p;
//            File file;
            int colorIndex;
            byte[] imageData;
            String szTmpFile;
            String strCarPlate;
            String strPassTime;
            FileOutputStream fileOutputStream;
            ByteByReference[] picData = pstPicData.apcData;
            String[] rowValues = {"", "", "", "", "", ""};
            String[] arrPlateColor = {"白", "黄", "蓝", "黑", "其他", "绿", "红", "黄绿", "渐变绿"};
            String[] carType = {"未知", "小型车", "中型车", "大型车", "其他"};
            FileOutputStream fout;
            String url="";


            // strPassTime = new String(pstPicData.szPassTime).trim();
            for (i = 0; i < pstPicData.ulPicNumber; i++) {
                p = picData[i].getPointer();
                imageData = p.getByteArray(0, pstPicData.aulDataLen[i]);
                strCarPlate = new String(pstPicData.szCarPlate).trim();
                String test=new String(pstPicData.szPassTime);
                test.substring(0,10);
                Date d=new Date();
                String times=String.valueOf(d.getTime());
                String newName=String.valueOf(i);
                newName=times+"_"+i;
                String szTmp = "F:"+File.separator+"pic"+File.separator+ strCarPlate+File.separator + newName +".jpg";
                url=strCarPlate+File.separator + newName +".jpg";
                try {
                    File file = new File(szTmp);
                    if (!file.getParentFile().exists())
                        file.getParentFile().mkdirs();
                    if (!file.exists())
                        file.createNewFile();
                    file.setWritable(true);
                    fout = new FileOutputStream(file);



                    fout.write(imageData);
                    fout.close();
                    System.err.println("写入图片完毕！");


                }catch(IOException ex){

                }
//                if (strCarPlate.length() > 1) {
//                    szTmpFile = String.format("%s_%s_%d.jpg", szTmp + preNo, strCarPlate, i);
//                } else {
//                    szTmpFile = String.format("%s_%d.jpg", szTmp + preNo, i);
//                }

//                try {
//                    file = new File(szTmpFile);
//                    if (!file.exists()) {
//                        file.createNewFile();
//                    }
//                    fileOutputStream = new FileOutputStream(file);
//                    try {
//                        fileOutputStream.write(imageData);
//                    } finally {
//                        fileOutputStream.close();
//                    }
//                } catch (IOException ex) {
//                    LOGGER.error("Error" + ex.getMessage(), ex);
//                }
            }
            if(1==1)
                return ;


            if ((pstPicData.lPlateColor >= 0) && (pstPicData.lPlateColor < 9)) {
                colorIndex = pstPicData.lPlateColor;
            } else {
                colorIndex = 4;
            }

            pre.setImg(url);
            carNo.setImg(url);
            if(1==1)
                return ;
            int code = pstPicData.lVehicleType;
            int speed = pstPicData.lVehicleSpeed;
            rowValues[0] = Integer.toString(pstPicData.ulRecordID);
            carNo.setCode(ip+"_"+ carType[code]);
            rowValues[1] = new String(pstPicData.szPassTime);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-M-d H:m:s");
            Date passTime = null;


            try {
                passTime=dateFormat.parse(rowValues[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            carNo.setCreateTime(passTime);
            pre.setPassTime(passTime);
            rowValues[2] = Integer.toString(pstPicData.lLaneID);
//            carNo.setLane(pstPicData.lLaneID);
            carNo.setLane(1);
            pre.setLane(1);
            //
            int colorCode=1;
            if(colorIndex==1)
            {
                colorCode=2;
            }
            else if(colorCode>=4){
                colorCode=3;
            }
            rowValues[3] = arrPlateColor[colorIndex];
            carNo.setColor(colorCode);
            pre.setColor(colorCode);
            try {
                rowValues[4] = new String(pstPicData.szCarPlate, "GB2312").trim();
                carNo.setCarNo(rowValues[4]);
                pre.setCarNo(rowValues[4]);
            } catch (UnsupportedEncodingException ex) {
                LOGGER.error("Error" + ex.getMessage(), ex);
            }
            rowValues[5] = "否";
            unvCarNoCore.tempCarnoDataMapper.insert(pre);
            unvCarNoCore.noService.insertCarNo(carNo);

            // vehicleInfoModel.addRow(rowValues);
        }
    }


    public UnvCarNoCore() {
        //todo 只是定义好了multiPicDataCallBackFun，并不是现在就执行它
        //自动抓拍回调函数
        multiPicDataCallBackFun = new multiPicDataCall();
    }

    private void btnPicPlayActionPerformed() {//GEN-FIRST:event_btnPicPlayActionPerformed
        if (Pointer.NULL == NetDEVSdk.m_lpDevHandle) {
//            JOptionPane.showMessageDialog(this, "当前用户未登录");
            System.err.println("未登录");
            return;
        }

        int iRet;
        int ifReTran;
        String errMessage;
        String localIP = "192.168.10.248";
        String reTranIP;

        if (Pointer.NULL != NetDEVSdk.m_lpPicHandle) {
            iRet = ITF.NETDEV_StopPicStream(NetDEVSdk.m_lpPicHandle);
            if (NetDEVEnum.TRUE == iRet) {
                NetDEVSdk.m_lpPicHandle = Pointer.NULL;
            }
        }

//        iRet = JOptionPane.showConfirmDialog(this, "是否进行断网续传？", "", JOptionPane.YES_NO_OPTION);
//        if (JOptionPane.YES_OPTION == iRet) {
//            ifReTran = NetDEVEnum.TRUE;
//        } else {
//            ifReTran = NetDEVEnum.FALSE;
//        }
        ifReTran = NetDEVEnum.TRUE;
        if ((localIP.isEmpty()) && (NetDEVEnum.TRUE == ifReTran)) {
//            JOptionPane.showMessageDialog(this, "本地IP为空.");
            System.err.println("本地ip为空");
            return;
        }

        if ((localIP.isEmpty()) || (NetDEVEnum.FALSE == ifReTran)) {
            reTranIP = "";
        } else {
            reTranIP = localIP;
        }

        NetDEVSdk.m_lpPicHandle = ITF.NETDEV_StartPicStream(NetDEVSdk.m_lpDevHandle, NetDEVSdk.m_lpPicWndHandle, ifReTran, reTranIP, multiPicDataCallBackFun, Pointer.NULL);
        if (Pointer.NULL == NetDEVSdk.m_lpPicHandle) {
            errMessage = "照片流起流失败";
            System.err.println(errMessage);
        } else {
            NetDEVSdk.bPicPlay = true;
            errMessage = "照片流起流成功";
            System.err.println(errMessage);
        }

//        JOptionPane.showMessageDialog(this, errMessage);
    }//GEN-LAST:event_btnPicPlayActionPerformed

//
//    //实况抓拍
//    public void btnPicPlayActionPerformed(String no) {//GEN-FIRST:event_btnPicPlayActionPerformed
//        preNo=no;
//        if (Pointer.NULL == m_lpDevHandle) {
//            log.info("未登录");
//            return;
//        }
//        int iRet;
//        int ifReTran;
//        String localIP = tfLocalIP;
//        String reTranIP;
//        if (Pointer.NULL != m_lpPicHandle) {
//            iRet = ITF.NETDEV_StopPicStream(m_lpPicHandle);
//            if (NetDEVEnum.TRUE == iRet) {
//                m_lpPicHandle = Pointer.NULL;
//            }
//        }
//        //  iRet = JOptionPane.showConfirmDialog(this, "是否进行断网续传？", "", JOptionPane.YES_NO_OPTION);
//        ifReTran = NetDEVEnum.TRUE;
////        if (JOptionPane.YES_OPTION == iRet) {
////        } else {
////            ifReTran = NetDEVEnum.FALSE;
////        }
//        if (localIP.isEmpty()) {
//            log.info("本地IP为空.");
//            // JOptionPane.showMessageDialog(this, "本地IP为空.");
//            return;
//        }
//        reTranIP = localIP;
////        m_lpPicHandle = ITF.NETDEV_StartPicStream(m_lpDevHandle, Pointer.NULL, ifReTran, reTranIP, multiPicDataCallBackFun, Pointer.NULL);
//        m_lpPicHandle = ITF.NETDEV_StartPicStream(m_lpDevHandle, NetDEVSdk.m_lpPicWndHandle, ifReTran, reTranIP, multiPicDataCallBackFun, Pointer.NULL);
//
//        if (Pointer.NULL == m_lpPicHandle) {
//            log.info("照片流起流失败.");
//        } else {
//            //   bPicPlay = true;
//            log.info("照片流起流成功.");
//        }
//        //JOptionPane.showMessageDialog(this, errMessage);
//    }//GEN-LAST:event_btnPicPlayActionPerformed
//
//    public void btnStopPicPlayActionPerformed() {//GEN-FIRST:event_btnStopPicPlayActionPerformed
//        if (Pointer.NULL ==m_lpPicHandle) {
//            log.info("当前未启用照片流");
//            return;
//        }
//
//        int iRet;
//
//        iRet = ITF.NETDEV_StopPicStream(m_lpPicHandle);
//        if (NetDEVEnum.TRUE != iRet) {
//            log.info(String.format("照片流停流失败: %d.", iRet));
//            return;
//        } else {
//            log.info("照片流停流成功.");
//        }
//        m_lpPicHandle = Pointer.NULL;
//    }//GEN-LAST:event_btnStopPicPlayActionPerformed


    //用户登录
    public boolean loginPerformed(String userName, String passWord, String deviceIP, int port) {//GEN-FIRST:event_btnLoginActionPerformed
        ip=deviceIP;
        if (Pointer.NULL !=NetDEVSdk.m_lpDevHandle) {
            log.info("用户已登录！");
            ITF.NETDEV_Logout(NetDEVSdk.m_lpDevHandle);
            return false;
        }
//        String userName = tfUserName.getText();
//        String passWord = tfPassWord.getText();
//        String deviceIP = tfDeviceIP.getText();
        short wDevPort = (short) port;
        NETDEV_DEVICE_INFO_S.ByReference pstDevInfo = new NETDEV_DEVICE_INFO_S.ByReference();

        NetDEVSdk.m_lpDevHandle = ITF.NETDEV_Login(deviceIP, wDevPort, userName, passWord, pstDevInfo);
        if (Pointer.NULL != NetDEVSdk.m_lpDevHandle) {
            log.info("NETDEV_Login succeed.");
            btnPicPlayActionPerformed();

        } else {
            log.info("NETDEV_Login failed.");
        }
        // btnPicPlayActionPerformed("");
        // JOptionPane.showMessageDialog(this, errMessage);
        return true;
        //  ITF.NETDEV_SetStatusCallBack(StatusReportCallBack);
    }//GEN-LAST:event_btnLoginActionPerformed
//
//    public void logoutPerformed() {//GEN-FIRST:event_btnLogoutActionPerformed
//        int iRet;
//        String errMessage;
//        if (Pointer.NULL == m_lpDevHandle) {
//            return;
//        }
//
//        if (Pointer.NULL != m_lpPlayHandle) {
//            ITF.NETDEV_StopRealPlay(m_lpPlayHandle);
//        }
//
//        if (Pointer.NULL != m_lpPicHandle) {
//            ITF.NETDEV_StopPicStream(m_lpPicHandle);
//        }
//
//        iRet = ITF.NETDEV_Logout(m_lpDevHandle);
//        if (NetDEVEnum.TRUE == iRet) {
//            log.info("登出成功.");
//            //  JOptionPane.showMessageDialog(this, errMessage);
//        } else {
//            log.info(String.format("登出失败, 错误码: %d.", iRet));
//            //  JOptionPane.showMessageDialog(this, errMessage);
//            return;
//        }
//
//        m_lpDevHandle = Pointer.NULL;
//        m_lpPlayHandle = Pointer.NULL;
//        m_lpPicHandle = Pointer.NULL;
//    }//GEN-LAST:event_btnLogoutActionPerformed


    public static void initSDK(ImosSdkInterface it) {
        ITF = it;
        int iRet;
        iRet = ITF.NETDEV_Init();
        if (NetDEVEnum.TRUE != iRet) {
            log.info(String.format("NETDEV_Init failed, error code: %d.", iRet));
            //  JOptionPane.showMessageDialog(this, errMessage);
        }
    }

    public void cleanUpSDK() {
        int iRet;
        iRet = ITF.NETDEV_Cleanup();
        if (NetDEVEnum.TRUE != iRet) {
            log.info(String.format("NETDEV_Cleanup failed, error code: %d.", iRet));
            //JOptionPane.showMessageDialog(this, errMessage);
        }
    }

    public void getLocalIPAddress() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            //   LOGGER.error("Error" + ex.getMessage(), ex);
        }

        //获取本机ip
        if (null != addr) {
            String ip = addr.getHostAddress();
            if (ip.length() > 0) {
                tfLocalIP = ip;
            }
        }
    }



    /**
     * 同步抓拍
     *
     */
    public void CaptureSyncAction(String preNo) {
        if (Pointer.NULL == m_lpPicHandle) {
            return;
        }
        AwsCarNo carNo = new AwsCarNo();
        int iRet;
        File file;
        Pointer p;
        byte[] imageData;
        String szTmpFile = null;
        String strCarPlate;
        int iColorIndex = 4;
        ByteByReference[] picData;
        NETDEV_PIC_DATA_S stPicData;
        FileOutputStream fileOutputStream;
        String[] rowValues = {"", "", "", "", "", ""};
        String[] arrPlateColor = {"白", "黄", "蓝", "黑", "其他"};
        String[] carType = {"未知", "小型车", "中型车", "大型车", "其他"};

        PointerByReference pstPicData = new PointerByReference();
        String dirPath = CURRENTDIRECTORY + File.separator + "pic" + File.separator;
        //TODO 抓拍并存储到pstPicData,最终存储到stpicData
        iRet = ITF.NETDEV_TriggerSync(m_lpDevHandle, pstPicData);
        if (NetDEVEnum.TRUE != iRet) {
            return;
        }
        stPicData = new NETDEV_PIC_DATA_S(pstPicData.getValue());
        picData = stPicData.apcData;
        file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
        //TODO 循环处理抓拍数据，图片存入本地
        for (int i = 0; i < stPicData.ulPicNumber; i++) {
            p = picData[i].getPointer();
            imageData = p.getByteArray(0, stPicData.aulDataLen[i]);
            strCarPlate = new String(stPicData.szCarPlate).trim();
            if (strCarPlate.length() > 1) {
                szTmpFile = String.format("%s_%s_%d.jpg", dirPath + preNo, strCarPlate, i);
            } else {
                szTmpFile = String.format("%s_%d.jpg", dirPath + preNo.trim(), i);
            }

            try {
                file = new File(szTmpFile);
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(imageData);
                fileOutputStream.close();
            } catch (IOException ex) {
                LOGGER.error("Error" + ex.getMessage(), ex);
            }
        }
//TODO 判断车牌颜色
        if ((stPicData.lPlateColor >= 0) && (stPicData.lPlateColor <= 4)) {
            iColorIndex = stPicData.lPlateColor;
        }
//        rowValues[0] = Integer.toString(stPicData.ulRecordID);
//        rowValues[1] = new String(stPicData.szPassTime);
//        rowValues[2] = Integer.toString(stPicData.lLaneID);
//        rowValues[3] = arrPlateColor[iColorIndex];
        int code = stPicData.lVehicleType;
        int speed = stPicData.lVehicleSpeed;
        rowValues[0] = Integer.toString(stPicData.ulRecordID);

        carNo.setCode(ip+"_"+carType[code]);
        rowValues[1] = new String(stPicData.szPassTime);
        carNo.setCreateTime(new Date());

        //车道信息
        rowValues[2] = Integer.toString(stPicData.lLaneID);
        carNo.setLane(stPicData.lLaneID);
        rowValues[3] = arrPlateColor[iColorIndex];
        carNo.setColor(iColorIndex);
        try {
            rowValues[4] = new String(stPicData.szCarPlate, "GB2312").trim();
            carNo.setCarNo(rowValues[4]);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Error" + ex.getMessage(), ex);
        }
        rowValues[5] = "是";
        //插入aws_car_no这个表
        unvCarNoCore.noService.insertCarNo(carNo);
        AwsPreCheckData pre=new AwsPreCheckData();
        pre.setPreNo(preNo);
        pre.setCarNo(rowValues[4]);
        pre.setImg(szTmpFile);
        pre.setCreateTime(new Date());
        //插入/更新per_check_data这个表
        AwsPreCheckData preCheckData=unvCarNoCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo,preNo));
        if (preCheckData==null)
            unvCarNoCore.preCheckDataMapper.insert(pre);
        else {
            unvCarNoCore.preCheckDataMapper.update(pre,new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo,preNo));
        }

    }//GEN-LAST:event_btnCaptureSyncActionPerformed


}
