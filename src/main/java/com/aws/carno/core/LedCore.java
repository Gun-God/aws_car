package com.aws.carno.core;


import com.aws.carno.Utils.LedUtil;
import com.aws.carno.Utils.RTXDataParse;
import com.aws.carno.Utils.RtxCommUtil;
import com.aws.carno.Utils.RxtxBuilder;
import com.aws.carno.domain.AwsLed;
import com.aws.carno.mapper.AwsLedMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    AwsLedMapper ledMapper;

    @PostConstruct
    public void init(){
        ledCore.ledMapper=this.ledMapper;
    }

    public LedCore(){

    }

    public LedCore(String com_name,String content){
        this.com_name=com_name;
        this.msg=content;
    }

    public void startMain(String name, int bits, String msg) {
        //开启串口
        RtxCommUtil commUtil = RxtxBuilder.init(this.com_name, bits, 0,null,0);
        assert commUtil != null;
        byte[] bytes = LedUtil.ledTextGen(this.msg, "GB18030");
        //byte[] bytes = LedUtil.ledTextGen(msg, "GB18030");
//        byte[] bytes=null;
//        try {
//            bytes = RTXDataParse.setTimeTextGen("utf-8");
//        }
//        catch (Exception e){
//
//        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commUtil.send(bytes);

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