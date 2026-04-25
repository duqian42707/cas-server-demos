package com.dqv5.cas.mfa.email.provider;

import org.junit.Test;

import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;

public class EmailMultifactorAuthenticationProviderTests {
    @Test
    public void shouldExposeBypassEvaluator() throws Exception {
        System.setProperty("catalina.home", Files.createTempDirectory("cas-test-catalina").toString());
        EmailMultifactorAuthenticationProvider provider = new EmailMultifactorAuthenticationProvider("mfa-email");
        assertNotNull(provider.getBypassEvaluator());
    }
}
