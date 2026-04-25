package com.dqv5.cas.mfa.email.webflow;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasMultifactorWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class EmailMultifactorWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {
    private final FlowDefinitionRegistry flowDefinitionRegistry;
    private final String providerId;

    public EmailMultifactorWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                             final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                             final FlowDefinitionRegistry flowDefinitionRegistry,
                                             final ApplicationContext applicationContext,
                                             final CasConfigurationProperties casProperties,
                                             final String providerId) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
        this.flowDefinitionRegistry = flowDefinitionRegistry;
        this.providerId = providerId;
    }

    @Override
    protected void doInitialize() {
        registerMultifactorProviderAuthenticationWebflow(getLoginFlow(), providerId, flowDefinitionRegistry, providerId);
    }
}
