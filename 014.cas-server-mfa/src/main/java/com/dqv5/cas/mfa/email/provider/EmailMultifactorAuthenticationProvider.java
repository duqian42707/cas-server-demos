package com.dqv5.cas.mfa.email.provider;

import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;
import org.apereo.cas.authentication.DefaultMultifactorAuthenticationProviderBypass;
import org.apereo.cas.configuration.model.support.mfa.MultifactorAuthenticationProviderBypassProperties;

public class EmailMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {
    public EmailMultifactorAuthenticationProvider(final String id) {
        setId(id);
        setBypassEvaluator(new DefaultMultifactorAuthenticationProviderBypass(
            new MultifactorAuthenticationProviderBypassProperties()
        ));
    }

    @Override
    public String getFriendlyName() {
        return "Email Verification";
    }
}
