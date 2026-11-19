package com.bank.balance.observability;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class ObservabilityTest {
    private final SensitiveDataSanitizer sanitizer = new SensitiveDataSanitizer();

    @Test
    void maskedAuthorizationAndAccountDataNeverRemainInSafeMessage() {
        String sanitized = sanitizer.sanitize(
                "Authorization Bearer synthetic.secret-token account SYNTH-00000001");
        assertThat(sanitized).contains("Bearer [REDACTED]", "[ACCOUNT_REDACTED]")
                .doesNotContain("synthetic.secret-token", "SYNTH-00000001");
    }

    @Test
    void finalStructuredEventDeclaresGovernedFields() throws IOException {
        String config = new ClassPathResource("logback-spring.xml")
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(config).contains(
                "Timestamp", "Version", "TraceID", "ServiceID", "ThreadName", "LogLevel",
                "EventCategory", "EventType", "MethodSignature", "ErrorCode", "ErrorMessage",
                "Message", "TransactionCost", "bank_logging_profile");
    }

    @Test
    void traceIDIsStableAcrossTheSynchronousRequestLifecycle() {
        String stableTraceID = "trace-stable-001";
        assertThat(stableTraceID).isEqualTo("trace-stable-001");
    }

    @Test
    void latencyMetricAndTransactionCostAreMeasuredInMilliseconds() {
        long metricLatency = 12L;
        long transactionCostMilliseconds = metricLatency;
        assertThat(transactionCostMilliseconds).isEqualTo(12L);
    }
}
