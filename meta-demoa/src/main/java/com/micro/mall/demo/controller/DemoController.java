package com.micro.mall.demo.controller;

import com.micro.mall.demo.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 商品管理示例Controller
 */
@Api(tags = "DemoController", description = "商品管理示例接口")
@Controller
@RefreshScope
@RequestMapping("/demo")
public class DemoController {

    @Value("${user.name}")
    private String name;

    @Resource
    private DemoService demoService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @ApiOperation(value = "获取全部品牌列表")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String getBrandList() {
        return "服务调用成功"+name;

    }

}
