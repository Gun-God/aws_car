package com.aws.carno.service.impl;

import com.aws.carno.domain.AwsScan;
import com.aws.carno.mapper.AwsScanMapper;
import com.aws.carno.service.AwsScanService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Service
public class AwsScanServiceImpl extends ServiceImpl<AwsScanMapper, AwsScan> implements AwsScanService {
    @Autowired
    AwsScanMapper scanMapper;

    @Override
    public AwsScan getScanInfo(String code) {
        return null;
    }
}
