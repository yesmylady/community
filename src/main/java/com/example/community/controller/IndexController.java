package com.example.community.controller;

import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {
    Logger logger = Logger.getLogger(this.getClass());

    @Resource
    private UserMapper userMapper;

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        logger.info("go to index");
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("token")) {
                logger.info("前端cookie里有用户token");
                String token = cookie.getValue();
                User user = userMapper.findByToken(token);
                if (user != null) {
                    logger.info("在数据库中找到了与之对应的token");
                    logger.info("把这个user加入到session中，这样前端页面就知道登录成功了");
                    request.getSession().setAttribute("user", user);
                } else {
                    logger.info("数据库中没有这个token对应的用户！不予自动登录");
                }
                break;
            }
        }

        return "index";
    }
}
