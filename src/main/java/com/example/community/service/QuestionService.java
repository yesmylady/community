package com.example.community.service;

import com.example.community.dto.QuestionDTO;
import com.example.community.mapper.QuestionMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.Question;
import com.example.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Mapper
    private QuestionMapper questionMapper;

    @Mapper
    private UserMapper userMapper;

    public List<QuestionDTO> findAll() {
        List<Question> questionList = questionMapper.findAll();
        List<QuestionDTO> questionDTOList = new ArrayList<>();  // List是抽象接口，得用ArrayList做承接
        for (Question question : questionList) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);  // 根据变量名字直接拷贝，不用一个一个set了
            questionDTO.setUser(user);

            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }
}
