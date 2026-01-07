package com.example.utils;

import com.example.entity.RestBean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 针对于返回值为String作为错误信息的方法进行统一处理
 */
@Component
public class ControllerUtils {
    public  <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
