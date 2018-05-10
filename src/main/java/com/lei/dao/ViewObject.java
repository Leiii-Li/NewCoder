package com.lei.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 2017/5/20.
 */
public class ViewObject {
    private Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }
}
