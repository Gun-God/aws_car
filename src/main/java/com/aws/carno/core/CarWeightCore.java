package com.aws.carno.core;


import com.aws.carno.Utils.*;
import com.aws.carno.domain.*;
import com.aws.carno.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
public class CarWeightCore implements Runnable {

    public static CarWeightCore carWeightCore;
    @Autowired
    public AwsCarTypeIdRelationMapper relationMapper;
    @Autowired
    public AwsTempWeightDataMapper tempWeightDataMapper;
    @Autowired
    public AwsPreCheckDataHistoryMapper preCheckDataHistoryMapper;
    @Autowired
    public AwsCarTypeMapper carTypeMapper;
    @Autowired
    public AwsAllWeightDataMapper allWeightDataMapper;
//    @Autowired
//    public AwsTempWeightDataMapper tempWeightDataMapper;
    public String portName;
    public boolean isSetTime;

    public RtxCommUtil commUtil;

    @PostConstruct
    public void init() {
        carWeightCore = this;
        carWeightCore.tempWeightDataMapper = this.tempWeightDataMapper;
        carWeightCore.relationMapper = this.relationMapper;
        carWeightCore.carTypeMapper = this.carTypeMapper;
        carWeightCore.preCheckDataHistoryMapper = this.preCheckDataHistoryMapper;
        carWeightCore.allWeightDataMapper=this.allWeightDataMapper;
//        carWeightCore.tempWeightDataMapper = this.tempWeightDataMapper;
    }

    public CarWeightCore() {

    }

//    public  void  test(){
//        System.err.println(11);
//        System.err.println( carWeightCore.preCheckDataMapper.selectList(null).size());
//    }


