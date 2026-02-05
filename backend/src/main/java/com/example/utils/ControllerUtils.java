package com.example.utils;

import com.example.entity.RestBean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 针对于返回值为String作为错误信息的方法进行统一处理
 */
@Component
public class ControllerUtils {
    public <T> RestBean<T> messageHandle(Supplier<String> action) {
        String message = action.get();
        if (message == null)
            return RestBean.success();
        else if (message.contains("等待审核"))
            // 等待审核的消息视为成功，将消息作为data返回
            return RestBean.success((T) message);
        else
            return RestBean.failure(400, message);
    }
}
