package com.tencent.wxcloudrun.utils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.model.UserMessage;
import com.tencent.wxcloudrun.service.impl.UserServiceImpl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SendWeChatMessage {
    public static void main(String[] args) throws Exception {
        Random random = new Random();


    }

    public static UserMessage randomUserMessage(){
        String[] provinceArr = new String[]{"湘","冀","黑","云","粤","鄂","赣","苏","浙","藏","吉","桂","青","新"};
        String carNumSuffix = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        char[] carNumSuffixArr = carNumSuffix.toCharArray();
        String[] phoneNumPrefixArr = new String[]{"130","131","133","137","135","186","139","138","158"};
        char[] phoneNumSuffixArr = "0123456789".toCharArray();

        Random random = new Random();
        UserMessage userMessage = new UserMessage();
        userMessage.setCarNum(provinceArr[random.nextInt(provinceArr.length)] + carNumSuffixArr[random.nextInt(22)] + getRandomStr(carNumSuffixArr,5,random));
        userMessage.setPhoneNum(phoneNumPrefixArr[random.nextInt(phoneNumPrefixArr.length)] + getRandomStr(phoneNumSuffixArr, 8 , random));
        return userMessage;
    }
    private static String getRandomStr(char[] charArr, int totalLength, Random random){
        StringBuffer sb = new StringBuffer();
        while (true){
            sb.append(charArr[random.nextInt(charArr.length)]);
            if (sb.length() >= totalLength){
                return sb.toString();
            }
        }
    }
}