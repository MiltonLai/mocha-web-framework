package com.rockbb.mocha.commons;

import com.rockbb.mocha.stereotype.Controller;
import com.rockbb.mocha.stereotype.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestMappingFactory {
    private static Logger logger = LoggerFactory.getLogger(RequestMappingFactory.class);
    private ObjectPool pool;
    private Map<String, RequestWorker> workerMap = new HashMap<>();

    RequestMappingFactory(ObjectPool pool, Set<String> basePackages) {
        this.pool = pool;

        List<Class<?>> classes = new ArrayList<>();
        for (String basePackage : basePackages) {
            classes.addAll(ObjectUtils.extractClasses(basePackage, true));
        }
        loadRequestMappingMethod(classes);
    }

    void clear() {
        workerMap.clear();
    }

    protected RequestWorker get(String requestPath, String requestMethod) {
        RequestWorker worker;
        if ((worker = workerMap.get(requestPath + "/" + requestMethod)) != null) {
            return worker;
        }
        return workerMap.get(requestPath);
    }

    /**
     * 获取classList下面的RequestMapping方法, 保存在mapp中
     *
     * @param classes 保存加了Controller的类
     */
    private void loadRequestMappingMethod(List<Class<?>> classes) {
        for (Class clazz : classes) {
            Annotation annotation = clazz.getAnnotation(Controller.class);
            String controllerName;
            if (annotation == null) {
                continue;
            } else {
                Controller controller = (Controller)annotation;
                controllerName = (controller.value().length() > 0)? controller.value() : clazz.getCanonicalName();
            }
            String[] bases = {""};
            ResultType baseResultType = ResultType.HTML;
            annotation = clazz.getAnnotation(RequestMapping.class);
            if (annotation != null) {
                RequestMapping requestMapping = (RequestMapping)annotation;
                if (requestMapping.value().length > 0) {
                    bases = requestMapping.value();
                }
                if (requestMapping.result() != ResultType.INHERIT) {
                    baseResultType = requestMapping.result();
                }
            }
            Method[] methods = clazz.getDeclaredMethods();
            try {
                Object controller = pool.get(controllerName);
                if (controller == null) {
                    controller = clazz.newInstance();
                    pool.put(controllerName, controller);
                }
                for (Method controllerMethod : methods) {
                    RequestMapping requestMapping = controllerMethod.getAnnotation(RequestMapping.class);
                    if (requestMapping != null) {
                        fill(bases, baseResultType, requestMapping, controller, controllerMethod);
                    }
                }
            } catch (InstantiationException e) {
                logger.error("InstantiationException:", e);
            } catch (IllegalAccessException e) {
                logger.error("IllegalAccessException:", e);
            }
        }
    }

    private void fill(String[] requestBases, ResultType baseResultType, RequestMapping requestMapping,
                      Object controller, Method controllerMethod) {
        ResultType resultType = (requestMapping.result() == ResultType.INHERIT)?
                baseResultType : requestMapping.result();
        for (String base : requestBases) {
            if (requestMapping.value().length > 0) {
                for (String requestPath : requestMapping.value()) {
                    fill(base + requestPath, requestMapping.method(), resultType, controller, controllerMethod);
                }
            } else {
                fill(base, requestMapping.method(), resultType, controller, controllerMethod);
            }
        }
    }

    private void fill(String requestPath, RequestMethod[] requestMethods, ResultType resultType, Object controller,
                      Method method) {
        if (requestPath == null || requestPath.length() == 0) {
            logger.error("Skipped empty path [{}.{}]", controller.getClass().getCanonicalName(), method.getName());
            return;
        }

        if (requestMethods.length == 0) {
            RequestWorker worker;
            if ((worker = workerMap.get(requestPath)) != null) {
                logger.error("Path already defined: [{}] -> {}.{}",
                        requestPath, controller.getClass().getCanonicalName(), method.getName());
                return;
            }
            worker = new RequestWorker(controller, method, resultType);
            workerMap.put(requestPath, worker);
            logger.info("[{}:{}] -> {}.{}",
                    requestPath, resultType, controller.getClass().getName(), method.getName());
        } else {
            for (RequestMethod requestMethod : requestMethods) {
                String path = requestPath + "/" + requestMethod.name();
                RequestWorker worker;
                if ((worker = workerMap.get(path)) != null) {
                    logger.error("Path already defined: [{}] -> {}.{}",
                            path, controller.getClass().getCanonicalName(), method.getName());
                    continue;
                }
                worker = new RequestWorker(controller, method, resultType);
                workerMap.put(path, worker);
                logger.info("[{}:{}] -> {}.{}", path, resultType, controller.getClass().getName(), method.getName());
            }
        }
    }
}
