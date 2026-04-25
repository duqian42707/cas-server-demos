package com.dqv5.cas.mfa.email.provider;

import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;

public class EmailMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {
    private final String id;

    public EmailMultifactorAuthenticationProvider(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getFriendlyName() {
        return "Email Verification";
    }
}
