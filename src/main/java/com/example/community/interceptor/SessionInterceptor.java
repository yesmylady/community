package com.example.community.interceptor;

import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import com.example.community.model.UserExample;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service  // 必须把这个类加到spring容器里，否则不给启动项目
public class SessionInterceptor implements HandlerInterceptor {
    Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie: cookies)
                if (cookie.getName().equals("token")) {
//                    logger.info("前端cookie里有用户token");
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (!users.isEmpty()) {
//                        logger.info("在数据库中找到了与之对应的token, 把这个user加入到session中，这样前端页面就知道登录成功了");
                        request.getSession().setAttribute("user", users.get(0));
                    } else {
                        logger.info("数据库中没有这个token对应的用户！就算session本来有user信息也不予自动登录");
                        request.getSession().setAttribute("user", null);  // 清空session中的user信息
                    }
                    break;
                }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
