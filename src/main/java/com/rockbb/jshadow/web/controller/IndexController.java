package com.rockbb.jshadow.web.controller;

import com.rockbb.jshadow.service.dao.UserDAO;
import com.rockbb.jshadow.service.dto.UserDTO;
import com.rockbb.mocha.commons.Pager;
import com.rockbb.mocha.commons.RequestMethod;
import com.rockbb.mocha.commons.ResponseHelper;
import com.rockbb.mocha.commons.ResultType;
import com.rockbb.mocha.commons.TimeUtil;
import com.rockbb.mocha.db.ArgGen;
import com.rockbb.mocha.stereotype.Controller;
import com.rockbb.mocha.stereotype.ModelAttribute;
import com.rockbb.mocha.stereotype.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class IndexController {
    private static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/index")
    public String doIndex(ResponseHelper response) {
        response.set("content", "这是测试文字");
        ArgGen args = new ArgGen()
                .addLike("cellphone", "13810")
                .addNotEmpty("createBefore", TimeUtil.getDate("2013-12-23", TimeUtil.FORMAT_YMD));
        List<UserDTO> users = new UserDAO().list(new Pager(), args.getArgs());
        response.set("users", users);
        return "index.ftl";
    }

    @RequestMapping(value = {"/login"}, method = {RequestMethod.GET})
    public String doLogin(ResponseHelper response) {
        response.set("title", "标题");
        response.set("content", "demo");
        return "index.ftl";
    }

    @RequestMapping(value = {"/invoke"}, method = {RequestMethod.GET})
    public String doRegister(HttpServletRequest request, ResponseHelper response, int value, String id, long value2,
                             @ModelAttribute("name") String name) throws IOException {
        String cmd = request.getParameter("cmd");
        logger.info("cmd {}", cmd);
        return "index.ftl";
    }

    @RequestMapping(value = {"/logout", "/quit", "/exit"}, result = ResultType.RAW)
    public String doLogout(@ModelAttribute("name") String name) {
        return name;
    }
}
