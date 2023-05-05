package com.example.community.dto;

public class GithubUser {
    /**
     * Refer to Github user information, extract what I need.
     */
    private String name;
    private long id;
    private String bio;  // 其他简介

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
