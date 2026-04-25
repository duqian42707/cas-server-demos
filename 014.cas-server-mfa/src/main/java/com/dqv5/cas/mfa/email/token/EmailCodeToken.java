package com.dqv5.cas.mfa.email.token;

import java.time.Instant;

public class EmailCodeToken {
    private final String principalId;
    private final String code;
    private final Instant createdAt;
    private final Instant expiresAt;

    public EmailCodeToken(final String principalId, final String code, final Instant createdAt, final Instant expiresAt) {
        this.principalId = principalId;
        this.code = code;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public String getCode() {
        return code;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
