package com.example.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
/**
 * 在springboot项目中使用junit测试：
 * 在maven中配置springboot-test和junit依赖
 * 在类名前加上@SpringBootTest注解，在方法名前加上junit的@Test注解就可以了
 */

@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() {
    }

}
