package com.caixy.backend.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Json操作类
 *
 * @name: com.caixy.backend.utils.JsonUtils
 * @author: CAIXYPROMISE
 * @since: 2024-04-02 12:17
 **/
public class JsonUtils
{
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls().create();

    public static HashMap<String, Object> jsonToMap(String json)
    {
        Type mapType = new TypeToken<HashMap<String, String>>()
        {
        }.getType();
        return gson.fromJson(json, mapType);
    }

    /**
     * map转json
     *
     * @author CAIXYPROMISE
     * @version a
     * @since 2024/16 15:52
     */
    public static String mapToString(Map<?, ?> map)
    {
        return gson.toJson(map);
    }

    public static String toJsonString(Object object)
    {
        return gson.toJson(object);
    }

    /**
     * 将 JSON 字符串转换为对象列表
     *
     * @param json    JSON 字符串
     * @param <T>     对象类型
     * @return 对象列表
     */
    public static <T> List<T> jsonToList(String json)
    {
        return jsonToObject(json, new TypeToken<List<T>>(){}.getType());
    }



    public static <T> T jsonToObject(String json, Class<T> targetType)
    {
        return gson.fromJson(json, targetType);
    }

    public static <T> T jsonToObject(String json, Type typeOfT)
    {
        return gson.fromJson(json, typeOfT);
    }

    public static String fixedJson(String incorrectJson)
    {
        JsonObject jsonObject = null;
        try {
            jsonObject = gson.fromJson(incorrectJson, JsonObject.class);
        } catch (JsonSyntaxException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return gson.toJson(jsonObject);
    }

}
