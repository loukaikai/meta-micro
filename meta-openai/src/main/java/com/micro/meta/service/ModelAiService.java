package com.micro.meta.service;

import com.micro.meta.common.api.ResultObject;
import com.micro.meta.vo.ChatOpenAiVO;
import com.micro.meta.vo.OpenaiVO;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.model.Model;

import java.util.List;

/**
 * @author loukaikai
 * @version 1.0.0
 */
public interface ModelAiService {

    /**
     * 获取模型列表
     *
     * @return 返回模型列表
     */
    ResultObject<List<Model>> listModels();


    /**
     * 根据模版生成文本
     *
     * @param openaiVO openaiVo
     * @return CompletionChoice列表
     */
    ResultObject<List<CompletionChoice>> completionChoice(OpenaiVO openaiVO);

    /**
     * chatGPT对话功能接口
     *
     * @param chatOpenAiVO chatOpenaiVo
     * @return ChatCompletionChoice列表
     */
    ResultObject<List<ChatCompletionChoice>> chatCompletionChoice(ChatOpenAiVO chatOpenAiVO);

}
