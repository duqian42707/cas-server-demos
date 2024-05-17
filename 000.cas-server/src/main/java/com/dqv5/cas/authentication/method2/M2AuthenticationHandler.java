package com.dqv5.cas.authentication.method2;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author duq
 * @date 2024/5/18
 */
public class M2AuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    protected M2AuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
            final UsernamePasswordCredential credential,
            final String originalPassword) throws Throwable {
        String username = credential.getUsername();
        char[] password = credential.getPassword();

        if (!Objects.equals(username, "admin2")) {
            throw new FailedLoginException();
        }

        if (!Objects.equals(originalPassword, "234234")) {
            throw new FailedLoginException("Sorry, Login failed!");
        }

        return createHandlerResult(credential, principalFactory.createPrincipal(username), new ArrayList<>());
    }
}
