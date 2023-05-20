package com.example.community.controller;

import com.example.community.dto.PaginationDTO;
import com.example.community.service.QuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private QuestionService questionService;  // controller层中不应该存在mapper，mapper逻辑应交由service层处理

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {
        logger.info("go to index");
        // 检查cookie中的token信息，实现自动登录
        // 在SessionInterceptor中做了

        // 传过来的page可能超出范围，在service层里面处理
        PaginationDTO paginationDTO = questionService.selectPageEntries(page, size);
        model.addAttribute("pagination", paginationDTO);
        return "index";
    }
}
