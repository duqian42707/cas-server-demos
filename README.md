# cas-server-demos

## 遇到的问题

### ./gradlew run 运行失败

可能是端口被占用了，没有提示出明确的信息

### Windows平台报错 /etc/cas/theKeystore 不存在

可能是没有运行 ./gradlew createKeystore

### 运行成功，访问 /cas/login 页面报错500

日志报出异常：

```text
java.security.InvalidAlgorithmParameterException: AlgorithmParameterSpec not of GCMParameterSpec
```

可能是CAS的bug，或是jdk版本问题，暂时只能把webflow加密关闭

```yml
cas:
  webflow:
    crypto:
      enabled: false
```

### web方式部署到tomcat容器，访问 /cas/ 提示404

未解决，感觉并没有部署成功，catalina.out文件中没有cas启动时打印出的日志

原因是tomcat版本不对，tomcat9不行，得需要tomcat10

### web方式部署，ServerProperties.getPort() is null

```text
	Caused by: java.lang.NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because the return value of "org.springframework.boot.autoconfigure.web.ServerProperties.getPort()" is null
		at org.apereo.cas.tomcat.CasTomcatServletWebServerFactory.<init>(CasTomcatServletWebServerFactory.java:55)
```

添加配置：

```yaml
server:
  port: 8443
```
### web方式部署，启动后访问/cas页面，出现 `Spring Security 的认证界面，而不是CAS的默认登录页面
