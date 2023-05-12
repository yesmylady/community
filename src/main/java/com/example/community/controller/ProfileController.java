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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    // @PathVariable(name = "action")自动把路径中的{action}解析到参数String action里
    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        // 拷贝自IndexCon
        User user = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie: cookies)
                if (cookie.getName().equals("token")) {
                    logger.info("前端cookie里有用户token");
                    String token = cookie.getValue();
                    user = userMapper.findByToken(token);
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
        if (user == null)
            return "redirect:/";  // 用redirect是为了让前端重新请求一次'/'，让IndexCon处理一下，传递必要的信息过去！而不是直接返回index.html文件，这样会丢失thymeleaf参数
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
        } else if ("replies".equals(action)) {
                model.addAttribute("section", "replies");
                model.addAttribute("sectionName", "最新回复");
        }

        PaginationDTO paginationDTO = questionService.selectByUserId(user.getId(), page, size);
        model.addAttribute("pagination", paginationDTO);
        return "profile";
    }
}
