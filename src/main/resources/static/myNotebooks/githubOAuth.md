### github授权登录
使用github提供的登录接口进行授权登录，并把该github用户的指定部分个人
信息保存到本地数据库，对应一个本地的用户。步骤如下

premise: 先把自己的库上传到github repo

 1. 第三方应用先在github的开放认证平台申请app的授权，得到client_id和client_secret。 (done in github.com)
 
 2. 第三方应用提供一个按钮或链接，让用户跳转到github的授权登录页，传递client_id和回调地址。(done in index.html)
 
 3. 用户在github上登录并确认授权，github会返回一个授权码code，并重定向到回调地址。 (here, callback() process code&state)
 
 4. 第三方应用收到code后，拼接client_id和client_secret，向github请求access_token。 (拼接成一个DTO对象，再转成json放进requestBody)
 
 5. github验证参数合法后，返回access_token给第三方应用。(执行上一步生成的请求，得到response并在其中提取出token)
 
 6. 第三方应用使用access_token，向github请求用户数据。
 
 7. 把用户数据存入本地数据库，并更新登录状态。
 
 #### issues
 ##### step6：用token请求github用户数据
 
 github不再支持url后缀方式token的get请求，而是必须以Authorization: Bearer {token}的格式把token加到请求头里面

```java
 Request request = new Request.Builder()  
         .addHeader("Authorization", "Bearer " + accessToken)  // 新增这一行
         .url("https://api.github.com/user")  // 不再使用url加后缀的方式
         .build();
```

##### github用户名
你的github用户名yesmylady不是name，name需要去profile里自己设。。。