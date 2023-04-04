package com.micro.meta.controller;

import com.micro.meta.common.aop.Log;
import com.micro.meta.common.api.ResultObject;
import com.micro.meta.service.ModelAiService;
import com.micro.meta.vo.ChatOpenAiVO;
import com.micro.meta.vo.OpenaiVO;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.model.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author loukaikai
 * @version 1.0.0
 */
@RestController
@RequestMapping("/openai")
@Api(tags = "智能导购模块-接入opnai")
public class OpenAiController {

    @Resource
    private ModelAiService modelAiService;

    /**
     * Given a prompt, the model will return one or more predicted completions,
     * and can also return the probabilities of alternative tokens at each position.
     *
     * @return
     */
    @GetMapping("models")
    @ResponseBody
    @Log
    @ApiOperation("获取模型列表")
    public ResultObject<List<Model>> models() {
        return modelAiService.listModels();
    }


    /**
     * Given a prompt, the model will return one or more predicted completions,
     * and can also return the probabilities of alternative tokens at each position.
     *
     * @return
     */
    @PostMapping("completion")
    @ResponseBody
    @Log
    @ApiOperation("根据模版生成文本")
    public ResultObject<List<CompletionChoice>> completion(@RequestBody @Validated OpenaiVO openaiVO) {
        return modelAiService.completionChoice(openaiVO);
    }

    /**
     * chatGPT
     *
     * @return
     */
    @PostMapping("chat")
    public ResultObject writeCode(@RequestBody @Validated ChatOpenAiVO chatOpenAiVO) {
        return modelAiService.chatCompletionChoice(chatOpenAiVO);
    }
}
