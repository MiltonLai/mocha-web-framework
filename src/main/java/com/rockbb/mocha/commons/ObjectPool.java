package com.rockbb.mocha.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ObjectPool {
    private static Logger logger = LoggerFactory.getLogger(ObjectPool.class);

    private Map<String, Object> objects = new HashMap<>();

    public void clear() {
        objects.clear();
    }

    public Object get(String key) {
        return objects.get(key);
    }

    public void put(String key, Object object) {
        objects.put(key, object);
    }

    public boolean containsKey(String key) {
        return objects.containsKey(key);
    }

    public Object remove(String key) {
        return objects.remove(key);
    }

    /**
     * @return false if the object canonical name is invalid
     */
    public boolean put(Object object) {
        String key = object.getClass().getCanonicalName();
        if (key != null && key.length() > 0) {
            put(key, object);
            return true;
        } else {
            return false;
        }
    }
}
