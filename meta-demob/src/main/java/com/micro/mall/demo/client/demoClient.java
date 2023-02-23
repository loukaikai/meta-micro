package com.micro.mall.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "demoa")
@Component
public interface demoClient {
    @GetMapping("demo/test")
    String getBrandList();
}
