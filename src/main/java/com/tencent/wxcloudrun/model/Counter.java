package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Counter implements Serializable {

  private Integer id;

  private Integer count;

  private String createdAt;

  private String updatedAt;
}
