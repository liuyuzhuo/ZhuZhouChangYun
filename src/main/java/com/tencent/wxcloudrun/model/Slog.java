package com.tencent.wxcloudrun.model;

/**
 * @author liuyuzhuo
 * @date 2023/9/12 11:41
 */
public class Slog {
    private Long id;
    private Long userId;
    private String name;
    private String image;
    private Long stypeId;
    private String bgColor;
    private String fontColor;
    private long status;
    private Long createtime;

    public Long getStypeId() {
        return stypeId;
    }

    public void setStypeId(Long stypeId) {
        this.stypeId = stypeId;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
