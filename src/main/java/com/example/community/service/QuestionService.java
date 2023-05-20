package com.example.community.service;

import com.example.community.dto.PaginationDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.mapper.QuestionMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.Question;
import com.example.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public void create(Question question) {
        questionMapper.create(question);
    }

    public List<Question> findAll(Integer offset, Integer size) {
        return questionMapper.findAll(offset, size);
    }

    public Integer countByUserId(Integer userId) {
        return questionMapper.countByUserId(userId);
    }


    public PaginationDTO selectPageEntries(Integer page, Integer size) {
        Integer totalPage = this.totalPage(size);
        if (page < 1) page = 1;  // 把currentPage限定在正确的范围内
        else if (page > totalPage) page = totalPage;

        Integer offset = size * (page - 1);

        List<Question> questionList = questionMapper.findAll(offset, size);  // 这一页从offset到offset+size
        List<QuestionDTO> questionDTOList = new ArrayList<>();  // List是抽象接口，得用ArrayList做承接
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);  // 根据变量名字直接拷贝，不用一个一个set了
            questionDTO.setUser(user);

            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestionDTOList(questionDTOList);
        paginationDTO.setPage(totalPage, page, size);
        return paginationDTO;
    }

    public Integer count() {
        return questionMapper.count();
    }

    public Integer totalPage(Integer size) {
        Integer totalCount = this.count();
        return (int) Math.ceil((double)totalCount / size);  // 注意int/int自动为int！得用double
    }

    public PaginationDTO selectByUserId(Integer userId, Integer page, Integer size) {
        Integer totalPage = this.totalPage(size);
        if (page < 1) page = 1;  // 把currentPage限定在正确的范围内
        else if (page > totalPage) page = totalPage;

        Integer offset = size * (page - 1);

        List<Question> questionList = questionMapper.selectByUserId(userId, offset, size);  // 这一页从offset到offset+size
        List<QuestionDTO> questionDTOList = new ArrayList<>();  // List是抽象接口，得用ArrayList做承接
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);  // 根据变量名字直接拷贝，不用一个一个set了
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestionDTOList(questionDTOList);
        paginationDTO.setPage(totalPage, page, size);
        return paginationDTO;

    }

    public QuestionDTO selectById(Integer id) {
        Question question = questionMapper.selectById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(userMapper.selectByPrimaryKey(question.getCreator()));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        } else {
            question.setGmtModified(question.getGmtCreate());
            questionMapper.update(question);
        }
    }
}
