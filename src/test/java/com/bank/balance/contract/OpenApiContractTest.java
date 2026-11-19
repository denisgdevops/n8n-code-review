package com.bank.balance.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class OpenApiContractTest {
    @SuppressWarnings("unchecked")
    @Test
    void openApiIsValidYamlAndDeclaresImplementedContract() throws IOException {
        String content = Files.readString(Path.of("docs/openapi.yaml"));
        Map<String, Object> document = new Yaml().load(content);
        Map<String, Object> paths = (Map<String, Object>) document.get("paths");
        Map<String, Object> path = (Map<String, Object>) paths.get("/api/v1/balances/enquiry");
        Map<String, Object> operation = (Map<String, Object>) path.get("post");
        assertThat(document.get("openapi")).isEqualTo("3.0.3");
        assertThat(operation.get("operationId")).isEqualTo("queryBalance");
        assertThat((List<String>) operation.get("x-interface-profiles")).containsExactly(
                "bank_http_headers_profile", "balance_query_request_profile",
                "bank_response_headers_profile", "bank_response_envelope_profile",
                "balance_query_response_profile", "bank_http_error_mapping_profile");
        Map<String, Object> responses = (Map<String, Object>) operation.get("responses");
        assertThat(responses).containsKeys("200", "400", "500");
        assertThat(content).contains("X-ServiceCode", "Authorization", "X-ElapsedTime",
                "primaryData", "accountBalance", "messageDescription");
    }
}
