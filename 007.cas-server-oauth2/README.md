# oauth方式认证

## 授权码模式

1. 构造如下访问地址，在浏览器打开，请求授权

   http://localhost:8080/cas/oauth2.0/authorize?response_type=code&client_id=client1&redirect_uri=https%3A%2F%2Fwww.baidu.com

2. 输入用户名密码进行登录

3. 登录成功后页面重定向到redirect_uri，并携带code参数，如：
   
    https://www.baidu.com/?code=OC-2--aM3mETAW-QolHOF0YFx5DEU2DEISgs1
   
4. 客户端后台调用接口，获取accessToken

    http://localhost:8080/cas/oauth2.0/accessToken?grant_type=authorization_code&client_id=client1&client_secret=12345678&code=OC-2--aM3mETAW-QolHOF0YFx5DEU2DEISgs1&redirect_uri=https%3A%2F%2Fwww.baidu.com

返回：

    access_token=AT-1--gH8Xlbx2DtXBLP7LuCDbzV4JmKdxC6Q&expires_in=28800

5. 客户端后台调用接口，通过accessToken获取用户信息

    http://localhost:8080/cas/oauth2.0/profile?access_token=AT-1--gH8Xlbx2DtXBLP7LuCDbzV4JmKdxC6Q

返回：
```json
{
  "service" : "https://www.baidu.com",
  "attributes" : {
    "credentialType" : "UsernamePasswordCredential"
  },
  "id" : "admin",
  "client_id" : "client1"
}
```
