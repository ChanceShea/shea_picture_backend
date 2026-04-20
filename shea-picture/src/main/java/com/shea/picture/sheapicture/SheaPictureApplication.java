package com.shea.picture.sheapicture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.shea.picture.sheapicture.mapper")
@EnableAspectJAutoProxy(exposeProxy = true) // 启用AspectJ自动代理
@EnableAsync // 启用异步方法
public class SheaPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SheaPictureApplication.class, args);
    }

}
