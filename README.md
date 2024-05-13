# cas-server-demos

## 一些说明

| 模块             | 说明        | 访问地址                          |
|----------------|-----------|-------------------------------|
| cas-server     | cas服务端    | http://cas.demo.com:8080   |
| cas-client-1   | cas客户端1   | http://client1.demo.com:18001 |
| cas-client-2   | cas客户端2   | http://client1.demo.com:18002 |
| cas-management | cas服务管理程序 | https://mgmt.demo.com:18443   |

## 本地运行

准备好环境：JDK21、Tomcat10

首先编辑hosts文件，加入下面的映射

```txt
127.0.0.1  cas.demo.com
127.0.0.1  client1.demo.com
127.0.0.1  client2.demo.com
127.0.0.1  mgmt.demo.com
```

## 数据库脚本

```sql
CREATE TABLE public.basic_user (
  user_id int NULL,
  username varchar(255) NULL,
  nick_name varchar(255) NULL,
  password varchar(255) NULL,
  sex int NULL
);

COMMENT ON COLUMN public.basic_user.user_id IS '主键';
COMMENT ON COLUMN public.basic_user.username IS '账号';
COMMENT ON COLUMN public.basic_user.nick_name IS '昵称';
COMMENT ON COLUMN public.basic_user.password IS '密码';
COMMENT ON COLUMN public.basic_user.sex IS '性别';

INSERT INTO public.basic_user  (user_id, username, nick_name, password, sex)
VALUES(1, 'admin', '管理员', '111111', 1);
INSERT INTO public.basic_user  (user_id, username, nick_name, password, sex)
VALUES(2, 'zhangsan', '张三', '222222', 2);        
```

## 遇到的问题


### 登录状态无法保持？

1. 以https协议访问
2. 若要以http协议访问，需关闭强制使用https

application.yml配置：

```yaml
cas:
  tgc:
    # 关闭强制使用https，否则无法实现统一认证（或使用https）
    secure: false
    # secure设置为false，必须同时设置sameSite策略为Lax或Strict，否则cookie还是不会保存
    same-site-policy: Lax
```

### cas-management 无限重定向？

不能用http协议，只能用https协议

### cas-management 重定向至 https://cas.example.org:8443/cas ?

配置文件加载顺序问题，尝试将配置文件放在 `/etc/cas/config/management.yml` 或者其他指定的位置。

可以指定环境变量：`cas.standalone.configuration-directory` 或 `cas.standalone.configuration-file` 
