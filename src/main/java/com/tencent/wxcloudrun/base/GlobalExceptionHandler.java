package com.tencent.wxcloudrun.base;

import com.tencent.wxcloudrun.config.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 15:52
 * 统一异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse error(MethodArgumentNotValidException e) {

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        //多个错误，取第一个
        FieldError error = fieldErrors.get(0);
        String msg = error.getDefaultMessage();
        return ApiResponse.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e){
        logger.error("请求出错:",e);
        String msg;
        if (e instanceof HttpRequestMethodNotSupportedException){
            msg = e.getMessage();
        }else {
            msg = "系统错误";
        }
        return ApiResponse.error(msg);
    }
}
