package com.aws.carno.service.impl;

import com.aws.carno.domain.AwsTempCarnoData;
import com.aws.carno.mapper.AwsTempCarnoDataMapper;
import com.aws.carno.service.AwsTempCarnoDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsTempCarnoDataServiceImpl
        extends ServiceImpl<AwsTempCarnoDataMapper,AwsTempCarnoData>
        implements AwsTempCarnoDataService
{

    @Autowired
    AwsTempCarnoDataMapper awsTempCarnoDataMapper;
}
