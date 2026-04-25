# 邮箱验证码 MFA 设计

## 概述

本设计会在当前 CAS 5.3 overlay 工程中新增一个独立的多因素认证提供方，名称为 `mfa-email`。

该提供方通过向用户邮箱发送一次性验证码来完成二次认证。在第一版中，不会真实发送邮件，而是把目标邮箱地址和验证码写入应用日志。用户邮箱地址来自本地静态配置映射。

该提供方与 `mfa-gauth` 完全独立。现有的 Google Authenticator MFA 能力继续保留，不受影响。

## 目标

- 新增一个 provider id 为 `mfa-email` 的 MFA 提供方
- 通过 registered service 的 MFA 策略触发该 provider
- 从本地配置中解析用户邮箱
- 生成短时有效的一次性验证码
- 不真实发送邮件，而是在日志中输出目标邮箱和验证码
- 提供一个独立的 MFA 页面让用户输入验证码
- 用户输入正确验证码后完成二次认证

## 非目标

- 真实 SMTP 邮件发送
- 用户自助邮箱注册或绑定
- 集群可共享或持久化的验证码存储
- 基于用户属性、主属性或全局策略触发 MFA
- “重新发送验证码”交互
- 限流、防刷、锁定等安全增强能力

## 备选方案

### 方案 1：复用 CAS 内置 simple MFA

使用 CAS 内置的简单 MFA 机制，再把它改造成“邮箱验证码”的表现形式。

优点：

- 代码量较少
- CAS 已有一部分现成能力

缺点：

- provider 身份和语义不够清晰
- 不容易得到一个真正独立的 `mfa-email`
- 在验证码生成和发送语义上还需要额外适配

### 方案 2：新增一个独立的 `mfa-email` provider

新增一套独立的 provider、credential、handler、action、view 和 token repository，专门处理邮箱验证码 MFA。

优点：

- provider 身份清晰，直接就是 `mfa-email`
- 配置和流程边界清楚
- 后续扩展为真实邮件发送更自然
- 不会混入 `mfa-gauth` 或 TOTP 的语义

缺点：

- 当前需要新增更多代码

### 方案 3：复制并改造 `mfa-gauth`

复制 Google Authenticator 的实现，再改造成邮箱验证码 MFA。

优点：

- 同一 CAS 版本内有现成参考实现

缺点：

- 会带入注册、TOTP 等不属于邮箱验证码的语义
- 后续维护不够干净
- 长期维护成本更高

### 结论

采用方案 2，做一个真正独立的 `mfa-email` provider。

## 触发模型

`mfa-email` 仅通过 registered service 的 MFA 策略触发。

service 定义中的 `multifactorAuthenticationProviders` 必须显式包含 `mfa-email`。这意味着：

- 请求中带有 `service` 参数时，只有该 service 命中配置了 `mfa-email` 的 registered service，才会触发邮箱验证码 MFA
- 没有匹配 service 的请求不会触发 `mfa-email`
- 现有基于 `mfa-gauth` 的 service 策略可以继续保留，与 `mfa-email` 并存

## 用户邮箱来源

第一版从静态配置中解析用户邮箱。

示例配置结构：

```properties
dqv5.authn.mfa.email.name=mfa-email
dqv5.authn.mfa.email.code-length=6
dqv5.authn.mfa.email.expiration-seconds=300
dqv5.authn.mfa.email.accounts.casuser=casuser@example.com
```

行为如下：

- 使用已完成主认证的 CAS principal 用户名作为查找 key
- 如果找到映射，则进入邮箱 MFA 流程
- 如果找不到映射，则 MFA 流程失败，并记录原因日志

## 架构

实现会在当前 overlay 的源码中新增一组小而独立的组件。

### 配置类

新增一个专用的 Spring `@Configuration` 类，负责注册：

- `mfa-email` provider bean
- 邮箱验证码 repository
- 验证码生成器
- 基于日志的邮件发送器
- MFA webflow configurer
- 发码 action
- 认证 handler
- authentication metadata populator
- execution plan configurer

### Provider

`EmailMultifactorAuthenticationProvider`

职责：

- 标识自身 id 为 `mfa-email`
- 提供 CAS MFA 流程中展示和识别所需的元数据

### Credential

`EmailTokenCredential`

职责：

- 承载用户输入的一次性验证码
- 作为邮箱验证码 MFA 的认证凭证类型

### Token Repository

`InMemoryEmailTokenRepository`

职责：

- 为每个 principal 保存一个当前有效验证码
- 跟踪验证码创建时间和过期时间
- 提供查询、校验、替换和消费能力

约束：

- 仅内存存储
- 仅当前节点可见
- 适用于本地开发和单节点演示环境

### Code Sender

定义 `EmailCodeSender` 接口，并提供 `LoggingEmailCodeSender` 实现。

职责：

- 抽象验证码投递方式
- 在当前版本中，把 principal id、目标邮箱地址、验证码写入日志

这样后续如果切换到真实 SMTP，只需要替换发送实现，不需要重做整个 MFA 流程。

### 发码 Action

`PrepareEmailCodeAction`

职责：

