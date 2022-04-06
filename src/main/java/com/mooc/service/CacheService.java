package com.mooc.service;

public interface CacheService {
    /*依据key前缀删除匹配该模式下的所有key-value
    * 如：传入shopcatrgory，则以shopcatrgory打头的key_value都会被清空*/
    void removeFromCache(String keyPrefix);
}
