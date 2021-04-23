# CAS 的认证接口 restul api

## 1. 获取TGT票据

```text
POST /cas/v1/tickets HTTP/1.0
'Content-type': 'Application/x-www-form-urlencoded'
username=battags&password=password&additionalParam1=paramvalue
```

登录成功后返回

```text
TGT-4-5SOnZbg2g91b3wYPFiZDLB0-phu3UruOW3fhd2PgdjdejDKs7CpZ48t-UzIYMmWdWM4duqian-MacBookPro
```

## 2. 获取ST

```text
POST /cas/v1/tickets/{TGT id}

service={form encoded parameter for the service url}
```

成功后返回

```text
ST-4-7xd9XRHIT1lfhJbSFsEl3--dSZ4duqian-MacBookPro
```


## 3. 通过ST票据去访问服务

用获取到的ST拼接到客户端地址栏后面即可

浏览器访问`https://demo.cas-client.com/?ticket=ST-4-7xd9XRHIT1lfhJbSFsEl3--dSZ4duqian-MacBookPro`


## 参考文章

https://apereo.github.io/cas/5.3.x/protocol/REST-Protocol.html
https://blog.csdn.net/Anumbrella/article/details/88912964
