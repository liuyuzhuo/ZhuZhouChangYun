package com.tencent.wxcloudrun.utils;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.tencent.wxcloudrun.model.User;

/**
 * @author liuyuzhuo
 * @date 2023/9/11 19:20
 */
public class Token {
    /**
     * 缓存大小1000
     * 过期时间3600秒
     */
    public static final Cache<String, User> tokenCache = CacheUtil.newFIFOCache(1000,3600*1000L);

    public static void set(String key,User user,long timeOut){
        tokenCache.put(key,user,timeOut);
    }



    public static User get(String key){
        return tokenCache.get(key);
    }

    public static String getToken(String key){
        User user = tokenCache.get(key);
        if (user != null){
            return user.getToken();
        }
        return null;
    }
}
