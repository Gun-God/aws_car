package com.aws.carno.core;

import com.aws.carno.Interface.HCNetSDK;
import com.aws.carno.Interface.ImosSdkInterface;
import com.aws.carno.domain.AwsScan;
import com.aws.carno.mapper.AwsScanMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/20 11:27
 * @description 启动类
 */
@Slf4j
@Component
@Order(1)
public class StartCore implements CommandLineRunner {
    public StartCore startCore;

    @Autowired
    AwsScanMapper scanMapper;
    @PostConstruct
    public void init(){
        startCore.scanMapper=this.scanMapper;
    }


    public static Map<String, UnvCarNoCore> UnvMaps = new HashMap<>();
    public static Map<String, HikCarNoCore> HikMaps = new HashMap<>();
    public static ConcurrentHashMap<byte[],String> hashMap=new ConcurrentHashMap<>();




    public void carWeighStart() {
        QueryWrapper<AwsScan> qw = new QueryWrapper<>();
        qw.eq("state", 1);
        qw.eq("type", 2);
        List<AwsScan> list = scanMapper.selectList(qw);
        for (AwsScan scan : list) {
            CarWeightCore cRead = new CarWeightCore();
            cRead.startMain(scan.getPortName(), 5600, scan.getCode(), scan.getFactory());
        }


//            try {
//                String st = "哈哈----你好";
//                System.out.println("发出字节数：" + st.getBytes("gbk").length);
////                cRead.outputStream.write(st.getBytes("gbk"), 0,
////                        st.getBytes("gbk").length);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
    }

    public void UnvCarNoStart() {
        ImosSdkInterface it = ImosSdkInterface.instance;
        UnvCarNoCore.initSDK(it);
        log.info("初始化宇视SDK--仅此一次");
        QueryWrapper<AwsScan> qw = new QueryWrapper<>();
        qw.eq("state", 1);
        qw.eq("type", 3);
        qw.eq("factory", 1);
        List<AwsScan> list = scanMapper.selectList(qw);
        for (AwsScan scan : list) {
            UnvCarNoCore st = new UnvCarNoCore();
            log.info("开始登录");
            boolean isRet = st.loginPerformed(scan.getUserName(), scan.getPassword(), scan.getVideoIp(), scan.getPort());
            if (!isRet)
                st.loginPerformed(scan.getUserName(), scan.getPassword(), scan.getVideoIp(), scan.getPort());
            UnvMaps.put(scan.getCode(), st);
        }


//        if (m_lpDevHandle != Pointer.NULL) {
//            log.info("开始车牌抓拍");
//            st.btnPicPlayActionPerformed(m_lpDevHandle);
//        }
    }

    public void HikCarNoStart() {

        if (HikCarNoCore.hCNetSDK == null) {
            if (!HikCarNoCore.createSDKInstance()) {
                System.out.println("Load SDK fail");
                return;
            }
        }

        /**初始化*/
        HikCarNoCore.hCNetSDK.NET_DVR_Init();
        System.err.println("初始化海康sdk");
        /**加载日志*/
        HikCarNoCore.hCNetSDK.NET_DVR_SetLogToFile(3, "./sdklog", false);
        /** 设备上传的报警信息是COMM_VCA_ALARM(0x4993)类型，
         在SDK初始化之后增加调用NET_DVR_SetSDKLocalCfg(enumType为NET_DVR_LOCAL_CFG_TYPE_GENERAL)设置通用参数NET_DVR_LOCAL_GENERAL_CFG的byAlarmJsonPictureSeparate为1，
         将Json数据和图片数据分离上传，这样设置之后，报警布防回调函数里面接收到的报警信息类型为COMM_ISAPI_ALARM(0x6009)，
         报警信息结构体为NET_DVR_ALARM_ISAPI_INFO（与设备无关，SDK封装的数据结构），更便于解析。*/
        HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG struNET_DVR_LOCAL_GENERAL_CFG = new HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG();
        struNET_DVR_LOCAL_GENERAL_CFG.byAlarmJsonPictureSeparate = 1;   //设置JSON透传报警数据和图片分离
        struNET_DVR_LOCAL_GENERAL_CFG.write();
        Pointer pStrNET_DVR_LOCAL_GENERAL_CFG = struNET_DVR_LOCAL_GENERAL_CFG.getPointer();
        HikCarNoCore.hCNetSDK.NET_DVR_SetSDKLocalCfg(17, pStrNET_DVR_LOCAL_GENERAL_CFG);
        QueryWrapper<AwsScan> qw = new QueryWrapper<>();
        qw.eq("state", 1);
        qw.eq("type", 3);
        qw.eq("factory", 2);
        List<AwsScan> list = scanMapper.selectList(qw);
        //   startListen("10.16.36.108",(short)7200);//报警监听，不需要登陆设备
        for (AwsScan scan : list) {
            String ip = scan.getVideoIp();
            int port = scan.getPort();
            HikCarNoCore hik = new HikCarNoCore(ip, (short) port);
            HikMaps.put(scan.getCode(), hik);

        }


    }


    @Override
    public void run(String... args) throws Exception {
        UnvCarNoStart();
        HikCarNoStart();
        carWeighStart();
    }
}
