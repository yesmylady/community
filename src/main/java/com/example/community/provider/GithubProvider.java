package com.example.community.provider;

import com.alibaba.fastjson.JSON;
import com.example.community.dto.AccessTokenDTO;
import com.example.community.dto.GithubUser;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class GithubProvider {
    Logger logger = Logger.getLogger(this.getClass());

    /**
     * @param accessTokenDTO 在github auth要求下拼接好的一个post请求，以获取这个用户的access token
     * @return 该用户的token，以轻松访问用户信息
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        logger.info("step4: get access token from github");
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();  // github返回的token信息
            logger.info("github token: " + string);
            return string.split("&")[0].split("=")[1];  // 从字符串中提取出token并返回
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用token做一个get请求去访问github用户的个人信息
     * @param accessToken
     * @return
     */
    public GithubUser getUser(String accessToken) {
        logger.info("get github user information and package it to local User and update database.");
        OkHttpClient client = new OkHttpClient();

        /**
         * 更新：github不再支持url后缀方式的token请求，而是必须以Authorization: Bearer {token}的格式把token加到请求头里面
         */
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)  // 试试
                .url("https://api.github.com/user")
                .build();

        try (Response response = client.newCall(request).execute()){  // 注意token可能是错的！
            String str = response.body().string();
            logger.info("returned token: " + str);
            // 把github返回的user信息转换为本地的GithubUser对象
            GithubUser githubUser = JSON.parseObject(str, GithubUser.class);
            response.close();
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