- 从 webflow context 中读取当前已认证 principal
- 从配置中解析目标邮箱
- 生成新的验证码
- 把验证码及过期时间写入 repository
- 调用 sender 记录投递日志

### Authentication Handler

`EmailTokenAuthenticationHandler`

职责：

- 支持 `EmailTokenCredential`
- 获取当前 MFA 事务关联的 principal 身份
- 用 repository 校验用户提交的验证码
- 对不存在、错误、过期、已消费的验证码进行拒绝
- 成功后消费验证码，防止重放

### Webflow

新增一个独立的 MFA flow 文件：

`webflow/mfa-email/mfa-email-webflow.xml`

计划中的状态：

- `initializeLoginForm`
- `sendCode`
- `viewLoginForm`
- `realSubmit`
- `success`

流程行为：

1. 初始化标准登录上下文
2. 生成验证码并写日志
3. 展示验证码输入页
4. 提交验证码，通过 one-time token webflow action 进入校验
5. 校验成功后完成 MFA

该流程不包含注册、可信设备或恢复机制。

### 页面视图

新增一个独立的 MFA 页面模板：

`templates/casEmailTokenLoginView.html`

职责：

- 展示验证码输入表单
- 把用户提交的值绑定到 `EmailTokenCredential`
- 沿用现有 CAS 默认模板风格

## 数据流

1. 用户完成主认证。
2. CAS 评估目标 service 的 MFA 策略。
3. 如果该 service 要求 `mfa-email`，CAS 进入 `mfa-email` 的 MFA flow。
4. 发码 action 从静态配置中解析当前用户邮箱。
5. 系统生成一个一次性验证码，并写入内存 repository。
6. logging sender 把目标邮箱和验证码写入应用日志。
7. 用户在 MFA 页面输入验证码。
8. authentication handler 针对当前 principal 校验验证码。
9. 校验成功后，repository 消费验证码，MFA 完成。

## 配置模型

第一版会在 overlay 中新增一组自定义配置，统一挂在 `dqv5.authn.mfa.email` 前缀下。

计划中的属性：

- `dqv5.authn.mfa.email.name`
- `dqv5.authn.mfa.email.code-length`
- `dqv5.authn.mfa.email.expiration-seconds`
- `dqv5.authn.mfa.email.accounts.<username>`

校验规则：

- 如果未配置 provider 名称，则默认使用 `mfa-email`
- 如果未配置验证码长度，则默认使用 6 位
- 如果未配置过期时间，则默认使用 300 秒
- 对于需要使用邮箱 MFA 的用户，必须配置邮箱映射

## Service 配置

如果要让某个 service 使用新 provider，对应的 registered service 定义必须引用 `mfa-email`。

示例：

```json
"multifactorPolicy" : {
  "@class" : "org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy",
  "multifactorAuthenticationProviders" : [ "java.util.LinkedHashSet", [ "mfa-email" ] ],
  "failureMode" : "CLOSED"
}
```

这与仍然指向 `mfa-gauth` 的 service 定义彼此独立，互不影响。

当前环境的 service registry 实际读取路径为 `file:D:\workspace_personal\cas-server-demos\301.services-json`。
该目录不在当前仓库内，因此启用 `mfa-email` 时需要手工修改外部 service JSON，或在获得明确授权后再对该目录执行变更。

## 错误处理

### 没有邮箱映射

- 记录包含 principal id 的清晰错误日志
- 使 MFA 流程失败

### 验证码错误

- 拒绝认证
- 返回验证码输入页面

### 验证码过期

- 拒绝认证
- 返回验证码输入页面

### 验证码重复使用

- 拒绝认证
- 不允许重放

### 重新进入 MFA 流程

- 生成一个新的验证码
- 替换该 principal 之前仍然有效的旧验证码

## 安全和运行约束

这一版明确只服务于本地演示环境，因此有意保持简单。

已知限制：

- 验证码以明文形式写入日志
- repository 仅存在于当前进程内存中
- 没有发送频率限制和重试限制
- 没有额外的审计模型，只有普通日志

这些限制在当前本地测试目标下是可接受的，并明确属于本轮范围之外。

## 测试策略

实现时应尽量遵循 test-first。

必须覆盖：

- 对于已配置邮箱映射的用户，能够生成验证码并写入 repository
- 用户没有邮箱映射时会失败
- 用户提交正确验证码时认证成功
- 用户提交错误验证码时认证失败
- 用户提交已过期验证码时认证失败
- 用户提交已消费验证码时认证失败
- 新验证码生成时会替换旧验证码

验证目标：

- 项目可以成功编译
- 自定义 bean 可以成功装配到 overlay 中
- registered service 策略可以正确指向 `mfa-email`
- 现有 `mfa-gauth` 依然可用

## 验收标准

- 当某个 registered service 配置为 `mfa-email` 时，用户在主认证后会跳转到邮箱验证码页面
- MFA 页面能正常展示验证码输入框
- 应用日志中能看到当前 principal 对应的目标邮箱和验证码
- 输入正确验证码后可以完成 MFA
- 错误、过期、重复使用的验证码都会失败
- 现有配置为 `mfa-gauth` 的 service 仍然可以正常工作
