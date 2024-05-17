package com.dqv5.cas.authentication.method1;

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
public class M1AuthenticationConfiguration {

    @Bean
    public AuthenticationHandler m1AuthenticationHandler(
            @Qualifier("servicesManager") ServicesManager servicesManager,
            final CasConfigurationProperties casProperties
    ) {

        var handler = new M1AuthenticationHandler(
                M1AuthenticationHandler.class.getSimpleName(),
                servicesManager, new DefaultPrincipalFactory(), 1
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
    public AuthenticationEventExecutionPlanConfigurer m1Plan(
            @Qualifier("m1AuthenticationHandler") final AuthenticationHandler handler
    ) {
        return plan -> plan.registerAuthenticationHandler(handler);
    }
}
