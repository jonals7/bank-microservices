package com.bank.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementDto {

    private Long customerId;
    private String customerName;

    private List<AccountStatement> accounts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountStatement {
        private String accountNumber;
        private String accountType;
        private BigDecimal currentBalance;
        private Boolean status;
        private List<MovementDetail> movements;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementDetail {
        private LocalDateTime date;
        private String movementType;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
    }
}