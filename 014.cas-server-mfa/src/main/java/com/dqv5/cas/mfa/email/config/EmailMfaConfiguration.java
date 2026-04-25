package com.dqv5.cas.mfa.email.config;

import com.dqv5.cas.mfa.email.auth.EmailTokenAuthenticationHandler;
import com.dqv5.cas.mfa.email.credential.EmailTokenCredential;
import com.dqv5.cas.mfa.email.provider.EmailMultifactorAuthenticationProvider;
import com.dqv5.cas.mfa.email.send.EmailCodeSender;
import com.dqv5.cas.mfa.email.send.LoggingEmailCodeSender;
import com.dqv5.cas.mfa.email.token.EmailCodeGenerator;
import com.dqv5.cas.mfa.email.token.InMemoryEmailTokenRepository;
import com.dqv5.cas.mfa.email.webflow.PrepareEmailCodeAction;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.ByCredentialTypeAuthenticationHandlerResolver;
import org.apereo.cas.authentication.metadata.AuthenticationContextAttributeMetaDataPopulator;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.MultifactorAuthenticationProvider;
import org.apereo.cas.services.ServicesManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(EmailMfaProperties.class)
public class EmailMfaConfiguration {
    @Bean
    public Clock emailMfaClock() {
        return Clock.systemUTC();
    }

    @Bean
    public InMemoryEmailTokenRepository emailTokenRepository(final Clock emailMfaClock) {
        return new InMemoryEmailTokenRepository(emailMfaClock);
    }

    @Bean
    public EmailCodeGenerator emailCodeGenerator() {
        return new EmailCodeGenerator();
    }

    @Bean
    public EmailCodeSender emailCodeSender() {
        return new LoggingEmailCodeSender();
    }

    @Bean
    public MultifactorAuthenticationProvider emailAuthenticationProvider(final EmailMfaProperties emailMfaProperties) {
        return new EmailMultifactorAuthenticationProvider(emailMfaProperties.getName());
    }

    @Bean
    public EmailTokenAuthenticationHandler emailTokenAuthenticationHandler(final ServicesManager servicesManager,
                                                                           final PrincipalFactory principalFactory,
                                                                           final InMemoryEmailTokenRepository emailTokenRepository) {
        return new EmailTokenAuthenticationHandler(
            "emailTokenAuthenticationHandler",
            servicesManager,
            principalFactory,
            emailTokenRepository
        );
    }

    @Bean
    public AuthenticationContextAttributeMetaDataPopulator emailAuthenticationMetaDataPopulator(
        final EmailTokenAuthenticationHandler handler,
        final EmailMfaProperties properties,
        final CasConfigurationProperties casProperties) {
        return new AuthenticationContextAttributeMetaDataPopulator(
            casProperties.getAuthn().getMfa().getAuthenticationContextAttribute(),
            handler,
            properties.getName()
        );
    }

    @Bean
    public PrepareEmailCodeAction prepareEmailCodeAction(final EmailMfaProperties properties,
                                                         final InMemoryEmailTokenRepository repository,
                                                         final EmailCodeGenerator generator,
                                                         final EmailCodeSender sender,
                                                         final Clock clock) {
        return new PrepareEmailCodeAction(properties, repository, generator, sender, clock);
    }

    @Bean
    public AuthenticationEventExecutionPlanConfigurer emailAuthenticationEventExecutionPlanConfigurer(
        final EmailTokenAuthenticationHandler handler,
        final AuthenticationContextAttributeMetaDataPopulator populator) {
        return plan -> {
            plan.registerAuthenticationHandler(handler);
            plan.registerMetadataPopulator(populator);
            plan.registerAuthenticationHandlerResolver(
                new ByCredentialTypeAuthenticationHandlerResolver(EmailTokenCredential.class)
            );
        };
    }
}
