package com.example.community.service;

import com.example.community.dto.PaginationDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.QuestionMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.Question;
import com.example.community.model.QuestionExample;
import com.example.community.model.User;
import com.example.community.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;


    public void create(Question question) {
        questionMapper.insert(question);
    }

    public List<Question> findPage(Integer offset, Integer size) {
        return questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));  // 这一页从offset到offset+size
    }

    public Integer countByUserId(Integer userId) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        return (int)questionMapper.countByExample(questionExample);
    }


    public PaginationDTO selectPageEntries(Integer page, Integer size) {
        Integer totalPage = this.totalPage(size);
        if (page < 1) page = 1;  // 把currentPage限定在正确的范围内
        else if (page > totalPage) page = totalPage;

        Integer offset = size * (page - 1);


        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));  // 这一页从offset到offset+size
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

    public Integer count() {  // 问题总数
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria();  // 空条件
        return (int)questionMapper.countByExample(questionExample);
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

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));  // 这一页从offset到offset+size
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
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND  );
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(userMapper.selectByPrimaryKey(question.getCreator()));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question == null) return;
        logger.info("question内容："+question.toString());
        if (question.getId() == null) {  // 给的是一个新问题，无id属性
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        } else if(questionMapper.selectByPrimaryKey(question.getId()) == null){  // 有id属性，但数据库中没查到，假数据！
            logger.info("没有此id的问题！更新失败！");
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        } else {  // 有id，且在数据库中存在，则可以直接用主键更新！代码简单点
            question.setGmtModified(question.getGmtCreate());
            questionMapper.updateByPrimaryKey(question);
        }
    }
}
