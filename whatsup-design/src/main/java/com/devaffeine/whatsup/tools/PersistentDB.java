package com.devaffeine.whatsup.tools;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

//key-value
//SQL NoSQL tables 0 1 2 3 4

public class PersistentDB {
    public Map<String, NavigableMap<Object, Object>> tables = new ConcurrentHashMap<>();

    private NavigableMap<Object, Object> getTable(String table) {
        return tables.computeIfAbsent(table, k -> new ConcurrentSkipListMap<>());
    }

    public void put(String table, Object key, Object value) {
        getTable(table).put(key, value);
    }

    public void remove(String table, Object key) {
        getTable(table).remove(key);
    }

    public NavigableMap<Object, Object> query(String table, Object from, Object to) {
        return getTable(table).subMap(from, true, to, true);
    }

    public Object get(String table, Object key) {
        return getTable(table).get(key);
    }
}
