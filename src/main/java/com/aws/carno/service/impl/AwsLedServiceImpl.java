package com.aws.carno.service.impl;


import com.aws.carno.domain.AwsLed;
import com.aws.carno.mapper.AwsLedMapper;
import com.aws.carno.service.AwsLedService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * <p>
 * led显示记录表 服务实现类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Service
public class AwsLedServiceImpl extends ServiceImpl<AwsLedMapper, AwsLed> implements AwsLedService {

}
