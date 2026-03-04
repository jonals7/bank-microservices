package com.bank.account_service;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.dto.MovementRequestDto;
import com.bank.account_service.exception.AccountNotFoundException;
import com.bank.account_service.exception.InsufficientBalanceException;
import com.bank.account_service.model.Account;
import com.bank.account_service.model.Movement;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.MovementRepository;
import com.bank.account_service.service.impl.MovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {

    @Mock
    private MovementRepository MovementRepository;

    @Mock
    private AccountRepository AccountRepository;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private MovementServiceImpl movementService;

    private Account Account;

    @BeforeEach
    void setUp() {
        Account = new Account(1L, "478758", "Ahorro",
                new BigDecimal("2000.00"), new BigDecimal("2000.00"), true, 1L);
    }

    @Test
    @DisplayName("Should apply DEBIT movement and reduce balance correctly")
    void createDebitMovement_success() {
        MovementRequestDto request = new MovementRequestDto(1L, "DEBIT", new BigDecimal("575.00"));

        Movement savedMovement = new Movement(1L, LocalDateTime.now(),
                "DEBIT", new BigDecimal("575.00"), new BigDecimal("1425.00"), 1L);

        when(AccountRepository.findById(1L)).thenReturn(Mono.just(Account));
        when(AccountRepository.save(any(Account.class))).thenReturn(Mono.just(Account));
        when(MovementRepository.save(any(Movement.class))).thenReturn(Mono.just(savedMovement));

        StepVerifier.create(movementService.create(request))
                .expectNextMatches(response ->
                        response.getMovementType().equals("DEBIT") &&
                        response.getBalance().compareTo(new BigDecimal("1425.00")) == 0
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should apply CREDIT movement and increase balance correctly")
    void createCreditMovement_success() {
        Account AccountWithLowBalance = new Account(2L, "225487", "Corriente",
                new BigDecimal("100.00"), new BigDecimal("100.00"), true, 2L);

        MovementRequestDto request = new MovementRequestDto(2L, "CREDIT", new BigDecimal("600.00"));

        Movement savedMovement = new Movement(2L, LocalDateTime.now(),
                "CREDIT", new BigDecimal("600.00"), new BigDecimal("700.00"), 2L);

        when(AccountRepository.findById(2L)).thenReturn(Mono.just(AccountWithLowBalance));
        when(AccountRepository.save(any(Account.class))).thenReturn(Mono.just(AccountWithLowBalance));
        when(MovementRepository.save(any(Movement.class))).thenReturn(Mono.just(savedMovement));

        StepVerifier.create(movementService.create(request))
                .expectNextMatches(response ->
                        response.getMovementType().equals("CREDIT") &&
                        response.getBalance().compareTo(new BigDecimal("700.00")) == 0
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when balance is not enough")
    void createDebitMovement_insufficientBalance() {
        Account AccountEmpty = new Account(3L, "495878", "Ahorro",
                BigDecimal.ZERO, BigDecimal.ZERO, true, 3L);

        MovementRequestDto request = new MovementRequestDto(3L, "DEBIT", new BigDecimal("150.00"));

        when(AccountRepository.findById(3L)).thenReturn(Mono.just(AccountEmpty));

        StepVerifier.create(movementService.create(request))
                .expectError(InsufficientBalanceException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account does not exist")
    void createMovement_accountNotFound() {
        MovementRequestDto request = new MovementRequestDto(99L, "DEBIT", new BigDecimal("100.00"));

        when(AccountRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(movementService.create(request))
                .expectError(AccountNotFoundException.class)
                .verify();
    }
}