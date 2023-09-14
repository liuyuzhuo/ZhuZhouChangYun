package com.tencent.wxcloudrun.model;

/**
 * @author liuyuzhuo
 * @date 2023/9/12 14:05
 */
public class TextToImageVo {
    private String name;
    private Long stype_id;
    private String font_color;
    private String bg_color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStype_id() {
        return stype_id;
    }

    public void setStype_id(Long stype_id) {
        this.stype_id = stype_id;
    }

    public String getFont_color() {
        return font_color;
    }

    public void setFont_color(String font_color) {
        this.font_color = font_color;
    }

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }
}
