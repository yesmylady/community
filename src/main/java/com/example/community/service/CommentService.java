package com.example.community.service;

import com.example.community.dto.CommentDTO;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.enums.NotificationStatusEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.*;
import com.example.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional  // 若方法执行失败，则会把数据库事务回滚，防止“半途而废”
    public void insert(Comment comment) {
        // 插入评论时需要检查几个点：所评问题、类型是否存在、
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARA_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARA_WRONG);
        }

        /**
         * 检查通过，把评论写入数据库
         * 下面parentId按自身type分情况：一级评论表示questionId，二级评论则表示commentId！
         */
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {  // type为2，回复评论
            // 检查母评论是否存在
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            commentMapper.insert(comment);
            // 新增通知条目
            addNotification(comment, NotificationTypeEnum.REPLY_COMMENT.getType());
        } else {  // type为1，回复问题
            // 检查母问题是否存在
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            questionExtMapper.incCommentCount(question);
            // 新增通知条目
            addNotification(comment, NotificationTypeEnum.REPLY_QUESTION.getType());
        }
    }

    private void addNotification(Comment comment, int type) {
        /**
         * for above method using
         * 如果该评论是一级评论：outer是question，notifier是comment.getCommentator，receiver是question.getCreator
         * 如果是二级评论：outerId: comment.getParentId, notifier同上，receiverId: 一级评论(comment.parent)的发布者(.commentator)
         * TODO
         */
        Long questionId = comment.getParentId();

        Notification notification = new Notification();

        if (type == NotificationTypeEnum.REPLY_COMMENT.getType()) {  // comment是二级评论时
            Comment levelOneComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            notification.setReceiver(levelOneComment.getCommentator());
            questionId = levelOneComment.getParentId();
        }
        Question question = questionMapper.selectByPrimaryKey(questionId);
        User user = userMapper.selectByPrimaryKey(comment.getCommentator());

        if (type == NotificationTypeEnum.REPLY_QUESTION.getType()) {
            notification.setReceiver(question.getCreator());
        }
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(type);
        notification.setOuterId(questionId);  // 回复给该评论的parent(问题或评论)
        notification.setOuterTitle(question.getTitle());
        notification.setNotifier(user.getId());
        notification.setNotifierName(user.getName());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByParentId(Long id, Integer type) {
        // 拿一级评论时id是question的、type是QUESTION；拿二级评论时id是comment的、type是COMMENT
        CommentExample commentExample =  new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type);
        commentExample.setOrderByClause("gmt_create desc");  // 添加order条件：按照gmt_create降序排序，即可按新->旧显示
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        // 用Lambda表达式把comment中的创建者（即userId）提取出来组成一个set（set自动去重）
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        // 获取评论人，并转化为map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // 转换comment为commentDTO，即把User对象加到DTO里
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
