package com.aws.carno.core;

import com.aws.carno.Interface.HCNetSDK;
import com.aws.carno.Interface.ImosSdkInterface;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.domain.AwsScan;
import com.aws.carno.domain.AwsTempCarnoData;
import com.aws.carno.domain.AwsTempWeightData;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.aws.carno.mapper.AwsScanMapper;
import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.aws.carno.mapper.AwsTempWeightDataMapper;
import com.aws.carno.service.AwsTempWeightDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
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
@EnableAsync
@Order(1)
public class StartCore implements CommandLineRunner {
//public class StartCore{
    public StartCore startCore;

    @Autowired
    AwsScanMapper scanMapper;
    @Autowired
    AwsTempWeightDataMapper tempWeightDataMapper;
    @Autowired
    AwsTempCarnoDataMapper tempCarnoDataMapper;
    @Autowired
    AwsPreCheckDataMapper preCheckDataMapper;

//    @PostConstruct
//    public void init(){
//        startCore.scanMapper=this.scanMapper;
//    }
    public static Map<String, UnvCarNoCore> UnvMaps = new HashMap<>();
    public static Map<String, HikCarNoCore> HikMaps = new HashMap<>();
    public static Map<String,AwsScan> HikScanMaps=new HashMap<>();
    public static Map<String,AwsScan> weightScanMaps=new HashMap<>();
    public static ConcurrentHashMap<Integer,String> hashMap=new ConcurrentHashMap<>();

    public StartCore()
    {

    }

//    public void process_preCheckData() {
//        int timeThreshold = 2; //时间有效阈值为2秒
//        while (true) {
//            //读取摄像头的表,查询最新的一条数据
//            AwsTempCarnoData tempCarnoData = tempCarnoDataMapper.selectOne(new QueryWrapper<AwsTempCarnoData>().lambda().orderByDesc(AwsTempCarnoData::getPassTime).last("limit 1"));
//
//            //如果不为空
//            if(tempCarnoData!=null &&  tempCarnoData.getLane()!=null)
//            {
//                //搜索该车道里 符合最新的数据
//                AwsTempWeightData tempWeightData=tempWeightDataMapper.selectOne(new QueryWrapper<AwsTempWeightData>().lambda().eq(AwsTempWeightData::getLane,tempCarnoData.getLane()).orderByDesc(AwsTempWeightData::getPassTime).last("limit 1"));
//
//                //计算时间差
//                Date w_passtime=tempWeightData.getPassTime();
//                Date c_passTime=tempCarnoData.getPassTime();
//                long interval = (c_passTime.getTime() -  w_passtime.getTime())/1000;
//
//
//                if(interval>0)
//                {
//                    if(interval<=timeThreshold)
//                    {
//
//                        //插入到precheckdata
//                        AwsPreCheckData pcData=new AwsPreCheckData();
//                        pcData.setCreateTime(tempWeightData.getCreateTime());
//                        pcData.setPassTime(tempWeightData.getPassTime());
//                        pcData.setSpeed(tempWeightData.getSpeed());
//                        pcData.setAxisNum(tempWeightData.getAxisNum());
//                        pcData.setPreNo(tempWeightData.getPreNo());
//                        pcData.setLane(tempWeightData.getLane());
//                        pcData.setOrgCode(tempWeightData.getOrgCode());
//                        pcData.setCarTypeId(tempWeightData.getCarTypeId());
//                        pcData.setLimitAmt(tempWeightData.getLimitAmt());
//                        pcData.setPreAmt(tempWeightData.getPreAmt());
//                        pcData.setCarNo(tempCarnoData.getCarNo());
//                        pcData.setImg(tempCarnoData.getImg());
//
//                        preCheckDataMapper.insert(pcData);
//
//                        //删除两个表的数据
//                        tempWeightDataMapper.delete(new QueryWrapper<AwsTempWeightData>().lambda().eq(AwsTempWeightData::getId,tempWeightData.getId()));
//
//                        tempCarnoDataMapper.delete(new QueryWrapper<AwsTempCarnoData>().lambda().eq(AwsTempCarnoData::getId,tempCarnoData.getId()));
//
//                    }//如果符合阈值则插入percheck，并删除两个表中的这条数据，否则摄像头数据删除，秤台数据保留
//                    else{
//
//                    }
//                }//如果小于0可能获取到的秤台数据应该与之后摄像头的数据匹配，而不是此条数据
//            }
//
//
//        }
//
//    }

