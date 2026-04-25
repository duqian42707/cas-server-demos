package com.dqv5.cas.mfa.email.webflow;

import com.dqv5.cas.mfa.email.config.EmailMfaProperties;
import com.dqv5.cas.mfa.email.send.EmailCodeSender;
import com.dqv5.cas.mfa.email.token.EmailCodeGenerator;
import com.dqv5.cas.mfa.email.token.InMemoryEmailTokenRepository;
import org.apereo.cas.authentication.DefaultAuthenticationBuilder;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrepareEmailCodeActionTests {
    @Test
    public void shouldGenerateAndSendCodeForMappedPrincipal() throws Exception {
        System.setProperty("catalina.home", Files.createTempDirectory("cas-test-catalina").toString());
        EmailMfaProperties properties = new EmailMfaProperties();
        properties.setAccounts(Collections.singletonMap("casuser", "casuser@example.com"));
        InMemoryEmailTokenRepository repository =
            new InMemoryEmailTokenRepository(Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC));
        AtomicReference<String> sentCode = new AtomicReference<>();
        EmailCodeSender sender = (principalId, emailAddress, code) -> sentCode.set(code);
        EmailCodeGenerator generator = new EmailCodeGenerator() {
            @Override
            public String generate(final int length) {
                return "123456";
            }
        };

        MockRequestContext context = new MockRequestContext();
        WebUtils.putAuthentication(
            new DefaultAuthenticationBuilder(new DefaultPrincipalFactory().createPrincipal("casuser")).build(),
            context
        );
        PrepareEmailCodeAction action = new PrepareEmailCodeAction(
            properties,
            repository,
            generator,
            sender,
            Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC)
        );

        Event event = action.execute(context);
        assertEquals("success", event.getId());
        assertEquals("123456", sentCode.get());
        assertTrue(repository.matches("casuser", "123456"));
    }
}
