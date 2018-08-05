package com.rockbb.mocha.commons;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {
    private HttpServletResponse response;
    /** For output data models */
    private Map<String, Object> models;

    public ResponseHelper(
            HttpServletResponse response) {
        this.response = response;
        this.models = new HashMap<>();
    }

    public void set(String key, Object value) {models.put(key, value);}
    public Object get(String key) {return models.get(key);}
    public void clear() {models.clear();}

    public HttpServletResponse getResponse() {return response;}
    public Map<String, Object> getModels() {return models;}
}