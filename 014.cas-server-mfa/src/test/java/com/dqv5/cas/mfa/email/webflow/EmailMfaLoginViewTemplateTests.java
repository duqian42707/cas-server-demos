package com.dqv5.cas.mfa.email.webflow;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EmailMfaLoginViewTemplateTests {
    @Test
    public void shouldPostBackIntoCurrentWebflowExecution() throws Exception {
        String template = new String(
            Files.readAllBytes(Paths.get("src/main/resources/templates/casEmailTokenLoginView.html")),
            StandardCharsets.UTF_8
        );

        assertTrue(template.contains("name=\"_eventId_submit\""));
        assertTrue(template.contains("name=\"execution\""));
        assertTrue(template.contains("th:value=\"${flowExecutionKey}\""));
    }
}
