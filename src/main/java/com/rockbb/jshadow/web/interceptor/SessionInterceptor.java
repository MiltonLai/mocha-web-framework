package com.rockbb.jshadow.web.interceptor;

import com.rockbb.jshadow.web.base.SessionAware;
import com.rockbb.jshadow.web.base.SessionBean;
import com.rockbb.mocha.commons.RequestContext;
import com.rockbb.mocha.commons.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionInterceptor implements RequestInterceptor {
    private static Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    @Override
    public boolean involve(RequestContext context, Object controller) {
        return SessionAware.class.isInstance(controller);
    }

    @Override
    public boolean preHandle(RequestContext context, Object controller) {
        logger.info("preHandle");
        // Do something to load the SessionBean
        SessionBean sb = new SessionBean();
        context.setAttribute(SessionBean.ATTR_KEY, sb);
        return true;
    }

    @Override
    public void postHandle(RequestContext context) {
        logger.info("postHandle");
    }
}
