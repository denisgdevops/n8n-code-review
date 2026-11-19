package com.bank.balance.api;

import java.math.BigDecimal;
import java.util.List;

public record BalanceResponse(
        String statusCode,
        String statusDescription,
        String messageID,
        String conversationID,
        String messageCode,
        String messageDescription,
        List<ErrorInfo> errorInfo,
        BalanceData primaryData,
        List<AdditionalData> additionalData) {

    public BalanceResponse {
        errorInfo = errorInfo == null ? List.of() : List.copyOf(errorInfo);
        additionalData = additionalData == null ? List.of() : List.copyOf(additionalData);
    }

    @Override
    public List<ErrorInfo> errorInfo() {
        return List.copyOf(errorInfo);
    }

    @Override
    public List<AdditionalData> additionalData() {
        return List.copyOf(additionalData);
    }

    public record BalanceData(
            String companyCode,
            String currency,
            BigDecimal unclearedBalance,
            BigDecimal accountBalance,
            BigDecimal limitAmount,
            String isAccountDormant,
            String isPostingRestricted,
            BigDecimal workingBalance,
            BigDecimal lockedAmount,
            BigDecimal retentionAmount,
            String accountName) {
    }
}
