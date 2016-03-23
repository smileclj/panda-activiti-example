package com.panda.activiti.util;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private static final Logger       logger       = LoggerFactory.getLogger(JsonUtil.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error("将对象:{}序列化为json字符串出错:{}", o, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        try {
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            logger.error("将字符串:{}反序列化为对象出错:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        try {
            return objectMapper.readValue(text, new TypeReference<List<T>>() {
            });
        } catch (IOException e) {
            logger.error("将字符串:{}反序列化为list出错:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static JSONObject parseObject(String text) {
        try {
            return JSON.parseObject(text);
        } catch (JSONException e) {
            logger.error("将字符串:{}反序列化为JSONObject出错:{}", text, e);
            throw new RuntimeException(e);
        }
    }

    public static JSONArray parseArray(String text) {
        try {
            return JSON.parseArray(text);
        } catch (JSONException e) {
            logger.error("将字符串:{}反序列化为JSONArray出错:{}", text, e);
            throw new RuntimeException(e);
        }
        
    }
}
