package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.UserMessage;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:29
 */
@RestController
public class UserController {

    @Resource
    UserService userService;
    @Resource
    HttpServletRequest request;

    @Value("${clean.pwd:clean.zzcy.321123}")
    private String cleanPwd;

    @PostMapping("/user/saveMessage")
    public ApiResponse saveMessage(@RequestBody @Validated UserMessage userMessage){
        int a = userService.saveAndSendMessage(userMessage);
        if (a > 0){
            return ApiResponse.ok("保存成功");
        }else {
            return ApiResponse.error("保存失败");
        }
    }

    @GetMapping("/user/test")
    public ApiResponse test(){
        return ApiResponse.ok(UserServiceImpl.buildWxMessage(userService.test()));
    }

    @GetMapping("/user/clean")
    public ApiResponse cleanUserMessage(){
        String pwd = request.getParameter("pwd");
        if (cleanPwd.equals(pwd)){
            int a = userService.cleanUserMessage();
            return ApiResponse.ok("clean success：" + a);
        }else {
            return ApiResponse.error("没有权限");
        }
    }
}
