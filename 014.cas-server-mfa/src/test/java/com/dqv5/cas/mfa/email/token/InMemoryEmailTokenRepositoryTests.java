package com.dqv5.cas.mfa.email.token;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InMemoryEmailTokenRepositoryTests {
    @Test
    public void shouldStoreAndConsumeValidCode() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC);
        InMemoryEmailTokenRepository repository = new InMemoryEmailTokenRepository(clock);
        repository.store(new EmailCodeToken("casuser", "123456", Instant.now(clock), Instant.now(clock).plusSeconds(300)));

        assertTrue(repository.matches("casuser", "123456"));
        assertTrue(repository.consume("casuser", "123456"));
        assertFalse(repository.matches("casuser", "123456"));
    }

    @Test
    public void shouldRejectExpiredAndReplacedCodes() {
        Clock clock = Clock.fixed(Instant.parse("2026-04-24T00:00:00Z"), ZoneOffset.UTC);
        InMemoryEmailTokenRepository repository = new InMemoryEmailTokenRepository(clock);
        repository.store(new EmailCodeToken("casuser", "111111", Instant.now(clock), Instant.now(clock).plusSeconds(300)));
        repository.store(new EmailCodeToken("casuser", "222222", Instant.now(clock), Instant.now(clock).plusSeconds(300)));

        assertFalse(repository.matches("casuser", "111111"));
        assertTrue(repository.matches("casuser", "222222"));
    }
}
