package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.service.AiService;
import com.example.utils.AiServiceUtils;
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

    @Resource
    AiServiceUtils aiServiceUtils;

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
            return false;
        }
        String result = response.getResult().getOutput().getText();
        return result.contains("是");
    }

    /**
     * 使用自训练的LSTM模型进行情感分析
     * 不再使用Spring AI，而是调用Python Flask服务的LSTM模型
     */
    @Override
    public Integer analyzeSentiment(String text) {
        try {
            // 调用自训练的LSTM模型服务
            JSONObject moderationResult = AiServiceUtils.moderateContent(text);

            if (moderationResult == null || !moderationResult.getBoolean("success")) {
                return 0;
            }

            JSONObject data = moderationResult.getJSONObject("data");
            if (data == null) {
                return 0;
            }

            // 获取情感标签
            String sentimentLabel = data.getString("sentiment");
            if (sentimentLabel != null) {
                switch (sentimentLabel) {
                    case "正向":
                    case "positive":
                        return 1;
                    case "负向":
                    case "negative":
                        return -1;
                    case "中性":
                    case "neutral":
                    default:
                        return 0;
                }
            }

            // 如果没有情感标签，根据置信度判断
            Double confidence = data.getDouble("confidence");
            if (confidence != null) {
                if (confidence > 0.7) {
                    return 1;
                } else if (confidence < 0.3) {
                    return -1;
                }
            }

            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
