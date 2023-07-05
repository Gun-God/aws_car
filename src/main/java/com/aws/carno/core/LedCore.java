package com.aws.carno.core;


import com.aws.carno.Utils.LedUtil;
import com.aws.carno.Utils.RTXDataParse;
import com.aws.carno.Utils.RtxCommUtil;
import com.aws.carno.Utils.RxtxBuilder;
import com.aws.carno.domain.AwsCarTypeIdRelation;
import com.aws.carno.domain.AwsLed;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.mapper.AwsCarTypeIdRelationMapper;
import com.aws.carno.mapper.AwsLedMapper;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import gnu.io.SerialPort;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author :hyw
 * @version 1.0
 * @description 情报板核心类
 * @date : 2023/06/20 11:21
 */

public class LedCore {
    public static LedCore ledCore;

    @Autowired
    AwsLedMapper ledMapper;

    @PostConstruct
    public void init(){
        ledCore.ledMapper=this.ledMapper;
    }


    public void startMain(String name, int bits, String msg) {
        //开启串口
        RtxCommUtil commUtil = RxtxBuilder.init(name, bits, 0,null,0);
        assert commUtil != null;
       // byte[] bytes = LedUtil.ledTextGen("请苏FE1861进站检测", "GB18030");
        byte[] bytes = LedUtil.ledTextGen(msg, "GB18030");
        commUtil.send(bytes);
        commUtil.ClosePort();
        AwsLed led = new AwsLed();
        led.setContent(msg);
        led.setCreateTime(new Date());
        led.setOrgCode("027");
        ledMapper.insert(led);

        // TODO Auto-generated method stub
    }

}