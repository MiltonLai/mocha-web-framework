package com.rockbb.mocha.commons;

import com.rockbb.mocha.stereotype.ModelAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Dispatcher implements Filter {
    private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private ObjectPool pool = new ObjectPool();
    private RequestMappingFactory requestMappingFactory;
    private ViewResolverFactory viewResolverFactory;
    private List<String> interceptors;

    public void init(FilterConfig filterConfig) throws ServletException {
        MochaConfig mochaConfig = new MochaConfig(filterConfig.getInitParameter("configLocation"));
        requestMappingFactory = new RequestMappingFactory(pool, mochaConfig.getScanPakcages());
        viewResolverFactory = new ViewResolverFactory(mochaConfig);
        interceptors = mochaConfig.getInterceptorClasses();
        loadInterceptors();
    }

    public void destroy() {
        requestMappingFactory.clear();
        pool.clear();
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String servletPath = request.getServletPath();
        int pos = servletPath.lastIndexOf('.');
        if (pos > 0) {
            servletPath = servletPath.substring(0, pos);
        }
        RequestWorker worker = requestMappingFactory.get(servletPath, request.getMethod());
        if (worker != null) {
            // This context will live within this request
            RequestContext context = new RequestContext(request, response, worker.getResultType());
            // Build the interceptors stack and pre-handle
            Stack<RequestInterceptor> interceptorStack = new Stack<>();
            for (String interceptorKey : interceptors) {
                RequestInterceptor interceptor = (RequestInterceptor)pool.get(interceptorKey);
                if (interceptor.involve(context, worker.getController())) {
                    interceptorStack.push(interceptor);
                    if (!interceptor.preHandle(context, worker.getController())) {
                        logger.error("Interceptor error: {}", interceptorKey);
                        return;
                    }
                }
            }
            // Prepare the parameters
            Object[] args = loadArguments(context, worker.getMethod().getParameters());
            try {
                // Invoke the controller method
                Object result = worker.getMethod().invoke(worker.getController(), args);
                // Interceptor post-handle
                RequestInterceptor interceptor;
                while (!interceptorStack.empty() && (interceptor = interceptorStack.pop()) != null) {
                    interceptor.postHandle(context);
                }
                // Render the result
                viewResolverFactory.render(context.getResultType(), context.getResponseHelper(), result);
            } catch (InvocationTargetException e) {
                logger.error("InvocationTargetException", e);
            } catch (IllegalAccessException e) {
                logger.error("IllegalAccessException", e);
            }

            logger.info("Request: {}/{}:{}->{}.{} {}ms",
                    servletPath,
                    request.getMethod(),
                    worker.getResultType(),
                    worker.getController().getClass().getSimpleName(),
                    worker.getMethod().getName(),
                    System.currentTimeMillis() - context.getTimestamp());
        } else {
            chain.doFilter(req, res);
        }
    }

    private Object[] loadArguments(RequestContext context, Parameter[] parameters) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class clazz = parameters[i].getType();
            if (clazz.equals(RequestHelper.class)) {
                args[i] = context.getRequestHelper();
                continue;
            }
            if (clazz.equals(ResponseHelper.class)) {
                args[i] = context.getResponseHelper();
                continue;
            }
            if (clazz.equals(HttpServletRequest.class)) {
                args[i] = context.getRequestHelper().getRequest();
                continue;
            }
            if (clazz.equals(HttpServletResponse.class)) {
                args[i] = context.getResponseHelper().getResponse();
                continue;
            }
            if (clazz.equals(Pager.class)) {
                args[i] = context.getRequestHelper().getPager();
                continue;
            }

            Annotation annotation = parameters[i].getAnnotation(ModelAttribute.class);
            if (annotation != null) {
                ModelAttribute modelAttribute = (ModelAttribute)annotation;
                Object value = context.getAttribute(modelAttribute.value());
                if (value != null) {
                    args[i] = value;
                    continue;
                }
                args[i] = context.getRequestHelper().get(modelAttribute.value(), clazz);
                continue;
            }

            // Finally, if there is nothing matched, fill it with fallback value.
            if (!clazz.isPrimitive()) {
                try {
                    args[i] = clazz.newInstance();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                if (clazz.equals(int.class)) {
                    args[i] = 0;
                } else if (clazz.equals(long.class)) {
                    args[i] = 0;
                } else if (clazz.equals(char.class)) {
                    args[i] = 0;
                } else if (clazz.equals(boolean.class)) {
                    args[i] = true;
                } else if (clazz.equals(byte.class)) {
                    args[i] = 0;
                } else if (clazz.equals(short.class)) {
                    args[i] = 0;
                } else if (clazz.equals(float.class)) {
                    args[i] = 0f;
                } else if (clazz.equals(double.class)) {
                    args[i] = 0f;
                }
            }
        }
        return args;
    }

    private void loadInterceptors() {
        try {
            for (String interceptorClass : this.interceptors) {
                Object obj = pool.get(interceptorClass);
                if (obj == null) {
                    Class clazz = Thread.currentThread().getContextClassLoader().loadClass(interceptorClass);
                    obj = clazz.newInstance();
                    pool.put(interceptorClass, obj);
                }
            }
        } catch (InstantiationException e) {
            logger.error("InstantiationException:", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException:", e);
        } catch (Exception e) {
            logger.error("Exception:", e);
        }
    }
}
