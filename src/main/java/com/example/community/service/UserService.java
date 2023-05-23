package com.example.community.service;

import com.example.community.mapper.UserMapper;
import com.example.community.model.User;
import com.example.community.model.UserExample;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    Logger logger = Logger.getLogger(this.getClass());

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());  // 本质是添加一条where语句！keep it in mind
        List<User> users = userMapper.selectByExample(userExample);

        if (users.isEmpty()) {  // 表中没有此用户，插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {                // 已存在此用户，更新信息
            User dbUser = users.get(0);
            user.setGmtModified(System.currentTimeMillis());
            userExample.clear();  // 清除之前残留的where条件
            userExample.or().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(user, userExample);  // 用userExample定位到行，用user进行部分更新
        }
    }

    public void insert(User user) {
         userMapper.insert(user);
    }

    public User findByToken(String token) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() != 1) {
            logger.info("数据库中未找到或找到多个拥有此token的用户: " + token);
            return null;
        } else {
            return users.get(0);
        }
    }

    public User findById(Integer creator) {
        return userMapper.selectByPrimaryKey(creator);
    }

    public User findByAccountId(String accountId) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(accountId);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() != 1) {
            logger.info("数据库中未找到或找到多个拥有此account id的用户: " + accountId);
            return null;
        } else {
            return users.get(0);
        }
    }

    public void update(User user) {
        if (user==null || this.findById(user.getId()) == null) {
            logger.info("没有此用户，无法更新！请检查传入的user对象");
        } else
            userMapper.updateByPrimaryKey(user);
    }
}
