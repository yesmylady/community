package com.example.community.dto;


import lombok.Data;

@Data
public class GithubUser {
    /**
     * Refer to Github user information, extract what I need.
     * 我们只需要github user中的这三个信息，其中id在github系统里也是唯一的（对应到本地database的account_id）
     */
    private String name;
    private long id;
    private String bio;  // 其他简介
    private String avatarUrl;
}

// jsjyz@bistu.edu.cn