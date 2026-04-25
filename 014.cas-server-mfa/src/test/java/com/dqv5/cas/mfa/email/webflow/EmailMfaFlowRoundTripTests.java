package com.dqv5.cas.mfa.email.webflow;

import com.dqv5.cas.mfa.email.auth.EmailTokenAuthenticationHandler;
import com.dqv5.cas.mfa.email.config.EmailMfaProperties;
import com.dqv5.cas.mfa.email.credential.EmailTokenCredential;
import com.dqv5.cas.mfa.email.send.EmailCodeSender;
import com.dqv5.cas.mfa.email.token.EmailCodeGenerator;
import com.dqv5.cas.mfa.email.token.InMemoryEmailTokenRepository;
import org.apereo.cas.authentication.DefaultAuthenticationBuilder;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.web.support.WebUtils;
import org.junit.Test;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EmailMfaFlowRoundTripTests {
    @Test
    public void shouldAuthenticateCodeGeneratedByPrepareAction() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC);
        EmailMfaProperties properties = new EmailMfaProperties();
        properties.setAccounts(Collections.singletonMap("casuser", "casuser@example.com"));
        InMemoryEmailTokenRepository repository = new InMemoryEmailTokenRepository(clock);
        AtomicReference<String> sentCode = new AtomicReference<>();
        EmailCodeSender sender = (principalId, emailAddress, code) -> sentCode.set(code);
        EmailCodeGenerator generator = new EmailCodeGenerator() {
            @Override
            public String generate(final int length) {
                return "123456";
            }
        };

        MockRequestContext context = new MockRequestContext();
        RequestContextHolder.setRequestContext(context);
        WebUtils.putAuthentication(
            new DefaultAuthenticationBuilder(new DefaultPrincipalFactory().createPrincipal("casuser")).build(),
            context
        );

        PrepareEmailCodeAction action = new PrepareEmailCodeAction(properties, repository, generator, sender, clock);
        action.execute(context);

        assertEquals("123456", sentCode.get());

        WebUtils.putCredential(context, new EmailTokenCredential("123456"));
        EmailTokenAuthenticationHandler handler =
            new EmailTokenAuthenticationHandler("emailHandler", null, new DefaultPrincipalFactory(), repository);

        assertNotNull(handler.authenticate(new EmailTokenCredential("123456")));
    }
}
