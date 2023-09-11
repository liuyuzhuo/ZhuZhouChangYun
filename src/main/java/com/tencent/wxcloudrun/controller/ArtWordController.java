package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuyuzhuo
 * @date 2023/9/9 10:00
 */
@RestController
@RequestMapping("api/index/")
public class ArtWordController {

    /**
     * 获取字体选项
     */
    @PostMapping("index")
    public ApiResponse getFontTypeList(){
        return null;
    }

    /**
     * 手机号登录
     */
    @PostMapping("mpTtLogin")
    public ApiResponse mobileLogin(){
        return null;
    }

    /**
     * 生成艺术字图片
     */
    @PostMapping("textToImage")
    public ApiResponse generateWord(){
        return null;
    }

    /**
     * 判断是否需要看广告
     */
    @PostMapping("judgeNeedAd")
    public ApiResponse judgeAd(){
        return null;
    }

    /**
     * 获取生成记录
     */
    @PostMapping("history")
    public ApiResponse historyList(){
        return null;
    }
}
