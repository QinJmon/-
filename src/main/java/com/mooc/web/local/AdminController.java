package com.mooc.web.local;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/local")
public class AdminController {
    /*绑定账号路径*/
    @RequestMapping(value = "/customerbind", method = RequestMethod.GET)
    public String bindlocalauth() {
        return "local/customerbind";
    }

    /*修改密码路由*/
    @RequestMapping(value = "/changepsw", method = RequestMethod.GET)
    public String changepsw() {
        return "local/changepsw";
    }


    /*登录页路由*/
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "local/login";
    }
}
