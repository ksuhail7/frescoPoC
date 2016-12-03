package com.suhailkandanur.fresco.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by suhail on 2016-12-02.
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T convertStrToJson(String jsonStr, Class<T> returnType) throws IOException {
        return objectMapper.readValue(jsonStr, returnType);
    }

    public static <T> String convertObjectToJsonStr(T object) throws IOException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
