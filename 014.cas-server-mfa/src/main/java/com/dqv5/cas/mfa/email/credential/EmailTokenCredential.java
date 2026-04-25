package com.dqv5.cas.mfa.email.credential;

import org.apereo.cas.authentication.OneTimeTokenCredential;

public class EmailTokenCredential extends OneTimeTokenCredential {
    public EmailTokenCredential() {
    }

    public EmailTokenCredential(final String token) {
        super(token);
    }
}
