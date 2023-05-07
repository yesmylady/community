#### 项目结构问题
spring是默认扫描根目录下的controller层的，不要在application里随意更改扫描的组件层。

pom里很多依赖类似mybatis、jdbc、thymeleaf依赖要用springboot版的

#### 数据库访问
还是使用自己的mysql数据库
1. 自己在mysql里建一个community库
2. 在pom里面导入jdbc-spring、mysql依赖，此时就可以使用了
3. 通过mybatis使用，pom导入mybatis-spring依赖
4. 在根目录新建一个mapper层，在里面写mybatis接口(@Mapper注解和CRUD注解)即可访问数据库
5. 写复杂语句时，在resources里也建一个mapper层，写xml文件访问数据库，并配置两个mapper目录的映射关系


#### controller层
controller层是直接与前端交互的，这是spring赋予这一层的能力：前端发出请求到后端这里，
由controller层对应url的函数接收并处理，前端的请求中有很多信息，可以只选择接收其中
一部分，
- @RequestParam：接收url某个后缀参数
- request：表示前端传过来的信息，如cookie
- response：表示后端准备响应给前端的数据，可以set cookie
```java
public String callback(@RequestParam(name="code") String code,
                       @RequestParam(name="state") String state,
                       HttpServletRequest request,
                       HttpServletResponse response) {
                            ...
                       }
```

