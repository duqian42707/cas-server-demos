# MFA Email Design

## Overview

This design adds a new independent multifactor authentication provider named `mfa-email` to the CAS 5.3 overlay.

The provider sends a one-time verification code to the user's email address. In this first version, email delivery is simulated by writing the destination email address and verification code to the application log. The user's email address is resolved from a local static mapping in configuration.

This provider is independent from `mfa-gauth`. Existing Google Authenticator MFA behavior remains available and unchanged.

## Goals

- Add a new MFA provider with id `mfa-email`
- Trigger the provider through registered service MFA policy
- Resolve user email from local configuration
- Generate a short-lived one-time code
- Log the target email address and code instead of sending real email
- Prompt the user for the code in a dedicated MFA screen
- Validate the code and complete MFA on success

## Non-Goals

- Real SMTP delivery
- Self-service email enrollment or registration
- Cluster-safe or persistent token storage
- User attribute, principal attribute, or global MFA triggering
- Resend-code UX
- Rate limiting, anti-abuse controls, or lockout policies

## Approaches Considered

### Approach 1: Reuse built-in simple MFA

Use a built-in simple MFA mechanism and adapt it to look like email MFA.

Pros:

- Less code
- More CAS behavior already exists

Cons:

- Provider identity and behavior are less explicit
- Harder to make the result a clean standalone `mfa-email`
- More adaptation needed around token generation and delivery semantics

### Approach 2: Create a standalone `mfa-email` provider

Create a separate provider, credential type, handler, actions, views, and token repository for email verification.

Pros:

- Clean provider identity: `mfa-email`
- Clear ownership of flow and configuration
- Easy to extend later to real email delivery
- No leakage from `mfa-gauth` or TOTP semantics

Cons:

- More code to add now

### Approach 3: Fork `mfa-gauth` behavior

Copy and modify the Google Authenticator flow to behave like email MFA.

Pros:

- Existing reference inside the same CAS version

Cons:

- Pulls in registration and TOTP-oriented semantics that do not belong
- Harder to maintain cleanly
- Higher long-term maintenance cost

### Decision

Use Approach 2 and build a standalone `mfa-email` provider.

## Triggering Model

`mfa-email` is triggered by registered service MFA policy only.

The service definition must reference `mfa-email` in `multifactorAuthenticationProviders`. This means:

- requests with a `service` parameter only trigger `mfa-email` when the selected service matches a registered service with that MFA policy
- requests without a matching service do not trigger `mfa-email`
- existing `mfa-gauth` service policies can coexist unchanged

## User Email Source

The first version resolves the user's email address from static configuration.

Example structure:

```properties
dqv5.authn.mfa.email.name=mfa-email
dqv5.authn.mfa.email.code-length=6
dqv5.authn.mfa.email.expiration-seconds=300
dqv5.authn.mfa.email.accounts.casuser=casuser@example.com
```

Behavior:

- the username from the authenticated CAS principal is used as the lookup key
- if the mapping exists, the email MFA flow continues
- if the mapping does not exist, the MFA flow fails and the reason is logged

## Architecture

The implementation adds a small independent set of components under the overlay source tree.

### Configuration

A dedicated Spring `@Configuration` class registers:

- the `mfa-email` provider bean
- the email token repository
- the code generator
- the logging-based email sender
- the MFA webflow configurer
- the send-code action
- the authentication handler
- the authentication metadata populator
- the execution plan configurer

### Provider

`EmailMultifactorAuthenticationProvider`

Responsibilities:

- identify itself as `mfa-email`
- provide friendly display metadata for CAS MFA selection and processing

### Credential

`EmailTokenCredential`

Responsibilities:

- carry the one-time verification code entered by the user
- serve as the credential type supported by the email MFA authentication handler

### Token Repository

`InMemoryEmailTokenRepository`

Responsibilities:

- store one active code per principal
- track creation and expiration timestamps
- support lookup, validation, replacement, and consumption

Constraints:

- in-memory only
- node-local only
- suitable for local development and single-node demos

### Code Sender

`EmailCodeSender` interface with `LoggingEmailCodeSender` implementation

Responsibilities:

- abstract the delivery mechanism
- in this version, log the principal id, target email address, and generated code

