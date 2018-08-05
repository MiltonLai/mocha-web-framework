package com.rockbb.mocha.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class StreamResolver {
    private static Logger logger = LoggerFactory.getLogger(StreamResolver.class);

    public static void process(
            HttpServletResponse res,
            String type,
            String disposition,
            String filename,
            byte[] streamBytes) {
        try {
            res.setHeader("Cache-Control", "no-store");
            res.setHeader("Pragma", "no-cache");
            if (disposition != null && disposition.length() > 0)
                res.setHeader("Content-Disposition", disposition + "; filename=\"" + filename + "\"");
            res.setDateHeader("Expires", 0);
            res.setContentType(type);
            ServletOutputStream responseOutputStream = res.getOutputStream();
            responseOutputStream.write(streamBytes);
            responseOutputStream.flush();
            responseOutputStream.close();
        } catch (Exception e) {
            logger.error("Unknown Exception: ", e);
        }
    }
}
