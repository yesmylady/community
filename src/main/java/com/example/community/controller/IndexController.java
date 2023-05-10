package com.example.community.controller;

import com.example.community.dto.QuestionDTO;
import com.example.community.mapper.QuestionMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.Question;
import com.example.community.model.User;
import com.example.community.service.QuestionService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class IndexController {
    Logger logger = Logger.getLogger(this.getClass());

    @Mapper
    private UserMapper userMapper;

    @Mapper
    private QuestionService questionService;  // controller层中不应该存在mapper，mapper逻辑应交由service层处理

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model) {
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

        List<QuestionDTO> questionList = questionService.findAll();
        model.addAttribute("questions", questionList);
        return "index";
    }
}
