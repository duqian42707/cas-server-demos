package com.dqv5.cas;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class MyAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

    private final Logger log = LoggerFactory.getLogger(MyAuthenticationHandler.class);

    public MyAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        UsernamePasswordMsgcodeCredential myCredential = (UsernamePasswordMsgcodeCredential) credential;
        String username = myCredential.getUsername();
        String password = myCredential.getPassword();
        String msgcode = myCredential.getMsgcode();
        boolean authPass = "admin".equals(username) && "pass123".equals(password) && "751481".equals(msgcode);
        log.info("自定义认证，用户名：{}，密码：{}，验证码：{}, 认证通过：{}", username, password, msgcode, authPass);
        if (authPass) {
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username), new ArrayList<>());
        }
        throw new FailedLoginException("Sorry, you are a failure!");
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordMsgcodeCredential;
    }
}
