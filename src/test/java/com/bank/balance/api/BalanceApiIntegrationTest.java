package com.bank.balance.api;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(properties = {"debug=false", "logging.level.org.springframework.web=INFO"})
@AutoConfigureMockMvc
class BalanceApiIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test
    void returnsContractCompliantBalanceAndStableTraceID() throws Exception {
        mvc.perform(validRequest().header("X-Correlation-ID", "trace-stable-001"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-ElapsedTime", matchesPattern("[0-9]+")))
                .andExpect(header().string("X-Correlation-ID", "trace-stable-001"))
                .andExpect(jsonPath("$.statusCode").value("0"))
                .andExpect(jsonPath("$.statusDescription").value("Success"))
                .andExpect(jsonPath("$.messageID").value("MSG-0001"))
                .andExpect(jsonPath("$.messageCode").value("BAL000"))
                .andExpect(jsonPath("$.messageDescription").value("Balance enquiry completed"))
                .andExpect(jsonPath("$.primaryData.companyCode").value("BANK01"))
                .andExpect(jsonPath("$.primaryData.currency").value("KES"))
                .andExpect(jsonPath("$.primaryData.accountBalance").value(10250.75))
                .andExpect(jsonPath("$.primaryData.isAccountDormant").value("N"))
                .andExpect(jsonPath("$.primaryData.accountName").value("SYNTHETIC CURRENT ACCOUNT"));
    }

    @Test
    void rejectsMissingMandatoryRequestFieldWithSafeEnvelope() throws Exception {
        mvc.perform(headers(post("/api/v1/balances/enquiry"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyCode\":\"BANK01\",\"primaryData\":{\"accountNumber\":\"SYNTH-00000001\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-ElapsedTime"))
                .andExpect(jsonPath("$.statusCode").value("1"))
                .andExpect(jsonPath("$.statusDescription").value("Failed"))
                .andExpect(jsonPath("$.messageCode").value("VAL001"))
                .andExpect(jsonPath("$.errorInfo[0].errorCode").value("VAL001"));
    }

    @Test
    void rejectsMissingRequiredHeader() throws Exception {
        mvc.perform(post("/api/v1/balances/enquiry")
                        .header("X-FeatureName", "Balance Enquiry")
                        .contentType(MediaType.APPLICATION_JSON).content(validBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("1"));
    }

    @Test
    void mapsUnknownAccountToAdoptedBadRequest() throws Exception {
        mvc.perform(headers(post("/api/v1/balances/enquiry"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody().replace("SYNTH-00000001", "SYNTH-99999999")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("1"))
                .andExpect(jsonPath("$.statusDescription").value("Failed"))
                .andExpect(jsonPath("$.messageCode").value("ACC001"))
                .andExpect(jsonPath("$.errorInfo[0].errorDescription")
                        .value("The requested synthetic account does not exist"));
    }

    private MockHttpServletRequestBuilder validRequest() {
        return headers(post("/api/v1/balances/enquiry"))
                .contentType(MediaType.APPLICATION_JSON).content(validBody());
    }

    private MockHttpServletRequestBuilder headers(MockHttpServletRequestBuilder builder) {
        return builder.header("X-FeatureName", "Balance Enquiry")
                .header("X-ServiceCode", "1021002")
                .header("X-ServiceName", "Synthetic Balance Enquiry")
                .header("X-ChannelCategory", "WEB").header("X-ChannelCode", "102")
                .header("X-ChannelName", "Test Web")
                .header("Authorization", "Bearer synthetic-test-token");
    }

    private String validBody() {
        return "{\"messageID\":\"MSG-0001\",\"companyCode\":\"BANK01\","
                + "\"primaryData\":{\"accountNumber\":\"SYNTH-00000001\"}}";
    }
}
