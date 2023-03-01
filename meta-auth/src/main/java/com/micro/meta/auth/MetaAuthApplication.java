package com.micro.meta.auth;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author loukaikai
 * @version 1.0.0
 * @ClassName MetaAuthApplication.java
 * @Description TODO
 * @createTime 2023年02月23日 22:56:00
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.micro.meta")
public class MetaAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetaAuthApplication.class, args);
    }
}
