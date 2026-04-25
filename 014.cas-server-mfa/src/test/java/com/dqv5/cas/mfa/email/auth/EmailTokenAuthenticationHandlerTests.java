package com.dqv5.cas.mfa.email.auth;

import com.dqv5.cas.mfa.email.credential.EmailTokenCredential;
import com.dqv5.cas.mfa.email.token.EmailCodeToken;
import com.dqv5.cas.mfa.email.token.InMemoryEmailTokenRepository;
import org.apereo.cas.authentication.AuthenticationBuilder;
import org.apereo.cas.authentication.DefaultAuthenticationBuilder;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

public class EmailTokenAuthenticationHandlerTests {
    @Test
    public void shouldAuthenticateMatchingCodeForCurrentPrincipal() throws Exception {
        System.setProperty("catalina.home", Files.createTempDirectory("cas-test-catalina").toString());
        Clock clock = Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC);
        InMemoryEmailTokenRepository repository = new InMemoryEmailTokenRepository(clock);
        repository.store(new EmailCodeToken("casuser", "123456", Instant.now(clock), Instant.now(clock).plusSeconds(300)));

        MockRequestContext context = new MockRequestContext();
        RequestContextHolder.setRequestContext(context);
        AuthenticationBuilder builder = new DefaultAuthenticationBuilder(new DefaultPrincipalFactory().createPrincipal("casuser"));
        WebUtils.putAuthentication(builder.build(), context);

        EmailTokenAuthenticationHandler handler =
            new EmailTokenAuthenticationHandler("emailHandler", null, new DefaultPrincipalFactory(), repository);
        assertNotNull(handler.authenticate(new EmailTokenCredential("123456")));
        assertFalse(repository.matches("casuser", "123456"));
    }
}
