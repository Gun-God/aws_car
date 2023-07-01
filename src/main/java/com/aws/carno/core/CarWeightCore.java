package com.aws.carno.core;


import com.aws.carno.Utils.RTXDataParse;
import com.aws.carno.Utils.RtxCommUtil;
import com.aws.carno.Utils.RxtxBuilder;
import com.aws.carno.Utils.StringUtil;
import com.aws.carno.domain.AwsCarTypeIdRelation;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.mapper.AwsCarTypeIdRelationMapper;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author :hyw
 * @version 1.0
 * @description 称台核心类
 * @date : 2023/06/20 11:21
 */

public class CarWeightCore {

    @Autowired
    AwsCarTypeIdRelationMapper relationMapper;
    @Autowired
    AwsPreCheckDataMapper preCheckDataMapper;

    public void startMain(String name, int bits, String code, int factory) {
        //开启串口
        RtxCommUtil commUtil = RxtxBuilder.init(name, bits, 1);
        assert commUtil != null;
        // TODO Auto-generated method stub
        try {
            System.out.println("--------------任务处理线程运行了--------------");
            //noinspection InfiniteLoopStatement
            while (true) {
                // 如果堵塞队列中存在数据就将其输出
                if (commUtil.msgQueue.size() > 0) {
                    AwsPreCheckData preCheckData = RTXDataParse.byteArrayToObjData(commUtil.msgQueue.take());
                    //生成唯一流水号
                    String preNo = StringUtil.genNo();
                    preCheckData.setPreNo(preNo);
                    if (factory == 1) {
                        UnvCarNoCore unv = StartCore.UnvMaps.get(code);
                        //接收到称台数据 调用宇视摄像头异步抓拍;
                        unv.CaptureSyncAction(preNo);
                    } else if (factory == 2) {
                        HikCarNoCore hik = StartCore.HikMaps.get(code);
                        //接收到称台数据 调用海康摄像头异步抓拍;
                        hik.startListen(preNo);
                    }
                    AwsCarTypeIdRelation relation = relationMapper.selectOne(new QueryWrapper<AwsCarTypeIdRelation>().lambda().eq(AwsCarTypeIdRelation::getVehType, preCheckData.getCarTypeId()));
                    int carTypeId = 0;
                    if (relation != null) {
                        carTypeId = relation.getCarTypeId();
                    }
                    preCheckData.setCarTypeId(carTypeId);
                    AwsPreCheckData p=preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo,preNo));
                    if (p==null)
                        preCheckDataMapper.insert(preCheckData);
                    else {
                        preCheckDataMapper.update(preCheckData,new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getPreNo,preNo));
                    }
                    System.err.println("车道:" + preCheckData.getLane());
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}