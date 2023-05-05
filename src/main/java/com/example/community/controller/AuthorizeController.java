package com.example.community.controller;

import com.example.community.dto.AccessTokenDTO;
import com.example.community.dto.GithubUser;
import com.example.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // github网站会带着para访问这个网址
    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);  // 由github OAuth生成的
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_url(redirectUrl);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);

        String token = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(token);
        System.out.println(user.getName());
        return "index";
    }
}
