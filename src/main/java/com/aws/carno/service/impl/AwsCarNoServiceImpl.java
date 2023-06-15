package com.aws.carno.service.impl;


import com.aws.carno.domain.AwsCarNo;
import com.aws.carno.mapper.AwsCarNoMapper;
import com.aws.carno.service.AwsCarNoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 车牌抓拍记录表 服务实现类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
@Service
public class AwsCarNoServiceImpl extends ServiceImpl<AwsCarNoMapper, AwsCarNo> implements AwsCarNoService {

    @Autowired
    AwsCarNoMapper carNoMapper;

    @Override
    public int insertCarNo(AwsCarNo carNo) {
        if (carNo!=null)
            carNoMapper.insert(carNo);

        return 0;
    }
}
