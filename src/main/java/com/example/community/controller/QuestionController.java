package com.example.community.controller;

import com.example.community.dto.QuestionDTO;
import com.example.community.service.QuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model) {
        try {
            questionService.incView(id);  // 累加阅读数
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        QuestionDTO questionDTO = questionService.selectById(id);  // DTO意味着模型方便与前端交互
        model.addAttribute("question", questionDTO);
        logger.info("进入问题" + id + ": " + questionDTO.getTitle());
        return "question";
    }
}
