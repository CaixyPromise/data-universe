package com.caixy.backend.constant;

import lombok.Getter;

/**
 * @name: com.caixy.backend.constant.RedisConstant
 * @description: Redis常量枚举类
 * @author: CAIXYPROMISE
 * @date: 2024-04-02 12:15
 **/
@Getter
public enum RedisConstant
{

    ;

    private final String key;
    private final Long expire;

    RedisConstant(String key, Long expire)
    {
        this.key = key;
        this.expire = expire;
    }
}
