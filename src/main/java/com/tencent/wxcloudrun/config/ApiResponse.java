package com.tencent.wxcloudrun.config;

import lombok.Data;

import java.util.HashMap;

@Data
public final class ApiResponse {

  private Integer code;
  private String msg;
  private Object data;

  private ApiResponse(int code, String msg, Object data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }
  
  public static ApiResponse ok() {
    return new ApiResponse(200, "", new HashMap<>());
  }

  public static ApiResponse ok(Object data) {
    return new ApiResponse(200, "", data);
  }

  public static ApiResponse ok(String msg, Object data) {
    return new ApiResponse(200, msg, data);
  }

  public static ApiResponse error(String errorMsg) {
    return new ApiResponse(400, errorMsg, null);
  }

  public Integer getCode() {
    return code;
  }

  public ApiResponse setCode(Integer code) {
    this.code = code;
    return this;
  }
}
