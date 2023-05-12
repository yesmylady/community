package com.example.community.controller;

import com.example.community.dto.AccessTokenDTO;
import com.example.community.dto.GithubUser;
import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import com.example.community.provider.GithubProvider;

import org.apache.ibatis.annotations.Mapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 利用github的登录接口实现这边的登录功能，并且拿到用户的部分个人信息
 *
 * 1.第三方应用先在github的开放认证平台申请app的授权，得到client_id和client_secret。 (done in github.com)
 * 2.第三方应用提供一个按钮或链接，让用户跳转到github的授权登录页，传递client_id和回调地址。(done in index.html)
 * 3.用户在github上登录并确认授权，github会返回一个授权码code，并重定向到回调地址。 (here, callback() process code&state)
 * 4.第三方应用收到code后，拼接client_id和client_secret，向github请求access_token。 (如下拼接成一个DTO对象)
 * 5.github验证参数合法后，返回access_token给第三方应用。
 * 6.第三方应用使用access_token，向github请求用户数据。
 * 7.把用户数据存入本地数据库，并更新登录状态。
 *
 * 这个流程之所以要分那么多步骤，是为了保证用户和github之间的安全通信，防止中间人攻击或者泄露敏感信息。摘自newBing
 */

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    // @Value注解把application.properties中的参数赋给对应的变量
    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.url}")
    private String redirectUrl;

    @Autowired
    private UserMapper userMapper;

    Logger logger = Logger.getLogger(this.getClass());

    // github网站会带着para访问这个网址
    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        logger.info("github call '/callback' with para code&state");
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(this.clientId);          // 由github OAuth生成的
        accessTokenDTO.setClient_secret(this.clientSecret);  // 同由github OAuth生成
        accessTokenDTO.setRedirect_url(this.redirectUrl);    // set by myself
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);

        // step4: get token
        String token = githubProvider.getAccessToken(accessTokenDTO);
        logger.info("step4 & 5: get token: " + token);
        if (token == null) {
            logger.info("未获取到token，也拿不到用户信息，认为登录失败");
            return "redirect:/";
        }

        GithubUser githubUser = githubProvider.getUser(token);
        logger.info("step6: get github user info: " + githubUser);

        if (githubUser != null) {
            // 登录成功，写cookie和session
            request.getSession().setAttribute("user", githubUser);
            logger.info("login successful, user name: " + githubUser.getName());

            User user = new User();
            user.setToken(UUID.randomUUID().toString());  //
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userMapper.insert(user);
            response.addCookie(new Cookie("token", user.getToken()));
            /**
             * 要做持久登录功能就要存cookie和session：若cookie中有当前用户信息且session里也有，则等于登录上了
             *                                     若cookie没有，而session里有，
             *                                     若都没有，则在session新建一个用户登录上去
             * session是存储在服务器端的数据，因为这里已经在数据库里存好了，用本地数据库代替session
             * cookie是存在客户端的，需要保存一下
             */

            return "redirect:/";  // 加上redirect前缀相当于前端多发了一个请求，不加的话是后端直接返回主页
        } else {
            // 登录失败，重新登录
            return "redirect:/";
        }
    }

}
