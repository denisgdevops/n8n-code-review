package com.bank.balance.domain;

import java.math.BigDecimal;

public record AccountBalance(
        String accountNumber,
        String companyCode,
        String currency,
        BigDecimal unclearedBalance,
        BigDecimal accountBalance,
        BigDecimal limitAmount,
        boolean dormant,
        boolean postingRestricted,
        BigDecimal workingBalance,
        BigDecimal lockedAmount,
        BigDecimal retentionAmount,
        String accountName) {
}
