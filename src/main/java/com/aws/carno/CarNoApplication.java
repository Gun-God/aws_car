package com.aws.carno;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.aws.carno.mapper")
@EnableScheduling
public class CarNoApplication {

    public static void main(String[] args) {

        SpringApplication.run(CarNoApplication.class, args);

    }

}
