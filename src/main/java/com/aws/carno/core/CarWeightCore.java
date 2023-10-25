package com.aws.carno.core;


import com.aws.carno.Utils.RTXDataParse;
import com.aws.carno.Utils.RtxCommUtil;
import com.aws.carno.Utils.RxtxBuilder;
import com.aws.carno.Utils.StringUtil;
import com.aws.carno.domain.AwsCarType;
import com.aws.carno.domain.AwsCarTypeIdRelation;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.mapper.AwsCarTypeIdRelationMapper;
import com.aws.carno.mapper.AwsCarTypeMapper;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import static com.aws.carno.Utils.RTXDataParse.hexStrToByteArray;

/**
 * @author :hyw
 * @version 1.0
 * @description 称台核心类
 * @date : 2023/06/20 11:21
 */
@Component
public class CarWeightCore {

    public static CarWeightCore carWeightCore;
    @Autowired
    public AwsCarTypeIdRelationMapper relationMapper;
    @Autowired
    public AwsPreCheckDataMapper preCheckDataMapper;
    @Autowired
    public  AwsCarTypeMapper carTypeMapper;

    @PostConstruct
    public void init() {
        carWeightCore=this;
        carWeightCore.preCheckDataMapper = this.preCheckDataMapper;
        carWeightCore.relationMapper = this.relationMapper;
        carWeightCore.carTypeMapper = this.carTypeMapper;
    }


//    public  void  test(){
//        System.err.println(11);
//        System.err.println( carWeightCore.preCheckDataMapper.selectList(null).size());
//    }


    public void startMain(String name, int bits, String code, int factory) {
        //开启串口
        RtxCommUtil commUtil = RxtxBuilder.init(name, bits, 1, code, factory);
        assert commUtil != null;
        // TODO Auto-generated method stub
        try {
            System.out.println("--------------任务处理线程运行了--------------");
            //noinspection InfiniteLoopStatement
            while (true) {
                // 如果堵塞队列中存在数据就将其输出
                if (commUtil.msgQueue.size() > 0) {
                    //TODO 将消息队列的消息转化成哈希码，后面利用哈希码取出哈希map中的唯一流水号
                    byte[] bytes = commUtil.msgQueue.take();
                    String hex = new BigInteger(1, bytes).toString(16);
//                    System.out.println(hex);
                    StringBuffer hex1 = new StringBuffer();
                    for (int i=0;i<hex.length();i+=2){
                        if(i+2<=hex.length()){
                            hex1.append(hex.substring(i,i+2));
                            hex1.append(" ");
                        }


                    }


                    System.out.println(hex1);

                    BufferedWriter writer = new BufferedWriter(new FileWriter("E:/com_data/output.txt", true));

                    writer.write(String.valueOf(hex1));
                    writer.newLine(); // 写入换行
                    writer.newLine(); // 写入换行

                    // 在这里写入文件

                    writer.close();

                    // System.err.println(new String(bytes, Charset.defaultCharset()));
                    byte[] bytes1=hexStrToByteArray(hex1.toString());

                    int hashCode= Arrays.hashCode(bytes);
                    //将称台字节数据解析到实体类
                    AwsPreCheckData preCheckData = RTXDataParse.byteArrayToObjData(bytes1);
                    //TODO 打印实体类数据(称重信息,重点看流水号是否绑定成功)
                    System.out.println("=========================================================");
                    System.out.println("称重台检测内容：");
                    System.err.println(preCheckData);
                    String preNo = StartCore.hashMap.get(hashCode);

                    AwsCarTypeIdRelation relation = carWeightCore.relationMapper.selectOne(new QueryWrapper<AwsCarTypeIdRelation>().lambda().eq(AwsCarTypeIdRelation::getVehType, preCheckData.getCarTypeId()));
                    int carTypeId = 0;
                    if (relation != null) {
                        carTypeId = relation.getCarTypeId();
                    }
                    AwsCarType carType = carWeightCore.carTypeMapper.selectById(carTypeId);
                    if (carType != null)
                        preCheckData.setLimitAmt(carType.getLimitAmt());
                    else
                        preCheckData.setLimitAmt(0d);
                    preCheckData.setCreateTime(new Date());
                    preCheckData.setCarTypeId(carTypeId);
                    //TODO 查重
//                    AwsPreCheckData p = carWeightCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo, preNo));
                    //TODO 如果是空的，则插入称重,最大限重，车辆类型等内容插入数据库
                    carWeightCore.preCheckDataMapper.insert(preCheckData);
//                    sqlSession.commit();

//                    AwsPreCheckData p = carWeightCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo, preNo));
//                    System.err.println("p:" + p);
//                    else {
//                        carWeightCore.preCheckDataMapper.update(preCheckData, new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo, preNo));
//                    }
                    System.err.println("车道:" + preCheckData.getLane());
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}