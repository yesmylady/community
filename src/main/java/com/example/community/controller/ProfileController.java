package com.example.community.controller;

import com.example.community.dto.PaginationDTO;
import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import com.example.community.service.NotificationService;
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
    private NotificationService notificationService;

    @Autowired
    private QuestionService questionService;

    // @PathVariable(name = "action")自动把路径中的{action}解析到参数String action里
    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {

        User user = (User) request.getSession().getAttribute("user");

        if (user == null)
            return "redirect:/";  // 用redirect是为了让前端重新请求一次'/'，让IndexCon处理一下，传递必要的信息过去！而不是直接返回index.html文件，这样会丢失thymeleaf参数
        if ("questions".equals(action)) {
            PaginationDTO paginationDTO = questionService.selectByUserId(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);

            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
        } else if ("replies".equals(action)) {
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);

            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            Long unreadCount = notificationService.unreadCount(user.getId());
            request.getSession().setAttribute("unreadCount", unreadCount);  // for navigation.html
            model.addAttribute("unreadCount", unreadCount);
            logger.info("reply个数：" + paginationDTO.getData().size() + "\n未读个数：" + unreadCount);
        }

        return "profile";
    }
}
