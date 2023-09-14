package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.ConfigMapper;
import com.tencent.wxcloudrun.dao.StypeMapper;
import com.tencent.wxcloudrun.model.Config;
import com.tencent.wxcloudrun.model.Stype;
import com.tencent.wxcloudrun.service.ArtWordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/9/11 15:33
 */
@Service
public class ArtWordServiceImpl implements ArtWordService {
    @Resource
    private ConfigMapper configMapper;

    @Resource
    private StypeMapper stypeMapper;

    @Override
    public List<Config> listConfig(List<String> nameList) {
        return configMapper.listConfig(nameList);
    }

    @Override
    public List<Stype> listStype() {
        return stypeMapper.listStype();
    }
}
