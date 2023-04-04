package com.micro.meta.domain;

import lombok.Data;

/**
 * @author loukaikai
 * @version 1.0.0
 */
@Data
public class ChatPrompt {
    String id;

    /**
     * 模板名称
     **/
    String promptName;

    /**
     * message
     * [
     * {"role": "system", "content": "You are a helpful assistant."},
     * {"role": "user", "content": "Who won the world series in 2020?"},
     * {"role": "assistant", "content": "The Los Angeles Dodgers won the World Series in 2020."},
     * {"role": "user", "content": "Where was it played?"}
     * ]
     * 可以保存为json字符串
     **/
    String message;


    /**********************以下字段皆为预留，
     * model 默认gpt-3.5-turbo
     * temperature
     * **********************/
    String modelId;
}
