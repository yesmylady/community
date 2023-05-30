package com.example.community.controller;

import com.example.community.dto.CommentDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.service.CommentService;
import com.example.community.service.QuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model) {
        try {
            questionService.incView(id);  // 累加阅读数
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        QuestionDTO questionDTO = questionService.selectById(id);  // DTO意味着模型方便与前端交互
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);  // 查标签相关问题
        List<CommentDTO> comments = commentService.listByParentId(id, CommentTypeEnum.QUESTION.getType());

        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions); 
        logger.info("进入问题" + questionDTO.getId() + ": " + questionDTO.getTitle());
        return "question";
    }


}
