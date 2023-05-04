package com.example.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Controller： In Spring’s approach to building web sites, HTTP requests are handled by a controller.
 *              在spring下，每个http请求对应的后端方法都要加上controller注解
 */
@Controller
public class GreetingController {
    /**
     * model对象的功能就类似于django中的render(context, *.html)中的context，把变量带进静态html里渲染
     *
     * @GetMapping("path") ensures that HTTP GET requests to /greeting are mapped to the greeting() method.
     *                     说人话就是把前端访问的路径"/greeting"绑定交由这个greeting()方法去处理
     *
     * @RequestParam 加在String name上的一个注解：表示请求参数，即前端发过来的url?后面跟的参数
     *
     * @param name 前端经由name参数传递过来的字符串，非必须，默认是"World"
     * @param model 可以把name装进这个对象里去，这个model对象会自动渲染进greeting.html里
     * @return 返回的greeting会由springboot自动映射到resources/templates下的同名html文件，并传给前端
     */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
}
