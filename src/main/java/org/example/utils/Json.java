package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.User;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Json {
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }
    public static <T> T fromJson(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public static HashMap<String, User> fromJsonMap (String json) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, User>>(){}.getType();
        return gson.fromJson(json, mapType);
    }
}
