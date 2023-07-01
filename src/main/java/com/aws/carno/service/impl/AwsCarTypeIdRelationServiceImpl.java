package com.aws.carno.service.impl;


import com.aws.carno.domain.AwsCarTypeIdRelation;
import com.aws.carno.mapper.AwsCarTypeIdRelationMapper;
import com.aws.carno.service.AwsCarTypeIdRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 车型id对应表 服务实现类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Service
public class AwsCarTypeIdRelationServiceImpl extends ServiceImpl<AwsCarTypeIdRelationMapper, AwsCarTypeIdRelation> implements AwsCarTypeIdRelationService {

    @Autowired
    AwsCarTypeIdRelationMapper relationMapper;


}
