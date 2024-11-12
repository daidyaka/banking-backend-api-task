package com.trufanov.dto.request;

import java.math.BigDecimal;

public record CreateAccountRequestDto(Long customerId, BigDecimal initialCredit) {
}
