package com.rockbb.mocha.commons;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private long timestamp = System.currentTimeMillis();
    private RequestHelper requestHelper;
    private ResponseHelper responseHelper;

    /** For innter data models */
    private Map<String, Object> attributes;

    private ResultType resultType;

    public RequestContext(HttpServletRequest request, HttpServletResponse response, ResultType resultType) {
        this.requestHelper = new RequestHelper(request);
        this.responseHelper = new ResponseHelper(response);
        this.attributes = new HashMap<>();
        this.resultType = resultType;
    }

    public void setAttribute(String key, Object value) {attributes.put(key, value);}
    public Object getAttribute(String key) {return attributes.get(key);}
    public void clearAttributes() {attributes.clear();}

    public long getTimestamp() {return timestamp;}
    public RequestHelper getRequestHelper() {return requestHelper;}
    public ResponseHelper getResponseHelper() {return responseHelper;}
    public ResultType getResultType() {return resultType;}
    public void setResultType(ResultType resultType) {this.resultType = resultType;}
}
