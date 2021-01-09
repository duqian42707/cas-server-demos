package com.dqv5.cas;

import com.sun.nio.sctp.HandlerResult;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    private final Logger log = LoggerFactory.getLogger(MyAuthenticationHandler.class);

    public MyAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential, final String originalPassword) throws GeneralSecurityException, PreventedException {
        String username = credential.getUsername();
        String password = credential.getPassword();
        boolean authPass = "admin".equals(username) && "pass123".equals(password);
        log.info("自定义认证，用户名：{}，密码：{}，认证通过：{}", username, password, authPass);
        if (authPass) {
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username), new ArrayList<>());
        }
        throw new FailedLoginException("Sorry, you are a failure!");
    }
}
