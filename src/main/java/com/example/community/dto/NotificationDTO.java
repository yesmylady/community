package com.example.community.dto;

import com.example.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private Long outerId; 
    private String outerTitle;
    private String type;
}
