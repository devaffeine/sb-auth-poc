package com.devaffeine.whatsup.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryDB {
    private Map<String, Object> data = new ConcurrentHashMap<>();

    public Object get(String key) {
        return data.get(key);
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public Object remove(String key) {
        return data.remove(key);
    }
}
