package com.dqv5.cas.mfa.email.config;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EmailMfaPropertiesTests {
    @Test
    public void shouldUseDefaultsAndResolveAccounts() {
        EmailMfaProperties properties = new EmailMfaProperties();
        HashMap<String, String> accounts = new HashMap<>();
        accounts.put("casuser", "casuser@example.com");
        properties.setAccounts(accounts);

        assertEquals("mfa-email", properties.getName());
        assertEquals(6, properties.getCodeLength());
        assertEquals(300, properties.getExpirationSeconds());
        assertEquals("casuser@example.com", properties.findEmail("casuser"));
        assertNull(properties.findEmail("missing"));
    }
}
