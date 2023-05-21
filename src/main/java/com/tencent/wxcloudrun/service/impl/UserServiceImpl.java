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

    @Override
    public int saveAndSendMessage(final UserMessage userMessage) {
        userMessage.setMd5(ZhuUtils.uuid());
        userMessage.setCreateTime(ZhuUtils.now());
        logger.info("接收到消息：" + userMessage.toString());
        int a = userMapper.saveMessage(userMessage);

        //异步通知小程序管理员
        ThreadPoolUtil.execute(() -> {
            logger.info("开始通知管理员" + userMessage.toString());
            sendEnterpriseWxChat(userMessage,WEB_HOOK);
        });

        return a;
    }


    @Override
    public UserMessage test() {
        UserMessage userMessage = SendWeChatMessage.randomUserMessage();
        userMessage.setMd5(ZhuUtils.uuid());
        userMessage.setCreateTime(ZhuUtils.now());
        userMessage.setLongitude(110.509);
        userMessage.setLatitude(28.345);
        userMessage.setOpenId("4ddl93LDJG");
        int a = userMapper.saveMessage(userMessage);
        //异步通知小程序管理员
        ThreadPoolUtil.execute(() -> {
            logger.info("开始通知管理员" + userMessage.toString());
            sendEnterpriseWxChat(userMessage,WEB_HOOK);
        });
        return userMessage;
    }

    @Override
    public void initDataBase() {
        userMapper.createTable_user_message();
        userMapper.createTable_Counters();
    }

    @Override
    public int cleanUserMessage() {
        return userMapper.cleanUserMessage();
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
    public static JSONObject buildMessage(UserMessage userMessage){
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

    public static String buildWxMessage(UserMessage userMessage){
        List<String> msgList = new ArrayList<>();
        msgList.add("收到一条救援请求，请及时处理！");
        msgList.add("电话：" + userMessage.getPhoneNum());
        msgList.add("车牌：" + userMessage.getCarNum());
        return String.join("\n", msgList);
    }

    public void sendEnterpriseWxChat(UserMessage userMessage, String webHook){
        JSONObject json = new JSONObject();
        json.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", UserServiceImpl.buildWxMessage(userMessage));
        json.put("text", text);
        StringEntity se = new StringEntity(json.toJSONString(), "utf-8");
        HttpPost httpPost = new HttpPost(webHook);
        httpPost.setEntity(se);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        //标记信息发送是否成功
        boolean isOk = false;
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "utf-8");
                //{"errcode":0,"errmsg":"ok"}
                logger.info(result);
                if (result.startsWith("{")){
                    JSONObject resultObj = JSONObject.parseObject(result);
                    if (resultObj.getIntValue("errcode") == 0 && "ok".equals(resultObj.getString("errmsg"))){
                        isOk = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isOk){
            //通知成功，标记数据库
            userMessage.setSendStatus(1);
            int a = userMapper.updateSendStatus(userMessage);
        }else {
            //通知失败
        }
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
//https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=d977753d-5805-4125-898a-c813931fcb30
    public static void main(String[] args) {
        String corpid = "wwb043b8425ece9298";
        String secret = "XbTtpnCeaQ5pny1vRMfJruvkE78Qjdqm4Z5tcn7w_4c";
        String agentid = "1000015";
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
        String access_token = getAccessToken(corpid, secret);
        url += access_token;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", "@all");
        jsonObject.put("toparty", "");
        jsonObject.put("totag", "");
        jsonObject.put("msgtype", "text");
        JSONObject content = new JSONObject();
        content.put("content", "这是一条测试消息");
        jsonObject.put("text", content);
        jsonObject.put("agentid", agentid);
        jsonObject.put("safe", 0);

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(jsonObject.toString(), "utf-8");
            params.setContentEncoding("UTF-8");
            params.setContentType("application/json");
            request.setEntity(params);
            httpClient.execute(request);
            System.out.println("消息发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("消息发送失败！");
        }
    }

    public static String getAccessToken(String corpid, String secret) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + secret;
        String access_token = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObject = JSONObject.parseObject(response.toString());
            access_token = jsonObject.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }
}
