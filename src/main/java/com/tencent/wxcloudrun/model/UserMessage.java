package com.tencent.wxcloudrun.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:30
 */
public class UserMessage {
    /**
     * uuid，用于唯一标识一条记录
     */
    private String md5;
    /**
     * 电话号码--必填
     */
    @NotBlank(message = "电话号码不能为空")
    private String phoneNum;
    /**
     * 车牌号--必填
     */
    @NotBlank(message = "车牌号不能为空")
    private String carNum;
    /**
     * 保险公司--非必填
     */
    private String insuranceCompany;
    /**
     * 保险过期时间【yyyy-MM-dd】--非必填
     */
    private String insuranceExpireTime;
    /**
     * 经度--必填
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;
    /**
     * 维度--必填
     */
    @NotNull(message = "维度不能为空")
    private Double latitude;
    /**
     * 用户openid--必填
     */
    @NotBlank(message = "openid不能为空")
    private String openId;
    /**
     * 位置描述字符串--非必填
     */
    private String address;
    /**
     * 创建时间--后台自己赋值
     */
    private String createTime;

    /**
     * 推送状态
     */
    private Integer sendStatus;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceExpireTime() {
        return insuranceExpireTime;
    }

    public void setInsuranceExpireTime(String insuranceExpireTime) {
        this.insuranceExpireTime = insuranceExpireTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "md5='" + md5 + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", carNum='" + carNum + '\'' +
                ", insuranceCompany='" + insuranceCompany + '\'' +
                ", insuranceExpireTime='" + insuranceExpireTime + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", openId='" + openId + '\'' +
                ", address='" + address + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
