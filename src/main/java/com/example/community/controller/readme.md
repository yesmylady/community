### 注解
用于简化配置和提供声明式的编程模型，可以大大提高开发效率和代码可读性
- @RequestMapping(value="/...", method=get/post)接收get或post请求，也能简写为@GetMapping和@PostMapping
- @RequestBod修饰参数，接收前端传过来的json数据并可以自动将其注入到pojo中
- @ResponseBody修饰返回值，与上面对应把pojo或map转换成json并写入response的body属性中返回给前端
- @AutoWired/@Resource修饰属性，把这个类注入到spring容器中，交由spring管理
- @Transactional修饰方法，当该方法被调用时，Spring将在事务上下文中执行该方法，并在方法执行完成后，提交或回滚事务。


### Cookie和Session
我对cookie和session的理解：cookie长期存储在client浏览器端，
session也是存在client浏览器，但每次访问网站开始时都是空的，
是用来接收后端传递过来的状态参数的。

这两个东西在client浏览器请求后端时会跟着request被传到后端，此时后端可以查看cookie的内容，并且可以更改session的内容

总的来说：cookie是前端的固有数据，session是用来承接后端数据的容器

```java
/**
 * @Controller： In Spring’s approach to building web sites, HTTP requests are handled by a controller.
 * 在spring项目中，每个http请求对应的后端方法都要加上Controller注解，服务器监听到的http请求由spring自动交给相应路由的controller层方法处理
 */
@Controller
public class GreetingController {
    /**
     * model对象的功能就类似于django中的render(context, *.html)中的context，把变量带进静态html里渲染
     *
     * @GetMapping("path") ensures that HTTP GET requests to /greeting are mapped to the greeting() method.
     *                     顾名思义是用来处理get请求，get的特点就是参数在url后边跟着，而post传的信息在请求头里面
     *                     说人话就是把前端访问的路径"/greeting"绑定交由这个greeting()方法去处理
     *
     * @RequestParam 加在String name上的一个注解：表示请求参数，即前端发过来的url?后面跟的参数
     *
     * @param name 前端经由name参数传递过来的字符串，非必须，默认是"World"
     * @param model 可以把name装进这个对象里去，这个model对象结合thymeleaf渲染进greeting.html里
     * @return 返回的greeting会由springboot自动映射到resources/templates下的同名html文件，并传给前端
     */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
}
```

### github授权登录
- provider: 参考github授权登录教程写出getAccessToken()和getUser(token)
- controller: 
    - 参考github OAuth写自己的callback，并在其中调用provider完成后续登录步骤，并将user插入数据库
    - 后续登录时若cookie中有token，则直接拿出来查找数据库，若存在则通过写入session的方式确定登录
- dto: GithubUser用来接收github用户信息，在controller里封装进本地model User里

前端检查登录的方式是通过检查session中的user

### 主页问题目录分页展示
- dto: 构建一个PaginationDTO，包含当前页面的questionDTOList和各种状态参数  
- controller: getMapping接收page和size参数  
- service: 根据数据库中的信息和page,size参数把paginationDTO填充好，并计算出mapper所需参数  
- mapper: select语句中使用limit start,offset来  

前端：th:each遍历展示本页的question，在bootstrap3里找页码样式，展示在service层写好的逻辑得到的正确页码和状态  

因为是后端实现，每次换页都重新访问index controller

### 发布问题页面
- controller: 路由'/publish'，请求方式新增post，接收title,desc,tag参数，并写入数据库表
- service:   
- mapper: 

前端表单填写内容：\<form action="/publish" method="post">


### 二级评论
在问题展示页面下的评论，可以对评论进行回复，用两次请求分别加载一级、二级评论
评论表的主要属性：所属问题id/所属一级评论id，所属用户，级别