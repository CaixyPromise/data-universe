package com.caixy.backend.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图表状态枚举类
 *
 * @name: com.caixy.backend.model.enums.ChartStatusEnum
 * @author: CAIXYPROMISE
 * @since: 2024-04-21 19:06
 **/

public enum ChartStatusEnum
{
    WAIT("等待中", "wait"),
    RUNNING("运行中", "running"),
    SUCCEED("生成成功", "succeed"),
    FAIL("生成失败", "failed")
    ;

    private final String text;

    private final String value;

    ChartStatusEnum(String text, String value)
    {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues()
    {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ChartStatusEnum getEnumByValue(String value)
    {
        if (ObjectUtils.isEmpty(value))
        {
            return null;
        }
        for (ChartStatusEnum anEnum : ChartStatusEnum.values())
        {
            if (anEnum.value.equals(value))
            {
                return anEnum;
            }
        }
        return null;
    }


    public String getValue()
    {
        return value;
    }

    public String getText()
    {
        return text;
    }

}
