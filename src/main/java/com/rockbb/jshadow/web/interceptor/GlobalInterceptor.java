package com.rockbb.jshadow.web.interceptor;

import com.rockbb.mocha.commons.RequestContext;
import com.rockbb.mocha.commons.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalInterceptor implements RequestInterceptor {
    private static Logger logger = LoggerFactory.getLogger(GlobalInterceptor.class);

    @Override
    public boolean involve(RequestContext context, Object controller) {
        return true;
    }

    @Override
    public boolean preHandle(RequestContext context, Object controller) {
        logger.info("preHandle");
        return true;
    }

    @Override
    public void postHandle(RequestContext context) {
        logger.info("postHandle");
    }
}