    public CarWeightCore(String name, int bits, String code, int factory) {
        //开启串口
        //RtxCommUtil commUtil = RxtxBuilder.init(name, bits, 1, code, factory);
        portName=name;
        isSetTime=false;
        this.commUtil = RxtxBuilder.init(name, bits, 1, code, factory);
      //  assert this.commUtil != null;
        // TODO Auto-generated method stub
    }

//    public void test_insert_weigth()
//    {
//        AwsTempWeightData tempWeightData = new AwsTempWeightData();
//        tempWeightData.setLane(1);
//        carWeightCore.tempWeightDataMapper.insert(tempWeightData);
//    }
//    @Scheduled(cron ="0 0 * * * ?")
  //  @Scheduled(cron ="0 0/5 * * * ?")
    public void setTimeSameWithComputer()
    {
        byte[] bytes = null;
        try {
            bytes=RTXDataParse.setTimeTextGen("UTF-8");
            if(bytes!=null)
            {
//                System.out.println("bytes不为空");

                if(this.commUtil!=null)
                {
                    System.out.println("this.commUtil不为空");
                   this.commUtil.send(bytes);
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startMain() {
        //开启串口
//        RtxCommUtil commUtil = RxtxBuilder.init(name, bits, 1, code, factory);
//        assert commUtil != null;
        // TODO Auto-generated method stub
        try {
            System.out.println("--------------任务处理线程运行了--------------");
            //noinspection InfiniteLoopStatement
            while (true) {
                // 如果堵塞队列中存在数据就将其输出
                if (this.commUtil.msgQueue.size() > 0) {
                    //TODO 将消息队列的消息转化成哈希码，后面利用哈希码取出哈希map中的唯一流水号
                    byte[] bytes = this.commUtil.msgQueue.take();

                    String hex = new BigInteger(1, bytes).toString(16);
//                    System.out.println(hex);
                    StringBuffer hex1 = new StringBuffer();
                    for (int i = 0; i < hex.length(); i += 2) {
                        if (i + 2 <= hex.length()) {
                            hex1.append(hex.substring(i, i + 2));
                            hex1.append(" ");
                        }
                    }


                    //  System.out.println(hex1);

//                    BufferedWriter writer = new BufferedWriter(new FileWriter("E:/com_data/output.txt", true));
//                    writer.write(String.valueOf(hex1));
//                    writer.newLine(); // 写入换行
//                    writer.newLine(); // 写入换行
//                    // 在这里写入文件
//                    writer.close();

                    // System.err.println(new String(bytes, Charset.defaultCharset()));
                    //String hex_weit_data=String.valueOf(hex1);

                    byte[] bytes1 = hexStrToByteArray(hex1.toString());

                    int hashCode = Arrays.hashCode(bytes);
                    //将称台字节数据解析到实体类
                    AwsPreCheckData preCheckData = RTXDataParse.byteArrayToObjData(bytes1);
                    AwsTempWeightData tempWeightData=new AwsTempWeightData();
                    AwsAllWeightData  allWeightData=new AwsAllWeightData();
                    BeanUtils.copyProperties(preCheckData, tempWeightData);
                   //
                    BeanUtils.copyProperties(preCheckData,allWeightData);



                    //test
                    // tempWeightData.setDeviceId(hex_weit_data);
                    //test
                    // tempWeightData.setDeviceId(String.valueOf(hex1));

                    Date time = preCheckData.getPassTime();
                   //commutil通过定时函数无法得到 出现commutil为空的情况 故可以放入一个线程执行
//                    获得当前时间，若为整点则校时
                    // 获取当前日期和时间
                    LocalDateTime now = LocalDateTime.now();
                    int minute = now.getMinute();
                    int second = now.getSecond();
                    if(minute==0 && !isSetTime)
                    {
                        // 调用校时接口
                        isSetTime=true;
                        setTimeSameWithComputer();
                    }
                    if(minute!=0)
                    {
                        isSetTime=false;
                    }
                    // 提取年、月、日、时、分、秒
                   // int year = now.getYear() % 100; // 获取年份并取后两位
                    //int month = now.getMonthValue();
                    //int day = now.getDayOfMonth();
                    //int hour = now.getHour();



                    // System.out.println("时间差" + DateUtil.getBetweenDays(time, new Date()));
//                    if (DateUtil.getBetweenDays( new Date(),time) > 0|| preCheckData.getWeight()==0)
//                        continue;



                    if (preCheckData.getWeight()==0)
                        continue;

                    // AwsPreCheckDataHistory preHis = new AwsPreCheckDataHistory();
//                    System.out.println("预见复制1" + preCheckData.getPassTime());
//                    BeanUtils.copyProperties(preCheckData, preHis);
//                    System.out.println("预见复制2" + preHis.getPassTime());
//                    System.out.println("预见复制3" + preCheckData.getPassTime());


//                    long time_prase=System.currentTimeMillis();
//                    System.out.println("称台数据解析时间"+time_prase+"  "+time_start+"  "+(time_prase-time_start));

                    //TODO 打印实体类数据(称重信息,重点看流水号是否绑定成功)
//                    System.out.println("=========================================================");
//                    System.out.println("称重台检测内容：");
//                    System.err.println(preCheckData);
                    //String preNo = StartCore.hashMap.get(hashCode);


                    String preNo = StringUtil.genNo();
                    try {
                        String filename = "D:" + File.separator + "weight_data" + File.separator + preNo + ".txt";
                        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
                        writer.write(String.valueOf(hex1));

                        writer.newLine(); // 写入换行
                        // 在这里写入文件
                        writer.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
//                    保存秤台数据临时解析用



                    AwsCarTypeIdRelation relation = carWeightCore.relationMapper.selectOne(new QueryWrapper<AwsCarTypeIdRelation>().lambda().eq(AwsCarTypeIdRelation::getVehType, preCheckData.getCarTypeId()));
                    int carTypeId = 0;
                    if (relation != null) {
                        carTypeId = relation.getCarTypeId();
                        tempWeightData.setCarTypeId(carTypeId);
                    }
                    AwsCarType carType = carWeightCore.carTypeMapper.selectById(carTypeId);
                    if (carType != null) {
                        tempWeightData.setLimitAmt(carType.getLimitAmt());
                        // preHis.setLimitAmt(carType.getLimitAmt());
                    } else {
                        tempWeightData.setLimitAmt(0d);
                        //   preHis.setLimitAmt(0d);
                    }

//                    preCheckData.setCreateTime(new Date());
                    tempWeightData.setCreateTime(preCheckData.getPassTime());

                    tempWeightData.setPreNo(preNo);

                    allWeightData.setCreateTime(allWeightData.getPassTime());
                    allWeightData.setPreNo(preNo);
                    AwsScan as_weight=StartCore.weightScanMaps.get(this.portName);
                    tempWeightData.setOrgCode(as_weight.getOrgCode());



//                    long inset_db=System.currentTimeMillis();

                    //TODO 如果是空的，则插入称重,最大限重，车辆类型等内容插入数据库
//                    if(preCheckData.getWeight()!=0 && preCheckData.getWeight() <10000)
                    carWeightCore.tempWeightDataMapper.insert(tempWeightData);
                    carWeightCore.allWeightDataMapper.insert(allWeightData);
                    // carWeightCore.preCheckDataHistoryMapper.insert(preHis);

//                    long inset_db_over=System.currentTimeMillis();
//                    System.out.println("称台数据入库时间"+(inset_db_over)+"   "+(time_prase)+"  "+(inset_db_over-time_prase));

//                    long inset_db_over=System.currentTimeMillis();
//                    System.out.println("称台数据入库"+inset_db_over);
//                    sqlSession.commit();

//                    AwsPreCheckData p = carWeightCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo, preNo));
//                    System.err.println("p:" + p);
//                    else {
//                        carWeightCore.preCheckDataMapper.update(preCheckData, new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo, preNo));
//                    }
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        startMain();
    }
}