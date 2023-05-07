package com.example.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class DatabaseTest {

    @Autowired  // 这个注解把jdbc和mysql配置信息啥的注入jdbcTemplate Bean，才能正常使用
    JdbcTemplate jdbcTemplate;

    @Test
    void testOracleConnect(){
        //USERINFO是你自己连接数据库中的表，这里查询的是记录的数目
        Long aL = jdbcTemplate.queryForObject("select count(*) from user",Long.class);
        System.out.println(aL);
    }
}
