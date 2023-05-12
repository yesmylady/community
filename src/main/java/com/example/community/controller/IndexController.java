package com.example.community.controller;

import com.example.community.dto.PaginationDTO;
import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import com.example.community.service.QuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;  // controller层中不应该存在mapper，mapper逻辑应交由service层处理

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {
        logger.info("go to index");
        Cookie[] cookies = request.getCookies();

        // 检查cookie中的token信息，实现自动登录
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie: cookies)
                if (cookie.getName().equals("token")) {
                    logger.info("前端cookie里有用户token");
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if (user != null) {
                        logger.info("在数据库中找到了与之对应的token");
                        logger.info("把这个user加入到session中，这样前端页面就知道登录成功了");
                        request.getSession().setAttribute("user", user);
                    } else {
                        logger.info("数据库中没有这个token对应的用户！就算session本来有user信息也不予自动登录");
                        request.getSession().setAttribute("user", null);  // 清空session中的user信息
                    }
                    break;
                }

        // 传过来的page可能超出范围，在service层里面处理
        PaginationDTO paginationDTO = questionService.selectPageEntries(page, size);
        model.addAttribute("pagination", paginationDTO);
        return "index";
    }
}
