package com.dqv5.cas.mfa.email.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;

public class EmailMfaWebflowConfigurationTests {
    @Test
    public void shouldInjectLoginFlowRegistryByQualifier() throws Exception {
        Field field = EmailMfaWebflowConfiguration.class.getDeclaredField("loginFlowDefinitionRegistry");
        Qualifier qualifier = field.getAnnotation(Qualifier.class);
        assertNotNull(qualifier);
        org.junit.Assert.assertEquals("loginFlowRegistry", qualifier.value());
        assertNotNull(field.getAnnotation(Autowired.class));
    }
}
