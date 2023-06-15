package com.aws.carno.config;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
/**
 * @author :hyw
 * @version 1.0
 * @date : 2023/06/12 15:34
 */


@Component
public class StandGuard  implements ApplicationRunner, DisposableBean {
    @Override
    public void run(ApplicationArguments args) {
        System.out.println("你好。");
    }
    @Override
    public void destroy() {
        System.out.println("再见!");
    }
}