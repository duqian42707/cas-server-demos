# cas-server-simple

官方template项目：

https://github.com/apereo/cas-overlay-template

分支5.3：

https://github.com/apereo/cas-overlay-template/tree/5.3


登录账号：casuser
登录密码：Mellon

tomcat启动时指定jvm参数：-Dspring.profiles.active=dqv5

## mfa-email 手工验证

1. 以 `-Dspring.profiles.active=dqv5` 启动 CAS。
2. 修改外部 service registry，使目标 service 启用 `mfa-email`。
3. 打开 `/cas/login?service=<matching-service>`。
4. 完成主认证。
5. 在 CAS 日志中检查 `Email MFA code for [casuser] -> [casuser@example.com] is [123456]` 这类日志。
6. 在邮箱 MFA 页面输入日志中的验证码。
7. 确认登录成功。
8. 再次使用同一个验证码，确认认证失败。

当前环境的 service registry 路径为 `file:D:\workspace_personal\cas-server-demos\301.services-json`。
这个目录在当前仓库之外，需要手工修改或在获得明确授权后单独处理。
