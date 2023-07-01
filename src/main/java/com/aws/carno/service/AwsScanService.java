package com.aws.carno.service;

import com.aws.carno.domain.AwsScan;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 设备信息表 服务类
 * </p>
 *
 * @author HuangYW
 * @since 2023-05-15
 */
public interface AwsScanService extends IService<AwsScan> {

    AwsScan getScanInfo(String code);


}
