package com.dqv5.cas.mfa.email.auth;

import com.dqv5.cas.mfa.email.credential.EmailTokenCredential;
import com.dqv5.cas.mfa.email.token.InMemoryEmailTokenRepository;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.support.WebUtils;

import javax.security.auth.login.FailedLoginException;

public class EmailTokenAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private final InMemoryEmailTokenRepository repository;

    public EmailTokenAuthenticationHandler(final String name,
                                           final ServicesManager servicesManager,
                                           final PrincipalFactory principalFactory,
                                           final InMemoryEmailTokenRepository repository) {
        super(name, servicesManager, principalFactory, null);
        this.repository = repository;
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential) throws FailedLoginException {
        EmailTokenCredential emailCredential = (EmailTokenCredential) credential;
        String principalId = WebUtils.getInProgressAuthentication().getPrincipal().getId();
        if (!repository.consume(principalId, emailCredential.getToken())) {
            throw new FailedLoginException("Invalid email MFA code");
        }
        return createHandlerResult(emailCredential, principalFactory.createPrincipal(principalId));
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof EmailTokenCredential;
    }
}
