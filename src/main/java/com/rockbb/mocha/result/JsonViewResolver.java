package com.rockbb.mocha.result;

import com.rockbb.mocha.commons.JacksonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonViewResolver {
    private static Logger logger = LoggerFactory.getLogger(JsonViewResolver.class);

    public void render(HttpServletResponse res, Object data) {
        res.setContentType("application/json; charset=utf-8");
        try {
            JacksonMapper.writeObject(res.getWriter(), data);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }
}
