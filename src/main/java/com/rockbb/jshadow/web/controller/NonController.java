package com.rockbb.jshadow.web.controller;

import com.rockbb.mocha.commons.RequestMethod;
import com.rockbb.mocha.commons.ResponseHelper;
import com.rockbb.mocha.commons.ResultType;
import com.rockbb.mocha.stereotype.Controller;
import com.rockbb.mocha.stereotype.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping(value = "/non", result = ResultType.JSON)
public class NonController {
    private static Logger logger = LoggerFactory.getLogger(NonController.class);

    @RequestMapping("/index2")
    public void doIndex2(ResponseHelper response) {
        response.set("code", 0);
        response.set("status", "success");
        response.set("message", "非常好");
    }

    @RequestMapping(value = "/do", method = {RequestMethod.POST})
    public String doPost(ResponseHelper response) {
        logger.info("doPost");
        return null;
    }

    @RequestMapping(value = "/do", method = {RequestMethod.GET})
    public void doGet(ResponseHelper response) {
        response.set("code", 0);
        response.set("message", "success");
    }
}
