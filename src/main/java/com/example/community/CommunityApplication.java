package com.example.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * (springboot默认把与这个文件同目录下的文件夹都加载到环境里，所以一般在这个目录下编写后端逻辑)
 *
 * 不同于毕设的前后端分离，这个项目用的是thymeleaf来实现类似jsp的非前后单分离开发方式
 * @ComponentScan annotation to your main application class or configuration class.
 * This annotation tells Spring to scan for components (including mappers) in the specified packages.
 */
//@ComponentScan(basePackages = {"com.example.community.mapper"})  别鸡巴跟着乱写！这个会把扫controller层弄没！
@MapperScan("com.example.community.mapper")  // I don't know if it is usable
@SpringBootApplication
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}

