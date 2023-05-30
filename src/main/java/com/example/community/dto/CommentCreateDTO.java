package com.example.community.dto;

import lombok.Data;


/**
 * 仅在前端创建新的评论时使用这个DTO
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
