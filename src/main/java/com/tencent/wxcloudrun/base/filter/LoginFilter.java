package com.tencent.wxcloudrun.base.filter;

import cn.hutool.cache.CacheUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSONObject;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.utils.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description : 发版过滤器，拦截请求，拒绝用户，因为正在发版
 * @Author : yuzhuoliu
 * @Date: 2022/9/16 15:26
 */
@Component
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String requestURI = request.getRequestURI();
        System.out.println(requestURI);
        if ((requestURI.startsWith("/www/wwwroot/yishu.sougouxm.com/uploads") || requestURI.startsWith("/uploads")) && requestURI.endsWith(".jpg")){
            ServletOutputStream outputStream = servletResponse.getOutputStream();
            String filePath = requestURI;
            if (requestURI.startsWith("/uploads")){
                filePath = "/data/yishu/public" + filePath;
            }
            outputStream.write(FileUtil.readBytes(filePath));
            outputStream.flush();
            return;
        }
        if (requestURI.startsWith("/api/index/mpTtLogin")
                || requestURI.startsWith("/api/index/index")  ){

            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            if (requestURI.endsWith(".js") || requestURI.endsWith(".css") || requestURI.endsWith(".html") || requestURI.endsWith(".ico") ){
                //静态资源不需要登录
                filterChain.doFilter(servletRequest,servletResponse);
            }else {
                String tokenStr = request.getHeader("token");
                User token = Token.get(tokenStr);
                if (token != null){
                    //缓存续期
                    Token.set(tokenStr,token,3600*1000L);
                    filterChain.doFilter(servletRequest,servletResponse);
                }else {
                    //非法操作，拦截
                    ApiResponse objectResultMessage = ApiResponse.error("请登录后操作").setCode(401);
                    servletResponse.setContentType("application/json;charset=utf-8");
                    servletResponse.getWriter().write(JSONObject.toJSONString(objectResultMessage));
                    ((HttpServletResponse)servletResponse).setStatus(401);
                }
            }
        }

    }

}
