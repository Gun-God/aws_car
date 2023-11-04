package com.aws.carno.service.impl;

import com.aws.carno.Utils.DateUtil;
import com.aws.carno.Utils.StringUtil;
import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.domain.AwsPreCheckDataHistory;
import com.aws.carno.domain.AwsTempCarnoData;
import com.aws.carno.domain.AwsTempWeightData;
import com.aws.carno.mapper.AwsPreCheckDataHistoryMapper;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.aws.carno.mapper.AwsTempWeightDataMapper;
import com.aws.carno.service.AwsTempCarnoDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AwsTempCarnoDataServiceImpl
        extends ServiceImpl<AwsTempCarnoDataMapper,AwsTempCarnoData>
        implements AwsTempCarnoDataService {

    @Autowired
    AwsTempCarnoDataMapper awsTempCarnoDataMapper;
    @Autowired
    AwsTempWeightDataMapper awsTempWeightDataMapper;
    @Autowired
    AwsPreCheckDataMapper preCheckDataMapper;
    @Autowired
    AwsPreCheckDataHistoryMapper historyMapper;

    @Override
    public void processWeightAndCarno() {
        System.out.println("定时任务匹配");
        List<AwsTempCarnoData> awsTempCarnoDataList = awsTempCarnoDataMapper.selectList(null);
        if (awsTempCarnoDataList.size() > 0) {
            for (AwsTempCarnoData awsTempCarnoData : awsTempCarnoDataList) {
                // 处理重量
                Integer lane=awsTempCarnoData.getLane();
                Date passTime=awsTempCarnoData.getPassTime();
                int timeThreshold = 60; //时间有效阈值为2秒
                // AwsPreCheckData pCData = hikCarNoCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getLane, laneInfo).isNull(AwsPreCheckData::getCarNo).orderByDesc(AwsPreCheckData::getPassTime).last("limit 1"));
                Date date1= DateUtil.reduceDateSecond(passTime,timeThreshold);
                Date date2= DateUtil.addDateSecond(passTime,timeThreshold);
                List<AwsTempWeightData> pCDataList = awsTempWeightDataMapper.selectList(new QueryWrapper< AwsTempWeightData>().lambda().eq( AwsTempWeightData::getLane, lane).between( AwsTempWeightData::getPassTime, date1,date2));
                AwsTempWeightData pCData = new AwsTempWeightData();
                if (pCDataList.size() > 0){
                    long diff=-1;
                    for (AwsTempWeightData data:pCDataList) {
                        long d=Math.abs(data.getPassTime().getTime()-passTime.getTime());
                        if(diff==-1||d<diff){
                            diff=d;
                            BeanUtils.copyProperties(data, pCData);
                        }

                    }

                    System.out.println("匹配成功"+awsTempCarnoData.getCarNo());
                    AwsPreCheckData preCheckData = new AwsPreCheckData();
                    BeanUtils.copyProperties(pCData, preCheckData);
                    preCheckData.setCarNo(awsTempCarnoData.getCarNo());
                    preCheckData.setImg(awsTempCarnoData.getImg());
                    preCheckData.setColor(awsTempCarnoData.getColor());
                    AwsPreCheckDataHistory preCheckDataHistory = new AwsPreCheckDataHistory();
                    BeanUtils.copyProperties(preCheckData, preCheckDataHistory);
                    if(preCheckDataMapper.insert(preCheckData)==1&&historyMapper.insert(preCheckDataHistory)==1){
                        awsTempCarnoDataMapper.deleteById(awsTempCarnoData.getId());
                        awsTempWeightDataMapper.deleteById(pCData.getId());

                    }



                }


            }

        }
    }
}
