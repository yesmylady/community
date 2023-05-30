把项目里所有notebook综合到这里来

### 报错
- 报kotlin错点左上角Build里的Rebuild Project，可能因为我瞎用@Mapper或者前端thymeleaf代码属性名错误导致build出问题？  
- @Mapper不是那么用的啊大傻逼，用@Resource或@Autowired
- 注意在properties里配置mybatis驼峰映射！！！

## 项目结构

### controller
controller层只负责调用service层接口以返回正确的页面和数据，逻辑判断和处理一概交给service层来处理

### service
service层完成controller层要做的抽象的功能，通过调用mapper层数据库访问接口来访问数据库

### mapper
- 这层专属于mybatis，里面存的都是interface，mybatis已经配置好了路径，能找到这里
- 直接用@Select @Insert还是另写xml文件做数据库访问都是mybatis支持的
- mybatis都会把这里的接口方法生成为可调用的类方法，在service层里用@Mapper注入接口即可使用

- 用@Select等注解在mapper层接口里写sql语句时，sql与java参数之间的传递用#{参数名}即可，类参数里边的属性可以直接放进去，不用加类名前缀


### model
model层里的@Data对象是与数据库表直接对应的(唯一不同是驼峰命名)，因为数据库字段可以是null，所以model里的基本数据类型要用包装类封装好

### dto
- 与model层把数据库表转成object不同的是，dto（data to object）是针对外部传过来的数据
- 还有一个作用是对model层的数据进行扩展，比如QuestionDTO可以通过将User对象作为属性来达到“外键关联”的效果

## Mybatis
### Mybatis generator
- 是基于mybatis框架的一个根据数据库表自动生成java对象、接口、xml文件的一个插件。  
- mybatis plus也有这个功能，且更加强大。可以自动生成每张表的CRUD相关的基础操作，不过复杂操作仍需要自己写。  
- 在pom里配置插件和其数据库依赖、再generatorConfig.xml里配置生成过程控制，此xml第一行不要加注释！
- 运行命令：mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
- 命令: mvn mybatis-generator:generate不会删除之前生成的文件，但会生成多余的  

会生成两个（User和UserExample），UserExample用来指定要对数据库进行的操作，里边封装好了针对数据库表每一列的CRUD基本方法，
只需要选中指定的函数并传入指定的参数即可使用。  

Example文件主要用来配置where条件，具体过程是：  
- 需要指定where条件时，先new一个Example对象，调用其createCriteria方法再调用配置where条件方法。（select默认会返回列表）
- 不需要指定where条件时直接调用对应方法即可
- 用主键当做where条件时直接上方法即可，不用配置Example对象，且返回的是一个pojo对象而非List
- update还能部分更新，具体看源码
```java
// 本质是为数据库表中某列添加一条where语句！keep it in mind
userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());  
```
#### 配置文件
generatorConfig.xml文件位于根目录下，会被自动识别，根据官网说明按步骤配置参数确定生成文件内容
- mysql表中text字段在进行生成时，不会自动转化为jdbc的varchar类型，需要在<table>中新建<columnOverride>标签手动指定其映射关系

#### 自己写sql
有时候generator生成的功能不能满足要求，就需要自己写sql
1. 在mapper层（xml和interface）都拷贝原文件如QuestionMapper，并加上Ext表示这是自己的，就不会被mvn generator命令搞没掉
2. 在service层中注入QuestionExtMapper，并在原使用处改成这个mapper的方法

## 前端
本项目没有采用前后端分离方式
前后端分离：采用json在前后端之间传递数据，controller层用@RequestBody和@ResponseBody接受传递处理json和pojo  
非前后端分离：如本项目，用渲染的方式从后端向前端传递数据（在controller层使用Model添加context），后端接收前端数据则可以在形参上用@RequestPara()  

### thymeleaf
与java的Model或ModelAndView结合使用  
${}里是参数，@{}是路由参数  
注意th:text和th:value的不同

### bootstrap3
项目访问本地css样式时是按照url访问的，相对路径时默认从当前url同级目录中找，绝对路径则会到static下找。

遇到问题时在前端试试能不能找到所需文件


### 富文本编辑
参考：https://github.com/pandao/editor.md
导入需要的文件至static下

#### 图片上传
本地服务：上传图片时把图片存到本地static/images下，之后前端再通过<img>的url就可以访问到图片

先在富文本配置里设置能上传图片，并设置上传地址，即后端处理图片的controller
在FileController里面写  

由于插件要求controller返回固定的json类型的信息表示上传成功或失败，写一个DTO