package com.bank.balance.infrastructure;

import com.bank.balance.domain.AccountBalance;
import com.bank.balance.domain.BalanceRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBalanceRepository implements BalanceRepository {
    private final Map<String, AccountBalance> accounts = Map.of(
            "SYNTH-00000001", new AccountBalance(
                    "SYNTH-00000001", "BANK01", "KES",
                    new BigDecimal("125.50"), new BigDecimal("10250.75"),
                    new BigDecimal("5000.00"), false, false,
                    new BigDecimal("10125.25"), new BigDecimal("100.00"),
                    new BigDecimal("25.50"), "SYNTHETIC CURRENT ACCOUNT"));

    @Override
    public Optional<AccountBalance> findByAccountNumberAndCompanyCode(
            String accountNumber, String companyCode) {
        return Optional.ofNullable(accounts.get(accountNumber))
                .filter(account -> account.companyCode().equals(companyCode));
    }
}
