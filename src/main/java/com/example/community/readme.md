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

### model
model层里的@Data对象是与数据库表直接对应的(唯一不同是驼峰命名)，因为数据库字段可以是null，所以model里的基本数据类型要用包装类封装好

### dto
- 与model层把数据库表转成object不同的是，dto（data to object）是针对外部传过来的数据
- 还有一个作用是对model层的数据进行扩展，比如QuestionDTO可以通过将User对象作为属性来达到“外键关联”的效果

