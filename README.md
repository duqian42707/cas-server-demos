# cas-server-demos

## 一些说明

| 模块           | 说明      | 访问地址                          |
|--------------|---------|-------------------------------|
| cas-server   | cas服务端  | http://cas.example.org:8080   |
| cas-client-1 | cas客户端1 | http://client1.demo.com:18001 |
| cas-client-2 | cas客户端2 | http://client1.demo.com:18002 |

## 本地运行

准备好环境：JDK21、Gradle（配置国内镜像源）、Tomcat10

首先编辑hosts文件，加入下面的映射

```txt
127.0.0.1  cas.example.org
127.0.0.1  client1.demo.com
127.0.0.1  client2.demo.com
```

## cas-server项目创建过程

- 进入 [Apereo CAS Initializr](https://getcas.apereo.org/ui) 项目初始化工具网站
- 展开`Advanced Options`, `Deployment Type` 选`Web`，并按自己需求进行其他配置
- 点击`Download`按钮，下载模板项目
- 解压项目后，先别着急用idea打开，先用文本编辑器修改`gradle/wrapper/gradle-wrapper.properties`文件中的`distributionUrl`，改成国内镜像地址，否则下载gradle包会很慢。

    ```text
    distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.5-bin.zip
    ```
- 用idea打开项目，修改`build.gradle`，在`war`下面增加`overlays`配置，即：

  ```groovy
  war {
    entryCompression = ZipEntryCompression.STORED
    enabled = false
  
    overlays {
      cas {
        from "org.apereo.cas:cas-server-webapp${project.appServer}:${project.'cas.version'}@war"
  
        provided = false
  
        def excludeArtifacts = ["WEB-INF/lib/servlet-api-2*.jar"]
        if (project.hasProperty("tomcatVersion")) {
          excludes += ["WEB-INF/lib/tomcat-*.jar"]
        }
        excludes = excludeArtifacts
  
      }
    }
  
  }
  ```
- idea配置tomcat服务器（注意tomcat要求版本10以上），部署此项目：`Gradle : org.apereo.cas : cas-server-7.0.4-plain.war (exploded)`
- 用jrebel启动tomcat服务器，即可实现开发、调试、热部署

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

未知。。后来未复现

### 在 IDEA 中开发并启用热部署
