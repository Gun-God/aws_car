package com.aws.carno.service.impl;

import com.aws.carno.domain.AwsTempCarnoData;
import com.aws.carno.domain.AwsTempWeightData;
import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.aws.carno.mapper.AwsTempWeightDataMapper;
import com.aws.carno.service.AwsTempCarnoDataService;
import com.aws.carno.service.AwsTempWeightDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsTempWeightDataServiceImpl  extends ServiceImpl<AwsTempWeightDataMapper, AwsTempWeightData>
        implements AwsTempWeightDataService {

    @Autowired
    AwsTempWeightDataMapper awsTempWeightDataMapper;

}
