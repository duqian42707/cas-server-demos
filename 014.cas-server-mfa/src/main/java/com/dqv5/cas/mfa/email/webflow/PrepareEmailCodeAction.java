package com.dqv5.cas.mfa.email.webflow;

import com.dqv5.cas.mfa.email.config.EmailMfaProperties;
import com.dqv5.cas.mfa.email.send.EmailCodeSender;
import com.dqv5.cas.mfa.email.token.EmailCodeGenerator;
import com.dqv5.cas.mfa.email.token.EmailCodeToken;
import com.dqv5.cas.mfa.email.token.InMemoryEmailTokenRepository;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.time.Clock;
import java.time.Instant;

public class PrepareEmailCodeAction extends AbstractAction {
    private final EmailMfaProperties properties;
    private final InMemoryEmailTokenRepository repository;
    private final EmailCodeGenerator generator;
    private final EmailCodeSender sender;
    private final Clock clock;

    public PrepareEmailCodeAction(final EmailMfaProperties properties,
                                  final InMemoryEmailTokenRepository repository,
                                  final EmailCodeGenerator generator,
                                  final EmailCodeSender sender,
                                  final Clock clock) {
        this.properties = properties;
        this.repository = repository;
        this.generator = generator;
        this.sender = sender;
        this.clock = clock;
    }

    @Override
    protected Event doExecute(final RequestContext context) {
        String principalId = WebUtils.getAuthentication(context).getPrincipal().getId();
        String email = properties.findEmail(principalId);
        if (email == null) {
            throw new IllegalStateException("No email mapping found for " + principalId);
        }
        Instant now = Instant.now(clock);
        String code = generator.generate(properties.getCodeLength());
        repository.store(new EmailCodeToken(principalId, code, now, now.plusSeconds(properties.getExpirationSeconds())));
        sender.send(principalId, email, code);
        return success();
    }
}
