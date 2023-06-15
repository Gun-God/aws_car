package com.aws.carno.service;

import com.aws.carno.domain.AwsCarNo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 车牌抓拍记录表 服务类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
public interface AwsCarNoService extends IService<AwsCarNo> {
    int insertCarNo(AwsCarNo carNo);

}
