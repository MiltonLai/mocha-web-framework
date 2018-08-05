package com.rockbb.mocha.commons;

import java.lang.reflect.Method;

public class RequestWorker {
    private Object controller;
    private Method method;
    private ResultType resultType;

    RequestWorker(Object controller, Method method, ResultType resultType) {
        this.controller = controller;
        this.method = method;
        this.resultType = resultType;
    }

    public Object getController() {return controller;}
    public Method getMethod() {return method;}
    public ResultType getResultType() {return resultType;}
}
