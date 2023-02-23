package com.micro.mall.demo.controller;

import com.micro.mall.demo.client.demoClient;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商品管理示例Controller
 */
@RequestMapping("/demob")
@Controller
public class DemoController {
    @Autowired
    private demoClient demoClient;

    @ApiOperation(value = "获取全部品牌列表")
    @RequestMapping(value = "/testFegin", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return demoClient.getBrandList();

    }

}
