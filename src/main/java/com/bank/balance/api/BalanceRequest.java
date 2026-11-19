package com.bank.balance.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BalanceRequest(
        @NotBlank @Size(max = 32) String messageID,
        @NotBlank @Size(max = 10) String companyCode,
        @NotNull @Valid PrimaryData primaryData,
        @Valid List<AdditionalData> additionalData) {
    public BalanceRequest {
        additionalData = additionalData == null ? List.of() : List.copyOf(additionalData);
    }

    @Override
    public List<AdditionalData> additionalData() {
        return List.copyOf(additionalData);
    }

    public record PrimaryData(@NotBlank @Size(max = 32) String accountNumber) {
    }
}
