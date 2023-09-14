package com.tencent.wxcloudrun.model;

/**
 * @author liuyuzhuo
 * @date 2023/9/11 17:08
 */
public class User {

    private Long id;
    private String nickname;
    private String salt;
    private int score;
    private long prevtime;
    private long logintime;
    private String loginip;
    private String joinip;
    private long jointime;
    private long createtime;
    private long updatetime;
    private String openid;
    private String status;
    private String token;
    private String avatar;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSalt() {
        return salt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getPrevtime() {
        return prevtime;
    }

    public void setPrevtime(long prevtime) {
        this.prevtime = prevtime;
    }

    public long getLogintime() {
        return logintime;
    }

    public void setLogintime(long logintime) {
        this.logintime = logintime;
    }

    public String getLoginip() {
        return loginip;
    }

    public void setLoginip(String loginip) {
        this.loginip = loginip;
    }

    public String getJoinip() {
        return joinip;
    }

    public void setJoinip(String joinip) {
        this.joinip = joinip;
    }

    public long getJointime() {
        return jointime;
    }

    public void setJointime(long jointime) {
        this.jointime = jointime;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
