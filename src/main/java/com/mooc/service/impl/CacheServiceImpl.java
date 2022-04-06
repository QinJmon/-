package com.mooc.service.impl;

import com.mooc.cache.JedisUtil;
import com.mooc.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private JedisUtil.Keys jedisKeys;

    public void removeFromCache(String keyPrefix) {
        Set<String>  keySet=jedisKeys.keys(keyPrefix+"*");
        for (String key : keySet) {
            jedisKeys.del(key);
        }
    }
}
