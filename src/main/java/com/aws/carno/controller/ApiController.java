package com.aws.carno.controller;

import com.aws.carno.service.AwsTempCarnoDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/11/03 15:08
 * @description
 */
@RestController
public class ApiController {

    @Autowired
    AwsTempCarnoDataService awsTempCarnoDataService;

    @Scheduled(cron ="0/1 * * * * ? ")
    public void PreCheckDataScheduledInto(){
        awsTempCarnoDataService.processWeightAndCarno();
    }
}
