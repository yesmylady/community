package com.example.community.service;


import com.example.community.dto.NotificationDTO;
import com.example.community.dto.PaginationDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.enums.NotificationStatusEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.NotificationMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;


    public PaginationDTO list(Long userId, Integer page, Integer size) {
        Integer totalPage = this.totalPage(size);
        if (page < 1) page = 1;  // 把currentPage限定在正确的范围内
        else if (page > totalPage) page = totalPage;

        Integer offset = size * (page - 1);

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        notificationExample.setOrderByClause("gmt_create desc");  // 时间倒序排列
        List<Notification> notificationList = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));  // 这一页从offset到offset+size
        List<NotificationDTO> notificationDTOList = new ArrayList<>();  // List是抽象接口，得用ArrayList做承接

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        for (Notification notification : notificationList) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);  // 根据变量名字直接拷贝，不用一个一个set了
            notificationDTO.setType(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOList.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOList);
        paginationDTO.setPage(totalPage, page, size);
        return paginationDTO;
    }


    public Integer count() {  // 通知总数
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria();  // 空条件
        return (int)notificationMapper.countByExample(notificationExample);
    }


    private Integer totalPage(Integer size) {  // 总页数
        Integer totalCount = this.count();
        return (int) Math.ceil((double)totalCount / size);  // 注意int/int自动为int！得用double
    }

    public Long unreadCount(Long userId) {  // 获得未读的评论总数
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        } else if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        Notification forStatusChange = new Notification();
        forStatusChange.setStatus(NotificationStatusEnum.READ.getStatus());
        forStatusChange.setId(notification.getId());
        notificationMapper.updateByPrimaryKeySelective(forStatusChange);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);  // 根据变量名字直接拷贝，不用一个一个set了
        notificationDTO.setType(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
