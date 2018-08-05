package com.rockbb.jshadow.web.controller.sys;

import com.rockbb.mocha.stereotype.Controller;
import com.rockbb.mocha.stereotype.RequestMapping;

/**
 * Created by Milton on 2016/12/24.
 */
@Controller
public class AdminController {
    @RequestMapping()
    public String method1() {
        return null;
    }

    @RequestMapping("/somewhere/inner")
    public String innerMethod1() {
        return "index.ftl";
    }
}
