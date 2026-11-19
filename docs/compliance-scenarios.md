# Controlled compliance scenarios

Start every scenario from the tested compliant baseline and keep each defect in its own local
branch/commit. Never merge a defect branch into the baseline.

| Scenario | Local branch | Isolated change | Expected deterministic result |
|---|---|---|---|
| A — compliant baseline | `compliance/baseline` | Complete code, OpenAPI, profiles, errors, logs, tests | At least contract field checks can PASS; the known `messageCode` ambiguity and deployment evidence may remain NEEDS_EVIDENCE |
| B — API contract violation | `compliance/violation-contract` | Make `X-ServiceCode` optional in OpenAPI while the adopted header profile requires it | `MS-DES-005`/`MS-BLD-007` deterministic field FAIL |
| C — error violation | `compliance/violation-error` | Change documented validation response from adopted HTTP 400 to 422 | `MS-DES-006` deterministic mapping FAIL when OpenAPI is collected |
| D — logging violation | `compliance/violation-logging` | Remove required `TraceID` from the final encoder | `MS-BLD-011` deterministic schema FAIL |
| E — security finding | `compliance/violation-security` | Add a synthetic literal such as `Authorization: Bearer synthetic-demo-only` to a test-only controller log | Static review finding for credential logging; no real secret or harmful behavior |

Expected outcomes are predictions based on the current reviewer implementation, not live-review
results. Live results depend on bounded PR context collection. Each PR should include the defect,
its directly related contract/configuration, and a focused test so the reviewer can prove the
single intended mismatch.
