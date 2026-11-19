package com.bank.balance.domain;

import java.util.Optional;

public interface BalanceRepository {
    Optional<AccountBalance> findByAccountNumberAndCompanyCode(String accountNumber, String companyCode);
}
