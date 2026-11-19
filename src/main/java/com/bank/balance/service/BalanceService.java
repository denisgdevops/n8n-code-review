package com.bank.balance.service;

import com.bank.balance.api.AdditionalData;
import com.bank.balance.api.BalanceRequest;
import com.bank.balance.api.BalanceResponse;
import com.bank.balance.domain.AccountBalance;
import com.bank.balance.domain.BalanceRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {
    private final BalanceRepository repository;

    public BalanceService(BalanceRepository repository) {
        this.repository = repository;
    }

    public BalanceResponse enquire(BalanceRequest request) {
        validateUniqueAdditionalDataKeys(request.additionalData());
        AccountBalance account = repository.findByAccountNumberAndCompanyCode(
                        request.primaryData().accountNumber(), request.companyCode())
                .orElseThrow(AccountNotFoundException::new);

        BalanceResponse.BalanceData data = new BalanceResponse.BalanceData(
                account.companyCode(), account.currency(), account.unclearedBalance(),
                account.accountBalance(), account.limitAmount(), flag(account.dormant()),
                flag(account.postingRestricted()), account.workingBalance(), account.lockedAmount(),
                account.retentionAmount(), account.accountName());

        return new BalanceResponse("0", "Success", request.messageID(), null,
                "BAL000", "Balance enquiry completed", List.of(), data,
                request.additionalData());
    }

    private void validateUniqueAdditionalDataKeys(List<AdditionalData> additionalData) {
        Set<String> keys = new HashSet<>();
        if (additionalData.stream().map(AdditionalData::key).anyMatch(key -> !keys.add(key))) {
            throw new InvalidAdditionalDataException("Additional data keys must be unique");
        }
    }

    private String flag(boolean value) {
        return value ? "Y" : "N";
    }
}
