package com.bank.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bank.balance.api.AdditionalData;
import com.bank.balance.api.BalanceRequest;
import com.bank.balance.api.BalanceResponse;
import com.bank.balance.domain.AccountBalance;
import com.bank.balance.domain.BalanceRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BalanceServiceTest {
    private final BalanceRepository repository = (account, company) -> Optional.of(new AccountBalance(
            account, company, "KES", new BigDecimal("0.10"), new BigDecimal("100.25"),
            BigDecimal.ZERO, false, false, new BigDecimal("100.15"),
            BigDecimal.ZERO, BigDecimal.ZERO, "SYNTHETIC ACCOUNT"));
    private final BalanceService service = new BalanceService(repository);

    @Test
    void returnsPreciseReadOnlyBalance() {
        BalanceResponse response = service.enquire(request(List.of()));
        assertThat(response.statusCode()).isEqualTo("0");
        assertThat(response.statusDescription()).isEqualTo("Success");
        assertThat(response.primaryData().accountBalance()).isEqualByComparingTo("100.25");
    }

    @Test
    void rejectsDuplicateAdditionalDataKeys() {
        List<AdditionalData> duplicates = List.of(
                new AdditionalData("purpose", "test"), new AdditionalData("purpose", "duplicate"));
        assertThatThrownBy(() -> service.enquire(request(duplicates)))
                .isInstanceOf(InvalidAdditionalDataException.class);
    }

    private BalanceRequest request(List<AdditionalData> additionalData) {
        return new BalanceRequest("MSG-1", "BANK01",
                new BalanceRequest.PrimaryData("SYNTH-00000001"), additionalData);
    }
}
