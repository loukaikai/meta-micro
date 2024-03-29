package com.micro.meta.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.meta.common.api.ResultObject;
import com.micro.meta.common.service.RedisService;
import com.micro.meta.contants.OpenAiContants;
import com.micro.meta.service.ModelAiService;
import com.micro.meta.vo.ChatOpenAiVO;
import com.micro.meta.vo.OpenaiVO;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author loukaikai
 * @version 1.0.0
 * @since 2023年03月19日 11:08:00
 */
@Service
public class ModelAiServiceImpl implements ModelAiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAiServiceImpl.class);


    @Value("${openai.apikey}")
    private String apikey;

    @Value("${openai.apikey}")
    private String isproxy;

    @Value("${proxy.host}")
    private String host;

    @Value("${proxy.port}")
    private String port;

    @Resource
    private RedisService redisService;

    /**获取模型列表
     * @return 模型列表
     */
    @Override
    public ResultObject<List<Model>> listModels() {
        LOGGER.info("获取模型列表");
        ResultObject<List<Model>> resultObject = new ResultObject<>();
        OpenAiService openAiService = createOpenAiApi();
        List<Model> list = openAiService.listModels();
        LOGGER.info("获取模型列表完成");
        return resultObject;
    }

    /**
     * 获取预测文本
     *
     * @param openaiVO openVo
     * @return CompletionChoice列表
     */
    @Override
    public ResultObject<List<CompletionChoice>> completionChoice(OpenaiVO openaiVO) {
        LOGGER.info("获取预测文本");
        ResultObject<List<CompletionChoice>> resultObject = new ResultObject<>();
        OpenAiService openAiService = createOpenAiApi();
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(openaiVO.getPrompt())
                .n(5).maxTokens(50)
                .user(openaiVO.getUser())
                .logitBias(new HashMap<>())
                .logprobs(5)
                .build();
        CompletionResult completionResult = openAiService.createCompletion(completionRequest);
        LOGGER.info("返回结果：[{}]", JSONObject.toJSONString(completionResult));
        List<CompletionChoice> choices = completionResult.getChoices();

        resultObject.setData(choices);
        LOGGER.info("获取预测文本完成");
        return resultObject;
    }

    /**
     * chatGPT对话功能接口
     *
     * @param chatOpenAiVO chatOpenAiVo
     * @return ChatCompletionChoice列表
     */
    @Override
    public ResultObject<List<ChatCompletionChoice>> chatCompletionChoice(ChatOpenAiVO chatOpenAiVO) {
        LOGGER.info("chatGPT对话功能接口");
        LOGGER.info("参数模版：[{}]", JSONObject.toJSONString(chatOpenAiVO));
        ResultObject<List<ChatCompletionChoice>> resultObject = new ResultObject<>();

        List<ChatMessage> chatMessages = new ArrayList<>();

        // 获取模板配置，没有默认为gpt-3.5-turbo
        LOGGER.info("获取模板配置，没有默认为gpt-3.5-turbo");

        // 获取用户的聊天信息
        LOGGER.info("获取用户的聊天信息");
        String redisKey = OpenAiContants.CHANT_USER_PROMPT + chatOpenAiVO.getUser();
        String json = redisService.get(redisKey).toString();
        if (!StringUtils.isBlank(json)) {
            chatMessages = JSONArray.parseArray(json, ChatMessage.class);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole(ChatMessageRole.USER.value());
        chatMessage.setContent(chatOpenAiVO.getMessages());
        chatMessages.add(chatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model("gpt-3.5-turbo").messages(chatMessages).n(5).maxTokens(50).logitBias(new HashMap<>()).build();

        OpenAiService openAiService = createOpenAiApi();
        List<ChatCompletionChoice> choices = openAiService.createChatCompletion(chatCompletionRequest).getChoices();
        LOGGER.info("返回结果：[{}],将消息缓存进redis", JSONObject.toJSONString(choices));
        chatMessages.add(choices.get(0).getMessage());
        redisService.set(redisKey, chatMessages, 15 * 60);
        LOGGER.info("chatGPT对话功能接口完成");
        resultObject.setData(choices);
        return resultObject;
    }


    private OpenAiService createOpenAiApi() {
        LOGGER.info("创建OpenAiService");
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        OkHttpClient client = null;
        if ("0".equals(isproxy)) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.valueOf(port)));
            client = OpenAiService.defaultClient(apikey, Duration.ofSeconds(1000)).newBuilder().proxy(proxy).build();
        } else {
            client = OpenAiService.defaultClient(apikey, Duration.ofSeconds(1000));
        }
        Retrofit retrofit = OpenAiService.defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService openAiService = new OpenAiService(api);
        LOGGER.info("创建OpenAiService完成");
        return openAiService;
    }
}

