package com.aws.carno.service;

import com.aws.carno.domain.AwsScan;
import com.aws.carno.domain.AwsTempCarnoData;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AwsTempCarnoDataService extends IService<AwsTempCarnoData> {

    void processWeightAndCarno();
}
