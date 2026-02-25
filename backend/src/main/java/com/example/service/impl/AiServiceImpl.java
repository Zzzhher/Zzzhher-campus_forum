package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.service.AiService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Resource
    ChatModel chatModel;

    @Override
    public SseEmitter chatWithAi(JSONArray context) {
        SseEmitter emitter = new SseEmitter(30000L);

        List<Message> list = context.stream().map(item -> {
            JSONObject obj = JSONObject.from(item);
            if ("user".equals(obj.getString("type"))) {
                return (Message) new UserMessage(obj.getString("text"));
            } else {
                return (Message) new UserMessage(obj.getString("text"));
            }
        }).toList();
        Prompt prompt = new Prompt(list.toArray(new Message[0]));
        Flux<ChatResponse> flux = chatModel.stream(prompt);

        flux.subscribe(response -> {
            String text = response.getResult().getOutput().getText();
            try {
                emitter.send(text);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, emitter::completeWithError, emitter::complete);

        return emitter;
    }

    @Override
    public boolean checkFalseAdvertising(String text) {
        String prompt = "请判断以下文本是否包含虚假宣传内容，如'包过'、'高薪'等误导性词汇。如果包含，请返回'是'，否则返回'否'。\n\n" + text;
        Prompt chatPrompt = new Prompt(new UserMessage(prompt));
        ChatResponse response = chatModel.call(chatPrompt);
        if (response.getResult() == null || response.getResult().getOutput() == null) {
            return false; // 默认不认为包含虚假宣传，避免空指针异常
        }
        String result = response.getResult().getOutput().getText();
        return result.contains("是");
    }

    @Override
    public Integer analyzeSentiment(String text) {
        String prompt = "请分析以下文本的情感倾向，正面情感返回1，负面情感返回-1，中性返回0。\n\n" + text;
        Prompt chatPrompt = new Prompt(new UserMessage(prompt));
        ChatResponse response = chatModel.call(chatPrompt);
        if (response.getResult() == null || response.getResult().getOutput() == null) {
            return 0; // 默认返回中性情感，避免空指针异常
        }
        String result = response.getResult().getOutput().getText();
        try {
            return Integer.parseInt(result.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
