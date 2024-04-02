package com.caixy.backend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密操作类
 *
 * @name: com.caixy.backend.utils.EncryptionUtils
 * @author: CAIXYPROMISE
 * @since: 2024-04-02 12:29
 **/
public class EncryptionUtils
{
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String SALT = "CAIXYPROMISE";

    public static String encodePassword(String rawPassword)
    {
        return passwordEncoder.encode(rawPassword);
    }

    public static boolean matches(String originPassword, String hashPassword)
    {
        return passwordEncoder.matches(originPassword, hashPassword);
    }
}
