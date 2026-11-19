# Synthetic Balance Enquiry Service

This repository is a runnable Spring Boot test service for the advisory
`BANK-MICROSERVICES-REVIEW` standard, version `1.1.0`, whose approval state remains
`DRAFT_FOR_OWNER_APPROVAL`. All accounts and values are synthetic.

## Business capability

`POST /api/v1/balances/enquiry` performs a synchronous, read-only balance enquiry. It reads one
in-memory fixture and does not post payments or mutate a ledger. The service has no external
HTTP dependencies, database, message broker, scheduled jobs, cache, or production endpoint.
It is designed as a single Spring Boot instance; deployment topology, cross-site failover, and
active-active behavior are intentionally not asserted.

The absence of state changes makes duplicate financial effects and scheduled-worker controls
not applicable to this operation. `MS-AA-002`, `MS-AA-008`, and `MS-AA-010` still require
deployment/architecture evidence from the service owner before any broader active-active claim.

## Run locally

Prerequisites: JDK 17 or newer and Maven 3.9 or newer.

```bash
mvn clean verify
mvn spring-boot:run
```

Example request (the token is a deliberately fake placeholder and is never logged):

```bash
curl -i http://localhost:8080/api/v1/balances/enquiry \
  -H 'Content-Type: application/json' \
  -H 'X-FeatureName: Balance Enquiry' \
  -H 'X-ServiceCode: 1021002' \
  -H 'X-ServiceName: Synthetic Balance Enquiry' \
  -H 'X-ChannelCategory: WEB' \
  -H 'X-ChannelCode: 102' \
  -H 'X-ChannelName: Test Web' \
  -H 'Authorization: Bearer synthetic-not-a-real-token' \
  -d '{"messageID":"MSG-0001","companyCode":"BANK01","primaryData":{"accountNumber":"SYNTH-00000001"}}'
```

## Enterprise profile adoption

The authoritative, reviewer-recognized operation declaration is the `x-interface-profiles`
block in `docs/openapi.yaml`. The logging profile is selected under `enterprise-review` in
`application.yml`, with its implemented schema in `logback-spring.xml`.

| Service | Operation | Profile | Version | Status | Responsibility | Evidence |
|---|---|---|---|---|---|---|
| `balance-enquiry-service` | `queryBalance` | `bank_http_headers_profile` | 1.1.0 | adopted | API/gateway boundary; sample validates at controller | OpenAPI, controller, integration tests |
| same | same | `balance_query_request_profile` | 1.1.0 | adopted | application | OpenAPI, request DTO, tests |
| same | same | `bank_response_headers_profile` | 1.1.0 | adopted | application filter | OpenAPI, filter, tests |
| same | same | `bank_response_envelope_profile` | 1.1.0 | adopted | application | OpenAPI, response DTOs, tests |
| same | same | `balance_query_response_profile` | 1.1.0 | adopted | application | OpenAPI, response DTO, tests |
| same | same | `bank_http_error_mapping_profile` | 1.1.0 | adopted | application; gateway mapping remains environment evidence | OpenAPI, exception advice, tests |
| same | all request events | `bank_logging_profile` | 1.1.0 | adopted | application plus encoder | logback config, filter, tests |

Only these profiles are adopted. Optional headers are not made mandatory. The governed source
conflicts on the length of `messageCode` (length 6 versus a seven-character example), so this
sample uses the declared length 6 (`BAL000`, `VAL001`, etc.) and records the ambiguity rather
than claiming owner approval.

## Logging and platform boundary

The application supplies `TraceID`, operation/event identity, safe status information, and
latency. Logback supplies UTC `Timestamp`, `ThreadName`, and `LogLevel`; static configuration
supplies schema `Version` and `ServiceID`. A gateway may authenticate `Authorization`; a service
mesh may add network spans; an agent/observability platform owns collection, retention, indexing,
and any infrastructure enrichment. The application never logs authorization values, request or
response bodies, or account numbers.

## Compliance evidence and limitations

The contract, DTOs, exception advice, structured encoder, and tests provide repository evidence
for `MS-DES-005`, `MS-DES-006`, `MS-BLD-007`, `MS-BLD-009`, `MS-BLD-010`, `MS-BLD-011`, and
`MS-BLD-014`. Evidence is advisory: it does not establish standard approval, gateway enforcement,
deployed log output, CI coverage policy, production security, or active-active compliance.

Controlled defect branches are documented in `docs/compliance-scenarios.md`. They must never be
merged into the compliant baseline.
