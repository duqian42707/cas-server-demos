package com.dqv5.cas.mfa.email.config;

import com.dqv5.cas.mfa.email.webflow.EmailMultifactorWebflowConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

@Configuration
public class EmailMfaWebflowConfiguration implements CasWebflowExecutionPlanConfigurer {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Autowired
    private EmailMfaProperties emailMfaProperties;

    @Bean
    public FlowDefinitionRegistry emailMfaFlowRegistry() {
        FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices);
        builder.setBasePath("classpath:/webflow");
        builder.addFlowLocationPattern("/mfa-email/*-webflow.xml");
        return builder.build();
    }

    @Bean
    public CasWebflowConfigurer emailMfaMultifactorWebflowConfigurer() {
        return new EmailMultifactorWebflowConfigurer(
            flowBuilderServices,
            loginFlowDefinitionRegistry,
            emailMfaFlowRegistry(),
            applicationContext,
            casProperties,
            emailMfaProperties.getName()
        );
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(emailMfaMultifactorWebflowConfigurer());
    }
}
