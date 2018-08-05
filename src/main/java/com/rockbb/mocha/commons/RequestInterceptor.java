package com.rockbb.mocha.commons;

public interface RequestInterceptor {
    boolean involve(RequestContext context, Object controller);
    boolean preHandle(RequestContext context, Object controller);
    void postHandle(RequestContext context);
}
