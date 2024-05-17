package com.dqv5.cas.authentication.method2;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class M2AuthenticationConfiguration {

    @Bean
    public AuthenticationHandler m2AuthenticationHandler(
            @Qualifier("servicesManager") ServicesManager servicesManager,
            final CasConfigurationProperties casProperties
    ) {

        var handler = new M2AuthenticationHandler(
                M2AuthenticationHandler.class.getSimpleName(),
                servicesManager, new DefaultPrincipalFactory(), 2
        );
        /*
            Configure the handler by invoking various setter methods, etc.
            Note that you also have full access to the collection of resolved CAS settings.
            Note that each authentication handler may optionally qualify for an 'order`
            as well as a unique name.
        */
        return handler;
    }

    @Bean
    public AuthenticationEventExecutionPlanConfigurer m2Plan(
            @Qualifier("m2AuthenticationHandler") final AuthenticationHandler handler
    ) {
        return plan -> plan.registerAuthenticationHandler(handler);
    }
}
