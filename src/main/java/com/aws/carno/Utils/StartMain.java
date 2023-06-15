package com.aws.carno.Utils;

import com.aws.carno.Enum.NetDEVEnum;
import com.aws.carno.Interface.ImosSdkInterface;
import com.aws.carno.Struct.NETDEV_DEVICE_INFO_S;
import com.aws.carno.Struct.NETDEV_PIC_DATA_S;
import com.aws.carno.domain.AwsCarNo;
import com.aws.carno.service.AwsCarNoService;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/12 12:09
 */
@Slf4j
@Component
@Order(1)
public class StartMain implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(StartMain.class.getName());
    private static final ImosSdkInterface ITF = ImosSdkInterface.instance;
    private static final String CURRENTDIRECTORY = System.getProperty("user.dir");
    private ImosSdkInterface.NETDEV_PIC_UPLOAD_PF multiPicDataCallBackFun = null;
    private int m_lChannelID = 1;
    private String tfLocalIP;

    @Autowired
    AwsCarNoService noService;


    public StartMain() {
        //自动抓拍回调函数
        multiPicDataCallBackFun = new ImosSdkInterface.NETDEV_PIC_UPLOAD_PF() {
            @Override
            public void callback(NETDEV_PIC_DATA_S.ByReference pstPicData, Pointer lpUserParam) {
                AwsCarNo carNo = new AwsCarNo();
                int i;
                Pointer p;
                File file;
                int colorIndex;
                byte[] imageData;
                String szTmpFile;
                String strCarPlate;
                String strPassTime;
                FileOutputStream fileOutputStream;
                ByteByReference[] picData = pstPicData.apcData;
                String[] rowValues = {"", "", "", "", "", ""};
                String arrPlateColor[] = {"白", "黄", "蓝", "黑", "其他", "绿", "红", "黄绿", "渐变绿"};
                String[] carType = {"未知", "小型车", "中型车", "大型车", "其他"};
                String szTmp = CURRENTDIRECTORY + File.separator + "pic" + File.separator;

                file = new File(szTmp);
                if (!file.exists()) {
                    file.mkdir();
                }

                strPassTime = new String(pstPicData.szPassTime).trim();
                for (i = 0; i < pstPicData.ulPicNumber; i++) {
                    p = picData[i].getPointer();
                    imageData = p.getByteArray(0, pstPicData.aulDataLen[i]);
                    strCarPlate = new String(pstPicData.szCarPlate).trim();

                    if (strCarPlate.length() > 1) {
                        szTmpFile = String.format("%s_%s_%d.jpg", szTmp + strPassTime, strCarPlate, i);
                    } else {
                        szTmpFile = String.format("%s_%d.jpg", szTmp + strPassTime, i);
                    }

                    try {
                        file = new File(szTmpFile);
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        fileOutputStream = new FileOutputStream(file);
                        try {
                            fileOutputStream.write(imageData);
                        } finally {
                            fileOutputStream.close();
                        }
                    } catch (FileNotFoundException ex) {
                        LOGGER.error("Error" + ex.getMessage(), ex);
                    } catch (IOException ex) {
                        LOGGER.error("Error" + ex.getMessage(), ex);
                    }
                }

                if ((pstPicData.lPlateColor >= 0) && (pstPicData.lPlateColor < 9)) {
                    colorIndex = pstPicData.lPlateColor;
                } else {
                    colorIndex = 4;
                }

                int code = pstPicData.lVehicleType;
                int speed = pstPicData.lVehicleSpeed;
                rowValues[0] = Integer.toString(pstPicData.ulRecordID);
                carNo.setCode(carType[code]);
                rowValues[1] = new String(pstPicData.szPassTime);
                carNo.setCreateTime(new Date());
                rowValues[2] = Integer.toString(pstPicData.lLaneID);
                carNo.setLane(pstPicData.lLaneID);
                rowValues[3] = arrPlateColor[colorIndex];
                carNo.setColor(rowValues[3]);

                try {
                    rowValues[4] = new String(pstPicData.szCarPlate, "GB2312").trim();
                    carNo.setCarNo(rowValues[4]);
                } catch (UnsupportedEncodingException ex) {
                    LOGGER.error("Error" + ex.getMessage(), ex);
                }

                rowValues[5] = "否";
                noService.insertCarNo(carNo);

                // vehicleInfoModel.addRow(rowValues);
            }
        };
    }


    //实况抓拍
    private void btnPicPlayActionPerformed() {//GEN-FIRST:event_btnPicPlayActionPerformed
        if (Pointer.NULL == NetDEVSdk.m_lpDevHandle) {
            log.info("未登录");
            return;
        }
        int iRet;
        int ifReTran;
        String errMessage;
        String localIP = tfLocalIP;
        String reTranIP;
        if (Pointer.NULL != NetDEVSdk.m_lpPicHandle) {
            iRet = ITF.NETDEV_StopPicStream(NetDEVSdk.m_lpPicHandle);
            if (NetDEVEnum.TRUE == iRet) {
                NetDEVSdk.m_lpPicHandle = Pointer.NULL;
            }
        }
        //  iRet = JOptionPane.showConfirmDialog(this, "是否进行断网续传？", "", JOptionPane.YES_NO_OPTION);
        ifReTran = NetDEVEnum.TRUE;
//        if (JOptionPane.YES_OPTION == iRet) {
//        } else {
//            ifReTran = NetDEVEnum.FALSE;
//        }
        if ((localIP.isEmpty()) && (NetDEVEnum.TRUE == ifReTran)) {
            log.info("本地IP为空.");
            // JOptionPane.showMessageDialog(this, "本地IP为空.");
            return;
        }

        if ((localIP.isEmpty()) || (NetDEVEnum.FALSE == ifReTran)) {
            reTranIP = "";
        } else {
            reTranIP = localIP;
        }

        NetDEVSdk.m_lpPicHandle = ITF.NETDEV_StartPicStream(NetDEVSdk.m_lpDevHandle, NetDEVSdk.m_lpPicWndHandle, ifReTran, reTranIP, multiPicDataCallBackFun, Pointer.NULL);
        if (Pointer.NULL == NetDEVSdk.m_lpPicHandle) {
            log.info("照片流起流失败.");
            errMessage = "照片流起流失败";
        } else {
            //   NetDEVSdk.bPicPlay = true;
            log.info("照片流起流失败.");
            errMessage = "照片流起流成功";
        }
        //JOptionPane.showMessageDialog(this, errMessage);
    }//GEN-LAST:event_btnPicPlayActionPerformed

    private void btnStopPicPlayActionPerformed() {//GEN-FIRST:event_btnStopPicPlayActionPerformed
        if (Pointer.NULL == NetDEVSdk.m_lpPicHandle) {
            log.info("当前未启用照片流");
            return;
        }

        int iRet;
        String errMessage;

        iRet = ITF.NETDEV_StopPicStream(NetDEVSdk.m_lpPicHandle);
        if (NetDEVEnum.TRUE != iRet) {
            log.info(String.format("照片流停流失败: %d.", iRet));
            return;
        } else {
            log.info("照片流停流成功.");
        }

        NetDEVSdk.m_lpPicHandle = Pointer.NULL;
        NetDEVSdk.bPicPlay = false;
    }//GEN-LAST:event_btnStopPicPlayActionPerformed


    //用户登录
    private boolean loginPerformed(String userName, String passWord, String deviceIP, String port) {//GEN-FIRST:event_btnLoginActionPerformed
        boolean status = false;
        if (Pointer.NULL != NetDEVSdk.m_lpDevHandle) {
            log.info("用户已登录！");
            ITF.NETDEV_Logout(NetDEVSdk.m_lpDevHandle);
            return true;
        }

//        String userName = tfUserName.getText();
//        String passWord = tfPassWord.getText();
//        String deviceIP = tfDeviceIP.getText();
        short wDevPort = Short.parseShort(port);
        NETDEV_DEVICE_INFO_S.ByReference pstDevInfo = new NETDEV_DEVICE_INFO_S.ByReference();

        NetDEVSdk.m_lpDevHandle = ITF.NETDEV_Login(deviceIP, wDevPort, userName, passWord, pstDevInfo);
        if (Pointer.NULL != NetDEVSdk.m_lpDevHandle) {
            status = true;
            log.info("NETDEV_Login succeed.");
        } else {
            log.info("NETDEV_Login failed.");
        }

        // JOptionPane.showMessageDialog(this, errMessage);
        return status;
        //  ITF.NETDEV_SetStatusCallBack(StatusReportCallBack);
    }//GEN-LAST:event_btnLoginActionPerformed

    private void logoutPerformed() {//GEN-FIRST:event_btnLogoutActionPerformed
        int iRet;
        String errMessage;
        if (Pointer.NULL == NetDEVSdk.m_lpDevHandle) {
            return;
        }

        if (Pointer.NULL != NetDEVSdk.m_lpPlayHandle) {
            ITF.NETDEV_StopRealPlay(NetDEVSdk.m_lpPlayHandle);
        }

        if (Pointer.NULL != NetDEVSdk.m_lpPicHandle) {
            ITF.NETDEV_StopPicStream(NetDEVSdk.m_lpPicHandle);
        }

        iRet = ITF.NETDEV_Logout(NetDEVSdk.m_lpDevHandle);
        if (NetDEVEnum.TRUE == iRet) {
            log.info("登出成功.");
            //  JOptionPane.showMessageDialog(this, errMessage);
        } else {
            log.info(String.format("登出失败, 错误码: %d.", iRet));
            //  JOptionPane.showMessageDialog(this, errMessage);
            return;
        }

        NetDEVSdk.m_lpDevHandle = Pointer.NULL;
        NetDEVSdk.m_lpPlayHandle = Pointer.NULL;
        NetDEVSdk.m_lpPicHandle = Pointer.NULL;
    }//GEN-LAST:event_btnLogoutActionPerformed


    private void initSDK() {
        int iRet;
        iRet = ITF.NETDEV_Init();
        if (NetDEVEnum.TRUE != iRet) {
            log.info(String.format("NETDEV_Init failed, error code: %d.", iRet));
            //  JOptionPane.showMessageDialog(this, errMessage);
        }
    }

    private void cleanUpSDK() {
        int iRet;
        iRet = ITF.NETDEV_Cleanup();
        if (NetDEVEnum.TRUE != iRet) {
            log.info(String.format("NETDEV_Cleanup failed, error code: %d.", iRet));
            //JOptionPane.showMessageDialog(this, errMessage);
        }
    }

    private void getLocalIPAddress() {
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

    @Override
    public void run(String... args) throws Exception {
        StartMain st = new StartMain();
        log.info("初始化SDK--仅此一次");
        st.initSDK();
        log.info("开始登录");
        boolean s = st.loginPerformed("admin", "123456", "192.168.3.2", "80");
        if (s) {
            log.info("开始车牌抓拍");
            st.btnPicPlayActionPerformed();
        }

    }
}
