package com.tencent.wxcloudrun.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dao.SlogMapper;
import com.tencent.wxcloudrun.dao.StypeMapper;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.ArtWordService;
import com.tencent.wxcloudrun.utils.Token;
import com.tencent.wxcloudrun.utils.ZhuUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuyuzhuo
 * @date 2023/9/9 10:00
 */
@RestController
@RequestMapping("api/index/")
public class ArtWordController {

    @Resource
    private ArtWordService artWordService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private StypeMapper stypeMapper;
    @Resource
    private SlogMapper slogMapper;
    @Resource
    private HttpServletRequest request;
    @Value("${yi.shu.zi.domain}")
    private String domain;
    

    private String getAppKey(){
        String appKey = request.getHeader("appKey");
        if (appKey == null){
            appKey = "";
        }
        return appKey;
    }

    /**
     * 获取字体选项
     */
    @PostMapping("index")
    public ApiResponse getFontTypeList(){
        List<Config> configs = artWordService.listConfig(new ArrayList<>(Arrays.asList("logo", ZhuUtils.outMsg("tt_jl{}",getAppKey()) )));
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(Config::getName,  Config::getValue, (oldValue, newValue) -> oldValue));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logo",configMap.get("logo"));
        jsonObject.put("tt_jl",configMap.get(ZhuUtils.outMsg("tt_jl{}",getAppKey())));

        List<Stype> stypes = artWordService.listStype();
        jsonObject.put("chinese",stypes);
        jsonObject.put("english",stypes);

