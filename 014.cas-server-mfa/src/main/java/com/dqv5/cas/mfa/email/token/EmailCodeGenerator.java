package com.dqv5.cas.mfa.email.token;

import java.security.SecureRandom;

public class EmailCodeGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generate(final int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
