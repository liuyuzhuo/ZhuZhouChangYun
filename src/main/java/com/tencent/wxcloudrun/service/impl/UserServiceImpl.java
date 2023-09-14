package com.tencent.wxcloudrun.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.config.WxAppletConfig;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.model.UserMessage;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.SendWeChatMessage;
import com.tencent.wxcloudrun.utils.ThreadPoolUtil;
import com.tencent.wxcloudrun.utils.ZhuUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:42
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 获取 ACCESS_TOKEN 接口地址，GET请求
     */
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    /**
     * 小程序统一消息推送接口地址，POST请求，ACCESS_TOKEN 通过URL传参
     */
    private static final String SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=";

    /**
     * 企业微信机器人群发消息地址
     */
    private static final String WEB_HOOK = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=d977753d-5805-4125-898a-c813931fcb30";

    @Resource
    UserMapper userMapper;


}
