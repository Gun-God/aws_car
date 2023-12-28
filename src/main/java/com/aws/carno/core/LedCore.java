package com.aws.carno.core;


import com.aws.carno.Utils.LedUtil;
import com.aws.carno.Utils.RTXDataParse;
import com.aws.carno.Utils.RtxCommUtil;
import com.aws.carno.Utils.RxtxBuilder;
import com.aws.carno.domain.AwsLed;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.mapper.AwsLedMapper;
import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.aws.carno.service.AwsTempCarnoDataService;
import com.aws.carno.service.impl.AwsTempCarnoDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author :hyw
 * @version 1.0
 * @description 情报板核心类
 * @date : 2023/06/20 11:21
 */
public class LedCore  implements Runnable {
    public static LedCore ledCore;
    public String com_name;
    public String msg;
    public RtxCommUtil commUtil;
    @Autowired
    AwsLedMapper ledMapper;
//    @Autowired
//    AwsTempCarnoDataServiceImpl tempCarnoDataServiceImpl;

    @PostConstruct
    public void init(){
        ledCore.ledMapper=this.ledMapper;
//        ledCore.tempCarnoDataServiceImpl=this.tempCarnoDataServiceImpl;
    }

    public LedCore(){

    }

    public LedCore(String com_name,String content){
        this.com_name=com_name;
        //this.msg=content;
        this.commUtil = RxtxBuilder.init(this.com_name, 115200, 0,null,0);
        assert commUtil != null;
    }

    public void startMain(String name, int bits, String msg) {
        //开启串口

        try {
            System.out.println("--------------LED任务处理线程运行了--------------");
            //noinspection InfiniteLoopStatement
            while (true) {
                if(AwsTempCarnoDataServiceImpl.carMsgQueue.size()>0)
                {
                    AwsPreCheckData apcd=AwsTempCarnoDataServiceImpl.carMsgQueue.take();
                    System.out.println("超重！"+apcd.getCarNo()+apcd.getPassTime());
//                    byte[] bytes = LedUtil.ledTextGen(apcd.getCarNo(), "GB18030");
//                    this.commUtil.send(bytes);
//                    AwsLed al=new AwsLed();
//                    al.setCreateTime(new Date());
//                    al.setContent(apcd.getCarNo());
//                    al.setOrgCode(apcd.getOrgCode());
//                    ledMapper.insert(al);

//                    ledMapper.insert();
                }
      //  byte[] bytes = LedUtil.ledTextGen(msg, "GB18030")
        byte[] bytes=null;
        try {
            bytes = RTXDataParse.setTimeTextGen("utf-8");

            this.commUtil.send(bytes);
        }
        catch (Exception e){

        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



            }
        }
        catch (Exception e){

        }



        //byte[] bytes = LedUtil.ledTextGen(this.msg, "GB18030");
        //byte[] bytes = LedUtil.ledTextGen(msg, "GB18030");
//        byte[] bytes=null;
//        try {
//            bytes = RTXDataParse.setTimeTextGen("utf-8");
//        }
//        catch (Exception e){
//
//        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
       // commUtil.send(bytes);

//        commUtil.ClosePort();
        //AwsLed led = new AwsLed();
        //led.setContent(msg);
       // led.setCreateTime(new Date());
       // led.setOrgCode("027");
        //ledMapper.insert(led);
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        startMain("请苏FE1861进站检测",9600,"请苏FE1861进站检测");
    }

}