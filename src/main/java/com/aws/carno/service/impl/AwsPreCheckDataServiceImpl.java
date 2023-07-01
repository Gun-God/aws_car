package com.aws.carno.service.impl;

import com.aws.carno.domain.AwsPreCheckData;
import com.aws.carno.mapper.AwsPreCheckDataMapper;
import com.aws.carno.service.AwsPreCheckDataService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 预检信息记录表 服务实现类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 * @version 1.0
 * @description  预检信息记录表 服务实现类
 */
@Service
public class AwsPreCheckDataServiceImpl extends ServiceImpl<AwsPreCheckDataMapper, AwsPreCheckData> implements AwsPreCheckDataService {

    @Autowired
    AwsPreCheckDataMapper awsPreCheckDataMapper;


}
