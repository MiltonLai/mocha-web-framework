package com.rockbb.mocha.commons;

import com.rockbb.mocha.result.HtmlViewResolver;
import com.rockbb.mocha.result.JsonViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewResolverFactory {
    private static Logger logger = LoggerFactory.getLogger(ViewResolverFactory.class);
    private HtmlViewResolver htmlViewResolver;
    private JsonViewResolver jsonViewResolver;

    public ViewResolverFactory(MochaConfig mochaConfig) {
        htmlViewResolver = new HtmlViewResolver(mochaConfig.get("templates_path"), mochaConfig.get("encoding"));
        jsonViewResolver = new JsonViewResolver();
    }

    public void render(ResultType resultType, ResponseHelper responseHelper, Object result) {
        switch (resultType) {
            case JSON:
                jsonViewResolver.render(responseHelper.getResponse(), responseHelper.getModels());
                return;
            case HTML:
                String template = (String)result;
                if (template.indexOf("redirect:") == 0) {
                    redirect(responseHelper.getResponse(), template.substring("redirect:".length()));
                    return;
                } else {
                    htmlViewResolver.render(responseHelper.getResponse(), template, responseHelper.getModels());
                    return;
                }
            case RAW:
                raw(responseHelper.getResponse(), (String)result);
                return;
            case STREAM:
                //
                return;
            case REDIRECT:
                redirect(responseHelper.getResponse(), (String)result);
                return;
            case INHERIT:
            case NONE:
            default:
                // Do nothing
                return;
        }
    }

    private static void redirect(HttpServletResponse res, String url) {
        try {
            res.sendRedirect(url);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    private void raw(HttpServletResponse res, String raw) {
        if (raw == null) {
            return;
        }
        try {
            res.getWriter().write(raw);
        } catch (Exception e) {
            logger.error("Exception:", e);
        }
    }
}
