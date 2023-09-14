package com.tencent.wxcloudrun.utils;

import com.tencent.wxcloudrun.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 18:36
 */
public class ZhuUtils {

    private static final Random random = new Random();
    /**
     * 48-57  65-90  97-122 数字、大写字母、数字
     */
    private static List<Character> charList = new ArrayList<Character>(62);
    static {
        for (int i = 1; i < 122; i++) {
            if ( (i>=48 && i<=57) || (i>=65 && i<=90) || (i>=97 && i<=122)){
                charList.add((char)i);
            }
        }
    }

    /**
     * 获取UUID，去掉-，转为大写
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }

    /**
     * 获取时间字符串
     */
    public static String now(){
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    /**
     * 密码随机盐 格式：HRvej2
     */
    public static String randomSalt(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(charList.get(random.nextInt(charList.size())));
        }
        return sb.toString();
    }

    /**
     * 随机生成昵称
     */
    public static String nickName(){
        return "#" + randomSalt();
    }

    /**
     * 获取tokenUser
     * @param request request
     */
    public static User getTokenUser(HttpServletRequest request){
        return Token.get(request.getHeader("token"));
    }

}
