package com.dqv5.cas.mfa.email.token;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEmailTokenRepository {
    private final Map<String, EmailCodeToken> tokens = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryEmailTokenRepository(final Clock clock) {
        this.clock = clock;
    }

    public void store(final EmailCodeToken token) {
        tokens.put(token.getPrincipalId(), token);
    }

    public boolean matches(final String principalId, final String code) {
        EmailCodeToken token = tokens.get(principalId);
        return token != null
            && token.getCode().equals(code)
            && token.getExpiresAt().isAfter(Instant.now(clock));
    }

    public boolean consume(final String principalId, final String code) {
        if (!matches(principalId, code)) {
            return false;
        }
        tokens.remove(principalId);
        return true;
    }
}
