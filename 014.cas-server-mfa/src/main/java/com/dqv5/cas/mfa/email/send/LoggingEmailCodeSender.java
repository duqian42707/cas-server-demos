package com.dqv5.cas.mfa.email.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEmailCodeSender implements EmailCodeSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEmailCodeSender.class);

    @Override
    public void send(final String principalId, final String emailAddress, final String code) {
        LOGGER.info("Email MFA code for [{}] -> [{}] is [{}]", principalId, emailAddress, code);
    }
}
