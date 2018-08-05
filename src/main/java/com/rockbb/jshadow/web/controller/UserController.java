package com.rockbb.jshadow.web.controller;

import com.rockbb.jshadow.web.base.SessionAware;
import com.rockbb.jshadow.web.base.SessionBean;
import com.rockbb.mocha.commons.RequestContext;
import com.rockbb.mocha.commons.RequestHelper;
import com.rockbb.mocha.commons.RequestMethod;
import com.rockbb.mocha.commons.ResponseHelper;
import com.rockbb.mocha.stereotype.Controller;
import com.rockbb.mocha.stereotype.ModelAttribute;
import com.rockbb.mocha.stereotype.RequestMapping;

import java.util.Date;


@Controller
@RequestMapping(value={"/user", "/my"})
public class UserController implements SessionAware {
    @RequestMapping()
    public String doIndex(RequestHelper request, ResponseHelper response, @ModelAttribute("date") Date date) {
        response.set("title", "UserController.doIndex");
        response.set("time", request.getDate("time"));
        response.set("date", request.getDate("date"));
        return "index.ftl";
    }

    @RequestMapping(value = "/chg_password", method = {RequestMethod.GET, RequestMethod.PUT})
    public String doChangePassword(
            @ModelAttribute(SessionBean.ATTR_KEY) SessionBean sb,
            RequestHelper request,
            ResponseHelper response) {
        response.set("title", "SessionBean Test");
        response.set("content", sb.getTimestamp());
        return "index.ftl";
    }

    @RequestMapping("/chg_password2")
    public String doChangePassword(RequestContext context, String two) {
        return null;
    }
}