This keeps delivery logic replaceable for a later SMTP implementation.

### Send-Code Action

`PrepareEmailCodeAction`

Responsibilities:

- read the current authenticated principal from webflow context
- resolve the target email from configuration
- generate a new code
- write the code into the repository with expiration
- call the sender to log the delivery event

### Authentication Handler

`EmailTokenAuthenticationHandler`

Responsibilities:

- support `EmailTokenCredential`
- fetch the current principal identity associated with the MFA transaction
- validate the submitted code against the repository
- reject missing, invalid, expired, or already-consumed codes
- consume the code on success

### Webflow

A dedicated MFA flow file is added:

`webflow/mfa-email/mfa-email-webflow.xml`

Planned states:

- `initializeLoginForm`
- `sendCode`
- `viewLoginForm`
- `realSubmit`
- `success`

Flow behavior:

1. initialize the standard login context
2. generate and log a code
3. show the verification page
4. submit the entered code through the one-time token webflow action
5. complete MFA on success

This flow does not include registration, device trust, or recovery options.

### View

A dedicated template is added for the MFA page:

`templates/casEmailTokenLoginView.html`

Responsibilities:

- display a simple prompt to enter the verification code
- bind the submitted value to `EmailTokenCredential`
- follow the existing CAS login template style

## Data Flow

1. The user completes primary authentication.
2. CAS evaluates the target service MFA policy.
3. If the service requires `mfa-email`, CAS transitions into the `mfa-email` MFA flow.
4. The send-code action resolves the user's email from static configuration.
5. A one-time code is generated and stored in the in-memory repository.
6. The logging sender writes the target email and code to the application log.
7. The user enters the code in the MFA page.
8. The authentication handler validates the code for the current principal.
9. On success, the repository consumes the code and the MFA flow completes.

## Configuration Model

The first version adds custom overlay-specific properties under a `dqv5.authn.mfa.email` prefix.

Planned properties:

- `dqv5.authn.mfa.email.name`
- `dqv5.authn.mfa.email.code-length`
- `dqv5.authn.mfa.email.expiration-seconds`
- `dqv5.authn.mfa.email.accounts.<username>`

Validation rules:

- provider name defaults to `mfa-email` if omitted
- code length defaults to 6 if omitted
- expiration defaults to 300 seconds if omitted
- account mappings are required for users who must use email MFA

## Service Configuration

To enable the new provider for a service, its registered service definition must reference `mfa-email`.

Example:

```json
"multifactorPolicy" : {
  "@class" : "org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy",
  "multifactorAuthenticationProviders" : [ "java.util.LinkedHashSet", [ "mfa-email" ] ],
  "failureMode" : "CLOSED"
}
```

This remains independent from any service definitions that still point to `mfa-gauth`.

## Error Handling

### No email mapping

- log a clear error with the principal id
- fail the MFA flow

### Invalid code

- reject authentication
- return the user to the code-entry page

### Expired code

- reject authentication
- return the user to the code-entry page

### Reused code

- reject authentication
- do not allow replay

### Re-entering the flow

- generate a new code
- replace the previously active code for that principal

## Security and Operational Constraints

This version is intentionally limited for a demo environment.

Known limitations:

- codes are logged in plaintext
- repository is process-local and in-memory
- no throttling or resend limits
- no audit model beyond normal logging

These limitations are acceptable for the current local test objective and are explicitly out of scope for this iteration.

## Testing Strategy

Implementation should be test-first where practical.

Required coverage:

- code generation and repository storage for a mapped user
- failure when no email mapping exists
- success when the submitted code matches the active code
- failure when the submitted code is wrong
- failure when the submitted code is expired
- failure when the submitted code has already been consumed
- replacement of old code when a new code is generated

Verification targets:

- project compiles successfully
- custom beans load into the overlay
- registered service policy can point to `mfa-email`
- existing `mfa-gauth` support remains available

## Acceptance Criteria

- A registered service configured for `mfa-email` redirects to the email MFA page after primary authentication.
- The MFA page displays a code input form.
- Application logs include the destination email address and verification code for the current principal.
- A correct code completes MFA.
- Invalid, expired, or replayed codes fail.
- Existing `mfa-gauth` flows still work for services configured to use them.
