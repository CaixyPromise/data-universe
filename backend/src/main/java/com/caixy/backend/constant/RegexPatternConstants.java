package com.caixy.backend.constant;

/**
 * 正则匹配通配符
 *
 * @name: com.caixy.backend.constant.RegexPatternConstants
 * @author: CAIXYPROMISE
 * @since: 2024-04-02 12:28
 **/
public interface RegexPatternConstants
{
    String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    String PHONE_REGEX = "^1[3-9]\\d{9}$";

    String PASSWORD_REGEX = "^\\w{4,32}$";
}
