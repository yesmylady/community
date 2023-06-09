package com.example.community.controller;


import com.example.community.dto.QuestionDTO;
import com.example.community.mapper.QuestionMapper;
import com.example.community.model.Question;
import com.example.community.model.User;
import com.example.community.service.QuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;


    Logger logger = Logger.getLogger(this.getClass());

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        logger.info("进入问题编辑页面，id传入model并隐藏在签单表单中");
        QuestionDTO questionDTO = questionService.selectById(id);
        model.addAttribute("id", questionDTO.getId());
        model.addAttribute("title", questionDTO.getTitle());
        model.addAttribute("description", questionDTO.getDescription());
        model.addAttribute("tag", questionDTO.getTag());
        return "/publish";
    }

    @GetMapping("/publish")
    public String publish() {
        logger.info("go to publish");
        return "/publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            /**
             * 本方法会被（1：问题编辑页面）（2：问题发布页面）以两种方式调用，
             * 第一种会传过来id，第二种不会，所以这里id对应的@RequestParam要加上required=false，并在方法中判断id!=null时才对其操作
             * 否则会抛出MissingServletRequestParameterException异常，而且我tm咋搞的，异常不在控制台显示，我tm真服了
             */
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tag") String tag,
            HttpServletRequest request,
            Model model  // 像这种有多个返回路径时，model是跟着谁呢？return谁跟着谁？应该是！
            ) {
        logger.info("发布新问题或更新问题...");

        // 这里model用于格式明显有误时保留之前填的内容
        if (id != null)
            model.addAttribute("id", id);
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);

        // 检查是否有错误，有的话直接重新刷新当前页面了
        if (title == null || title.trim().equals("")) {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (description == null || description.trim().equals("")) {
            model.addAttribute("error", "描述不能为空");
            return "publish";
        }
        if (tag == null || tag.trim().equals("")) {
            model.addAttribute("error", "tag不能为空");
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        // question格式正确，存入数据库
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        if (id != null)
            question.setId(id.longValue());

        logger.info("开始将问题存入数据库...");
        questionService.createOrUpdate(question);
        logger.info("存入成功");
        return "redirect:/";  // 重定向到主页
    }
}