    @Async
    public void carWeighStart() {
        QueryWrapper<AwsScan> qw = new QueryWrapper<>();
        qw.eq("state", 1);
        qw.eq("type", 2);
        //List<AwsScan> list = scanMapper.selectList(qw);
//        CarWeightCore cRead = new CarWeightCore();
//        cRead.startMain("COM1", 115200, "0", 0);
        List<AwsScan> list = scanMapper.selectList(qw);
        for (AwsScan scan : list) {
            //AwsScan awsScan=scanMapper.selectOne(new QueryWrapper<AwsScan>().eq("code",scan.getCode()));

           /// AwsScan awsScan=scanMapper.selectOne(new QueryWrapper<AwsScan>().eq("code",scan.getReCode()));

            CarWeightCore cRead = new CarWeightCore(scan.getPortName(), 115200, null, 0);
            Thread thread=new Thread(cRead);
            thread.start();
            weightScanMaps.put(scan.getPortName(),scan);
           // cRead.startMain();//异步线程处理数据
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //波特率    5600
//           cRead.startMain();
            //cRead.startMain(scan.getPortName(), 5600, scan.getCode(), scan.getFactory());

        }
    }

    public void UnvCarNoStart() {
        ImosSdkInterface it = ImosSdkInterface.instance;
        //TODO 初始化sdk
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
        //TODO 添加海康设备到list
        List<AwsScan> list = scanMapper.selectList(qw);
        //startListen("10.16.36.108",(short)7200);//报警监听，不需要登陆设备
        //TODO 将list设备加载进hikMaps中
        for (AwsScan scan : list) {
            String ip = scan.getVideoIp();
            HikScanMaps.put(ip,scan);
        }

        HikCarNoCore hik = new HikCarNoCore("192.168.3.3", (short)7200,"admin", "admin12345");
        //hik.setCameraTime();

        Thread thread=new Thread(hik);
        thread.start();

//        int index=0;
       /* for (AwsScan scan : list) {
            String ip = scan.getVideoIp();
            //int port = scan.getPort();
            String vPort=scan.getVideoPort();
            int listenPort=Integer.parseInt(vPort);

            String user_name = scan.getUserName();
            String password = scan.getPassword();
           // 10.19测试  HikCarNoCore hik = new HikCarNoCore("192.10.12.245", (short)8000);
//            HikCarNoCore hik = new HikCarNoCore(ip, (short)port,user_name, password);
//            Thread thread=new Thread(hik);
//            thread.start();

            HikCarNoCore hik = new HikCarNoCore(ip, (short)listenPort,user_name, password);
            Thread thread=new Thread(hik);
            thread.start();



            // hik.processing_Data();
            System.err.println("加载设备"+scan.getCode());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HikMaps.put(scan.getCode(), hik);

             }*/
    }
    @Override
    public void run(String... args) throws Exception {
        //UnvCarNoStart();
//        Thread myThread = new Thread(new Runnable(){
//            @Override
//            public void run() {
//                // 在 run() 方法中编写线程要执行的任务。
//                carWeighStart();
//            }
//        });
//        myThread.start();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        process_preCheckData();


        carWeighStart();
        HikCarNoStart();

        //new ScoketWeightCore("192.10.12.243", 3132);
//        LedCore led=new LedCore();
////        led.startMain("COM1",9600,"请苏FE1861进站检测");
//        led.startMain("COM7",115200,"请苏FE1861进站检测！");
    }
}
