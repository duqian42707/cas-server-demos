package com.dqv5.cas.mfa.email.send;

public interface EmailCodeSender {
    void send(String principalId, String emailAddress, String code);
}
