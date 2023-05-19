package com.tencent.wxcloudrun.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.config.WxAppletConfig;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.model.UserMessage;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.ThreadPoolUtil;
import com.tencent.wxcloudrun.utils.ZhuUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    @Resource
    UserMapper userMapper;

    @Override
    public int saveAndSendMessage(final UserMessage userMessage) {
        userMessage.setMd5(ZhuUtils.uuid());
        userMessage.setCreateTime(ZhuUtils.now());
        int a = userMapper.saveMessage(userMessage);

        //异步通知小程序管理员
        ThreadPoolUtil.execute(() -> {
            logger.info("开始通知管理员" + userMessage.toString());
            String accessToken = getAccessToken();
            if (accessToken != null && accessToken.length() > 0){
                send(userMessage, accessToken);
            }
        });

        return a;
    }


    @Override
    public void test() {
        UserMessage userMessage = new UserMessage();
        userMessage.setCarNum("粤MP2443");
        userMessage.setPhoneNum("13121211234");
        //异步通知小程序管理员
        ThreadPoolUtil.execute(() -> {
            logger.info("开始通知管理员" + userMessage.toString());
            String accessToken = getAccessToken();
            logger.info(accessToken);
            send(userMessage,accessToken);
        });
    }

    @Override
    public void initDataBase() {
        userMapper.createTable_user_message();
        userMapper.createTable_Counters();
    }


    /**
     * 车牌号
     * {{car_number3.DATA}}
     * 联系电话
     * {{phone_number2.DATA}}
     * 联系人
     * {{thing1.DATA}}
     * @param userMessage userMessage
     * @return message
     */
    private JSONObject buildMessage(UserMessage userMessage){
        JSONObject messageObj = new JSONObject();

        JSONObject carNumField = new JSONObject();
        carNumField.put("value",userMessage.getCarNum());
        messageObj.put("car_number3",carNumField);

        JSONObject phoneNumField = new JSONObject();
        phoneNumField.put("value", userMessage.getPhoneNum());
        messageObj.put("phone_number2", phoneNumField);

        JSONObject thingField = new JSONObject();
        thingField.put("value", "");
        messageObj.put("thing1",thingField);

        return messageObj;
    }

    private static String getAccessToken(){
        HttpResponse execute = HttpUtil.createGet(ACCESS_TOKEN_URL)
                .form("appid", WxAppletConfig.APPLET_APPID)
                .form("secret",WxAppletConfig.APPLET_SECRET)
                .form("grant_type","client_credential")
                .setConnectionTimeout(5000)
                .setReadTimeout(5000)
                .execute();
        String accessToken = null;
        if (execute.isOk()){
            String body = execute.body();
            JSONObject jsonObject = JSONObject.parseObject(body);
            accessToken = jsonObject.getString("access_token");
        }else {
            logger.info("获取accessToken出错" + execute.body());
        }
        return accessToken;
    }

    private void send(UserMessage userMessage,String accessToken){
        //参数封装
        JSONObject jsonData = new JSONObject();
        //公众号管理员用户的openid
        jsonData.put("touser", WxAppletConfig.ADMIN_USER_OPEN_ID);

        JSONObject jsonObject = new JSONObject();
        //公众号APPID
        jsonObject.put("appid", WxAppletConfig.OFFICIAL_ACCOUNT_APPID);
        //公众号模板ID
        jsonObject.put("template_id", WxAppletConfig.OFFICIAL_ACCOUNT_TEMPLATE_ID);
        //公众号模板消息所要跳转的url，暂时不设置
        //jsonObject.put("url", "https://www.baidu.com");

        /**
         //公众号模板消息所要跳转的小程序，小程序的必须与公众号具有绑定关系
         JSONObject miniprogram = new JSONObject();
         //小程序APPID
         miniprogram.put("appid",WxAppletConfig.APPLET_APPID);
         //跳转到小程序的页面路径
         miniprogram.put("pagepath","pages/main_Page/mainPage");
         jsonObject.put("miniprogram", miniprogram);
         */

        //公众号消息数据封装
        //此处的参数key,需要对照模板中的key来设置
        jsonObject.put("data", buildMessage(userMessage));

        jsonData.put("mp_template_msg", jsonObject);

        HttpResponse execute = HttpUtil.createPost(SEND_MESSAGE_URL + accessToken)
                .setConnectionTimeout(5000)
                .setReadTimeout(5000)
                .header("Content-Type", "application/json;charset=utf-8")
                .body(jsonData.toString())
                .execute();
        String body = execute.body();
        if (execute.isOk()){
            logger.info("通知成功");
            //更新数据库的推送状态
        }else {
            logger.info("通知失败");
        }
        logger.info(body);
    }

}
