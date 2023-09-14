package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Config;
import com.tencent.wxcloudrun.model.Stype;

import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/9/11 15:33
 */
public interface ArtWordService {

    public List<Config> listConfig(List<String> nameList);

    public List<Stype> listStype();
}
