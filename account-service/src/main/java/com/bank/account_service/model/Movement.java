package com.bank.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("Movement")
public class Movement {

    @Id
    private Long id;

    private LocalDateTime date;

    @Column("movement_type")
    private String movementType;

    private BigDecimal amount;

    private BigDecimal balance;

    @Column("account_id")
    private Long accountId;
}