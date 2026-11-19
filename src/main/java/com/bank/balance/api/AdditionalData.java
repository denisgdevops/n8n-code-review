package com.bank.balance.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdditionalData(
        @NotBlank @Size(max = 30) String key,
        @NotBlank @Size(max = 100) String value) {
}
