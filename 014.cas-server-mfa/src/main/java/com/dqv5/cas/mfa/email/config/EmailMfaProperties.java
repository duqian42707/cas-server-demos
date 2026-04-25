package com.dqv5.cas.mfa.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "dqv5.authn.mfa.email")
public class EmailMfaProperties {
    private String name = "mfa-email";
    private int codeLength = 6;
    private int expirationSeconds = 300;
    private Map<String, String> accounts = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(final int codeLength) {
        this.codeLength = codeLength;
    }

    public int getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(final int expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public Map<String, String> getAccounts() {
        return accounts;
    }

    public void setAccounts(final Map<String, String> accounts) {
        this.accounts = accounts;
    }

    public String findEmail(final String principalId) {
        return accounts.get(principalId);
    }
}
