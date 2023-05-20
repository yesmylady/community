再说一次，这个目录默认是放html文件的，
因为spring默认会在这个目录下查找html文件

- 这个二逼thymeleaf跟别的不一样，他只能把别的文件的组件replace到自己这里，不能搞继承
- 把每个组件写成一个文件，并在相应的功能标签里加上th:fragment表示一个模块，其他文件可以通过
在标签中加上th:replace属性拿到模块替换自己，或用th:include附加模块，具体看代码
  
思路：每个页面的body都有一套layout: header, nav, main, footer

#### 导入资源文件
必须在路径最前面加上/，不加的话默认是从与当前url同级别下找css文件(css目录在网页端url的第一层可以访问到)