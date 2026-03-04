package com.bank.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("Account")
public class Account {

    @Id
    private Long id;

    @Column("account_number")
    private String accountNumber;

    @Column("account_type")
    private String accountType;

    @Column("initial_balance")
    private BigDecimal initialBalance;

    @Column("current_balance")
    private BigDecimal currentBalance;

    private Boolean status;

    @Column("customer_id")
    private Long customerId;
}