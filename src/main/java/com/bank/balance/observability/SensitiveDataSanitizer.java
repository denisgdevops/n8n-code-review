package com.bank.balance.observability;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class SensitiveDataSanitizer {
    private static final Pattern BEARER = Pattern.compile("(?i)Bearer\\s+[A-Za-z0-9._~+/-]+=*");
    private static final Pattern ACCOUNT = Pattern.compile("\\bSYNTH-[0-9]{8}\\b");

    public String sanitize(String value) {
        if (value == null) {
            return null;
        }
        return ACCOUNT.matcher(BEARER.matcher(value).replaceAll("Bearer [REDACTED]"))
                .replaceAll("[ACCOUNT_REDACTED]");
    }

    public String maskedAuthorization(String authorization) {
        return authorization == null || authorization.isBlank() ? "absent" : "Bearer [REDACTED]";
    }
}
