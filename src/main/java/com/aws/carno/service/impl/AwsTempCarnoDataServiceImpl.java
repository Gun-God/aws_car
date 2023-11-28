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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AwsTempCarnoDataServiceImpl
        extends ServiceImpl<AwsTempCarnoDataMapper, AwsTempCarnoData>
        implements AwsTempCarnoDataService {

    @Autowired
    AwsTempCarnoDataMapper awsTempCarnoDataMapper;
    @Autowired
    AwsTempWeightDataMapper awsTempWeightDataMapper;
    @Autowired
    AwsPreCheckDataMapper preCheckDataMapper;
    @Autowired
    AwsPreCheckDataHistoryMapper historyMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processWeightAndCarno() {
        System.out.println("定时任务匹配");
        List<AwsTempCarnoData> awsTempCarnoDataList = awsTempCarnoDataMapper.selectList(new QueryWrapper<AwsTempCarnoData>().lambda().orderByDesc(AwsTempCarnoData::getId));
        ExecutorService outerExecutor  = Executors.newFixedThreadPool(1);
        ExecutorService innerExecutor  = Executors.newFixedThreadPool(2);
        long m1=System.currentTimeMillis();

        final int[] count = {0};
        List<AwsTempWeightData> plist = awsTempWeightDataMapper.selectList(null);
        if (awsTempCarnoDataList.size() > 0) {
            for (AwsTempCarnoData awsTempCarnoData : awsTempCarnoDataList) {
                outerExecutor.execute(() -> {
                    // 处理重量
                    Integer lane = awsTempCarnoData.getLane();
                    Date passTime = awsTempCarnoData.getPassTime();
                    String orgCode=awsTempCarnoData.getOrgCode();
                    int timeThreshold = 15; //时间有效阈值
                    // AwsPreCheckData pCData = hikCarNoCore.preCheckDataMapper.selectOne(new QueryWrapper<AwsPreCheckData>().lambda().eq(AwsPreCheckData::getLane, laneInfo).isNull(AwsPreCheckData::getCarNo).orderByDesc(AwsPreCheckData::getPassTime).last("limit 1"));
                    Date date1 = DateUtil.reduceDateSecond(passTime,timeThreshold);
                    Date date2 = DateUtil.addDateSecond(passTime, timeThreshold);
                    AwsTempWeightData pCData = new AwsTempWeightData();
                    List<AwsTempWeightData> pCDataList = new ArrayList<>();
//                    long l1=passTime.getTime();
//                    long l2=date2.getTime();
                    for (AwsTempWeightData weightData : plist) {
                        // innerExecutor.execute(() -> {
                        //t1
//                        long l3=weightData.getPassTime().getTime();
//                        boolean t1 = (l3-l1)>=0 ? true : false;
////                        System.out.println("时间范围");
////                        System.out.println(passTime);
//                        //t2
//                        boolean t2 = (l2-l3)>=0 ? true : false;
//                        System.out.println(date2);
//                        System.out.println("=================");
                        boolean t1 = weightData.getPassTime().after(date1);
                        //t2
                        boolean t2 = weightData.getPassTime().before(date2);
//                        if (weightData.getLane() != null && weightData.getOrgCode().equals(orgCode) && weightData.getLane().equals(awsTempCarnoData.getLane()) && t1 && t2)
                        if (weightData.getLane() != null && weightData.getOrgCode().equals(orgCode) && weightData.getLane().equals(awsTempCarnoData.getLane()) && t1 && t2)
                            pCDataList.add(weightData);
                        //   });

                    }
                    //  innerExecutor.shutdown();
//                    while (true) {
//                        if (innerExecutor.isTerminated()) {
//                            innerExecutor.shutdownNow();
//                            break;
//                        }
//                    }
                    if (pCDataList.size() > 0) {
                        long diff = -1;
                        for (AwsTempWeightData data : pCDataList) {
                            long d = Math.abs(data.getPassTime().getTime() - passTime.getTime());
//                            long d = data.getPassTime().getTime() - passTime.getTime();
//                            if(d>=0){
                            if (diff == -1 || d < diff) {
                                diff = d;
                                BeanUtils.copyProperties(data, pCData);
                            }
//                            }
                        }

                         //如果未匹配上
                        plist.remove(pCData);
                        count[0]++;
                        System.out.println("称重表剩余数据" + plist.size());
                        System.out.println("匹配成功" + awsTempCarnoData.getCarNo());

                        AwsPreCheckData preCheckData = new AwsPreCheckData();
                        BeanUtils.copyProperties(pCData, preCheckData);
                        preCheckData.setCarNo(awsTempCarnoData.getCarNo());
                        preCheckData.setImg(awsTempCarnoData.getImg());
                        preCheckData.setColor(awsTempCarnoData.getColor());
                        AwsPreCheckDataHistory preCheckDataHistory = new AwsPreCheckDataHistory();
                        BeanUtils.copyProperties(preCheckData, preCheckDataHistory);
                        int p = 0,h=0;
                        try {
                            p=preCheckDataMapper.insert(preCheckData);
                            h=historyMapper.insert(preCheckDataHistory);
                        }catch (Exception e){
                            e.printStackTrace();
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        }
                        if ( p== 1 &&  h== 1)
                        {
                            historyMapper.insertOtherModel(preCheckData);
                            awsTempCarnoDataMapper.deleteById(awsTempCarnoData.getId());
                            awsTempWeightDataMapper.deleteById(pCData.getId());
                        }

                    }


                });
            }
            outerExecutor.shutdown();
            while (true) {
                if (outerExecutor.isTerminated()) {
                    long m2=System.currentTimeMillis();
                    outerExecutor.shutdownNow();
                    System.out.println("匹配完成,匹配条数：" + count[0]);
                    System.out.println("匹配耗时" + (m2-m1));
                    break;
                }
            }

        }
    }

    @Override
    public void deleteOverData() {
        AwsTempCarnoData atcd = awsTempCarnoDataMapper.selectOne(new QueryWrapper<AwsTempCarnoData>().lambda().orderByDesc(AwsTempCarnoData::getId).last("limit 1"));
        Date newTime = atcd.getPassTime();
        String beforeTime = DateUtil.reduceDateMinut(newTime, 30);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date bTime = format.parse(beforeTime);
            awsTempCarnoDataMapper.delete(new QueryWrapper<AwsTempCarnoData>().lambda().lt(AwsTempCarnoData::getPassTime, bTime));
            awsTempWeightDataMapper.delete(new QueryWrapper<AwsTempWeightData>().lambda().lt(AwsTempWeightData::getPassTime, bTime));

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


}
