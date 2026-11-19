package com.bank.balance.api;

import java.util.List;

public record ErrorResponse(
        String statusCode,
        String statusDescription,
        String messageID,
        String conversationID,
        String messageCode,
        String messageDescription,
        List<ErrorInfo> errorInfo) {
    public ErrorResponse {
        errorInfo = errorInfo == null ? List.of() : List.copyOf(errorInfo);
    }

    @Override
    public List<ErrorInfo> errorInfo() {
        return List.copyOf(errorInfo);
    }
}
