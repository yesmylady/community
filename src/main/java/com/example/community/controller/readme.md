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