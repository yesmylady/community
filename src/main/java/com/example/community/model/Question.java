package com.example.community.model;

import lombok.Data;

@Data
public class Question {
    private Integer id;  // 用Integer是因为可以是数据库中的null，而且与int可以自动装载，没有什么问题
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;  // 同理，Long是long的包装类
    private Integer creator;   // 关联User
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
}
