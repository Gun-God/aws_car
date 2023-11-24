package com.aws.carno.service.impl;

import com.aws.carno.domain.AwsAllWeightData;
import com.aws.carno.mapper.AwsAllWeightDataMapper;
import com.aws.carno.service.AwsAllWeightDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsAllWeightDataServiceImpl extends ServiceImpl<AwsAllWeightDataMapper, AwsAllWeightData> implements AwsAllWeightDataService {

    @Autowired
    AwsAllWeightDataMapper  awsAllWeightDataMapper;

}
