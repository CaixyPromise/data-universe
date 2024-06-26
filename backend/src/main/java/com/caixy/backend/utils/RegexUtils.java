package com.caixy.backend.utils;

import com.caixy.backend.constant.RegexPatternConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则操作类
 *
 * @name: com.caixy.backend.utils.RegexUtils
 * @author: CAIXYPROMISE
 * @since: 2024-04-02 12:28
 **/
public class RegexUtils
{
    /**
     * 校验手机号格式
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:56
     */
    public static boolean isMobilePhone(String input)
    {
        return match(RegexPatternConstants.PHONE_REGEX, input);
    }

    /**
     * 校验邮箱格式
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:55
     */
    public static boolean isEmail(String input)
    {
        return match(RegexPatternConstants.EMAIL_REGEX, input);
    }

    /**
     * 校验密码合法性
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 20:58
     */
    public static boolean validatePassword(String input)
    {
        return match(RegexPatternConstants.PASSWORD_REGEX, input);
    }

    /**
     * 清洗图像结果返回结果
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/4/25 下午1:57
     */
    public static String matchChartResult(String input)
    {
        return Pattern.compile(RegexPatternConstants.CHART_RESULT_REGEX).matcher(input).replaceAll("");
    }

    public static String extraJson(String jsonString)
    {
        Matcher matcher = RegexPatternConstants.EXTRA_JSON_PATTERN.matcher(jsonString);
        if (matcher.find())
        {
            return matcher.group(0);
        }
        else
        {
            return "{}";
        }
    }


    private static boolean match(String regex, String input)
    {
        return input.matches(regex);
    }


}