        return ApiResponse.ok(jsonObject);
    }

    /**
     * 手机号登录
     * 参见文档：https://developer.open-douyin.com/docs/resource/zh-CN/mini-app/develop/server/log-in/code-2-session
     */
    @PostMapping("mpTtLogin")
    public ApiResponse mobileLogin(@RequestBody LoginVo loginVo){
        if (StringUtil.isEmpty(loginVo.getCode())){
            return ApiResponse.error("code不能为空");
        }
        String tt_appid = ZhuUtils.outMsg("tt_appid{}", getAppKey());
        String tt_secret = ZhuUtils.outMsg("tt_secret{}", getAppKey());
        List<Config> configs = artWordService.listConfig(new ArrayList<>(Arrays.asList(tt_appid, tt_secret,"default_avatar","register_score")));
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(Config::getName,  Config::getValue, (oldValue, newValue) -> oldValue));
        JSONObject param = new JSONObject()
                .fluentPut("appid",configMap.get(tt_appid))
                .fluentPut("secret",configMap.get(tt_secret))
                .fluentPut("code",loginVo.getCode())
                .fluentPut("anonymous_code","");

        HttpResponse response = HttpUtil.createPost("https://developer.toutiao.com/api/apps/v2/jscode2session")
                .body(param.toJSONString())
                .setConnectionTimeout(5 * 1000)
                .setReadTimeout(15 * 1000)
                .execute();
        if (response.isOk()){
            JSONObject result = JSONObject.parseObject(response.body());
            JSONObject data = result.getJSONObject("data");
            String openid = data.getString("openid");
            if (StringUtil.isNotEmpty(openid)){
                //登录抖音平台成功
                //查询系统用户
                User user = userMapper.getUserByOpenId(openid);
                if (user == null){
                    //新增用户
                    //['id', 'username', 'nickname', 'mobile', 'avatar', 'score'];
                    user = new User();
                    user.setNickname(ZhuUtils.nickName());
                    user.setSalt(ZhuUtils.randomSalt());
                    user.setScore(configMap.get("register_score")!=null?Integer.parseInt(configMap.get("register_score")) : 10);
                    long time = System.currentTimeMillis()/1000L;
                    user.setPrevtime(time);
                    user.setLogintime(time);
                    user.setLoginip(request.getRemoteAddr());
                    user.setJoinip(request.getRemoteAddr());
                    user.setJointime(time);
                    user.setCreatetime(time);
                    user.setUpdatetime(time);
                    user.setOpenid(openid);
                    user.setStatus("normal");
                    user.setAvatar("");
                    String tokenStr = ZhuUtils.uuid();
                    user.setToken(tokenStr);
                    Token.set(tokenStr,user,3600*1000L);
                    userMapper.save(user);

                    User userByOpenId = userMapper.getUserByOpenId(openid);
                    user.setId(userByOpenId.getId());
                    return ApiResponse.ok("授权登录成功", user);
                }else {
                    //判断用户状态
                    if (!"normal".equals(user.getStatus())){
                        //登录失败
                        return ApiResponse.error("账户异常~请联系管理员");
                    }else {
                        String tokenStr = ZhuUtils.uuid();
                        Token.set(tokenStr,user,3600*1000L);
                        user.setToken(tokenStr);
                        return ApiResponse.ok("授权登录成功", user);
                    }
                }

            }
        }
        return ApiResponse.error("登录失败");
    }

    /**
     * 生成艺术字图片
     */
    @PostMapping("textToImage")
    public ApiResponse generateWord(@RequestBody TextToImageVo textToImageVo) throws URISyntaxException, IOException {
        Stype stypeById = stypeMapper.getStypeById(textToImageVo.getStype_id());
        if (stypeById == null){
            return ApiResponse.error("字体类型不存在").setCode(100);
        }

        User tokenUser = ZhuUtils.getTokenUser(request);

        //敏感词检测
        String msg = checkSecCheckTk(textToImageVo.getName());
        if (msg != null){
            return ApiResponse.error(msg).setCode(104);
        }

        JSONObject param = new JSONObject();
        param.fluentPut("id",textToImageVo.getName())
                .fluentPut("zhenbi",20191123)
                .fluentPut("id1",stypeById.getSid())
                .fluentPut("id2",colorToSix(textToImageVo.getBg_color()))
                .fluentPut("id3","")
                .fluentPut("id4","#000000")
                .fluentPut("id5","")
                .fluentPut("id6",colorToSix(textToImageVo.getFont_color()));
        HttpResponse response = HttpUtil.createPost("http://www.yishuzi.com/make_ysz.php?file=a")
                .setReadTimeout(15 * 1000)
                .setConnectionTimeout(5 * 1000)
                .form("id",textToImageVo.getName())
                .form("zhenbi",20191123)
                .form("id1",stypeById.getSid())
                .form("id2",colorToSix(textToImageVo.getBg_color()))
                .form("id3","")
                .form("id4","#000000")
                .form("id5","")
                .form("id6",colorToSix(textToImageVo.getFont_color()))
                .execute();
        //<img src="http://76.84c.cn/cdn/136/1267.png?09111053337098">
        String body = response.body();
        String imageUrl = "";
        Slog slog = new Slog();
        slog.setUserId(tokenUser.getId());
        slog.setCreatetime(System.currentTimeMillis()/1000L);
        slog.setFontColor(colorToSix(textToImageVo.getFont_color()));
        slog.setBgColor(colorToSix(textToImageVo.getBg_color()));
        slog.setStypeId(textToImageVo.getStype_id());
        slog.setName(textToImageVo.getName());

        body = body.replace("\uFEFF","");
        if (body.startsWith("<img src")){
            imageUrl = body.substring(body.indexOf("\"")+1,body.lastIndexOf("\""));
            slog.setImage(imageUrl);
            slog.setStatus(1);
        }else {
            slog.setImage(imageUrl);
            slog.setStatus(0);
            slogMapper.save(slog);
            return ApiResponse.error("包含敏感词汇");
        }

        //保存文件到本地目录
        String jarFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String now = DateUtil.now();
        String day = now.split("\\s")[0].replace("-","");
        URL url = new URL(slog.getImage());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30 * 1000);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream inputStream = conn.getInputStream();
        IOUtils.copy(inputStream, output);
        String path = File.separator + day + File.separator + ZhuUtils.uuid() + ".jpg";
        String localPath = "/www/wwwroot/yishu.sougouxm.com/uploads" + path;
        File file = new File(localPath);
        System.out.println("文件存储路径："+file);
        System.out.println(file.getParent());
        System.out.println("开始创建文件夹："+file.getParent());
        boolean mkdirs = file.getParentFile().mkdirs();
        System.out.println("创建文件夹结果："+mkdirs);
        FileUtil.writeBytes(output.toByteArray(),localPath);
        //slog.setImage("http://localhost:9999/uploads" + path);
        //本地路径和第三方网络路径都保存下，后面解决了静态资源代理再说
        slog.setImage(domain+localPath+","+imageUrl);
        slogMapper.save(slog);
        try {
            output.close();
            conn.disconnect();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ApiResponse.ok("生成成功",domain+localPath);
    }

    /**
     * 色值强制#fff转换成#ffffff格式
     * @param bg_color bg_color
     */
    private String colorToSix(String bg_color) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bg_color.length(); i++) {
            if (i > 0){
                sb.append(bg_color.charAt(i)).append(bg_color.charAt(i));
            }else {
                sb.append(bg_color.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 内容安全检测、敏感词检测
     * @param name name
     */
    private String checkSecCheckTk(String name) {
        if (StringUtil.isEmpty(name)){
            return "姓名不能为空";
        }
        String accessTokenTt = getAccessTokenTt();
        JSONObject param = new JSONObject();
        JSONArray array = new JSONArray();
        array.add(new JSONObject().fluentPut("content",name));
        param.put("tasks",array);
        HttpResponse response = HttpUtil.createPost("https://developer.toutiao.com/api/v2/tags/text/antidirt")
                .setConnectionTimeout(5 * 1000)
                .setReadTimeout(15 * 1000)
                .header("X-Token", accessTokenTt)
                .body(param.toJSONString())
                .execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null){
            JSONArray predicts = data.getJSONArray("predicts");
            if (predicts != null){
                JSONObject res = predicts.getJSONObject(0);
                Boolean hit = res.getBoolean("hit");
                if (hit != null && hit){
                    return "内容安全检测未通过";
                }
            }
        }
        return null;
    }

    /**
     * 获取AccessToken
     */
    private String getAccessTokenTt(){
        String tt_appid = ZhuUtils.outMsg("tt_appid{}", getAppKey());
        String tt_secret = ZhuUtils.outMsg("tt_secret{}", getAppKey());
        List<Config> configs = artWordService.listConfig(new ArrayList<>(Arrays.asList(tt_appid, tt_secret)));
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(Config::getName,  Config::getValue, (oldValue, newValue) -> oldValue));
        JSONObject param = new JSONObject()
                .fluentPut("appid",configMap.get(tt_appid))
                .fluentPut("secret",configMap.get(tt_secret))
                .fluentPut("grant_type","client_credential");
        HttpResponse response = HttpUtil.createPost("https://developer.toutiao.com/api/apps/v2/token")
                .setConnectionTimeout(5 * 1000)
                .setReadTimeout(15 * 1000)
                .contentType("application/json").body(param.toJSONString()).execute();
        JSONObject jsonObject = JSONObject.parseObject(response.body());
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null){
            return data.getString("access_token");
        }
        return null;
    }

    /**
     * 判断是否需要看广告
     */
    @PostMapping("judgeNeedAd")
    public ApiResponse judgeAd(@RequestBody User user){
        //SELECT * FROM your_table_name WHERE DATE(FROM_UNIXTIME(timestamp_column)) = CURDATE();
        User tokenUser = ZhuUtils.getTokenUser(request);

        User query = new User();
        query.setId(tokenUser.getId());
        query.setName(user.getName());
        List<Slog> slogList = userMapper.getSlogList(query);
        if (slogList.size() > 0){
            //同一用户，同一名字，不需要再看广告
            return ApiResponse.ok(0);
        }

        //今日5次广告后，即可无限设计
        query = new User();
        query.setId(tokenUser.getId());
        query.setCreatetime(System.currentTimeMillis()/1000);
        slogList = userMapper.getSlogList(query);
        if (slogList.size() >= 5){
            return ApiResponse.ok(0);
        }

        return ApiResponse.ok(1);
    }

    /**
     * 获取生成记录
     */
    @PostMapping("history")
    public ApiResponse historyList(){
        User tokenUser = ZhuUtils.getTokenUser(request);
        User query = new User();
        query.setId(tokenUser.getId());
        List<Slog> slogList = userMapper.getSlogList(query);
        for (Slog slog : slogList) {
            String[] arr = slog.getImage().split(",");
            if (arr.length==2){
                slog.setImage(arr[0]);
            }
        }
        return ApiResponse.ok("ok", slogList);
    }
}
