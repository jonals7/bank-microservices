package com.bank.account_service.service.impl;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.dto.MovementRequestDto;
import com.bank.account_service.dto.MovementResponseDto;
import com.bank.account_service.dto.StatementDto;
import com.bank.account_service.exception.AccountNotFoundException;
import com.bank.account_service.exception.InsufficientBalanceException;
import com.bank.account_service.model.Movement;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.MovementRepository;
import com.bank.account_service.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private static final String DEBIT  = "DEBIT";
    private static final String CREDIT = "CREDIT";

    private final MovementRepository MovementRepository;
    private final AccountRepository AccountRepository;
    private final CustomerClient customerClient;

    @Override
    public Flux<MovementResponseDto> findAll() {
        log.debug("Fetching all movements");
        return MovementRepository.findAll()
                .map(this::toResponseDto);
    }

    @Override
    public Mono<MovementResponseDto> findById(Long id) {
        log.debug("Fetching movement with id: {}", id);
        return MovementRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Movement not found with id: " + id)))
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public Mono<MovementResponseDto> create(MovementRequestDto request) {
        log.debug("Creating {} movement of {} for account {}", 
                request.getMovementType(), request.getAmount(), request.getAccountId());

        return AccountRepository.findById(request.getAccountId())
                .switchIfEmpty(Mono.error(new AccountNotFoundException(request.getAccountId())))
                .flatMap(Account -> {
                    BigDecimal currentBalance = Account.getCurrentBalance();
                    BigDecimal newBalance;

                    // F2: Debit subtracts, Credit adds
                    if (DEBIT.equalsIgnoreCase(request.getMovementType())) {
                        // F3: Insufficient balance check
                        if (currentBalance.compareTo(request.getAmount()) < 0) {
                            log.warn("Insufficient balance. Current: {}, Requested: {}",
                                    currentBalance, request.getAmount());
                            return Mono.error(new InsufficientBalanceException());
                        }
                        newBalance = currentBalance.subtract(request.getAmount());
                    } else if (CREDIT.equalsIgnoreCase(request.getMovementType())) {
                        newBalance = currentBalance.add(request.getAmount());
                    } else {
                        return Mono.error(new RuntimeException("Invalid movement type. Use DEBIT or CREDIT"));
                    }

                    // Update account balance
                    Account.setCurrentBalance(newBalance);

                    return AccountRepository.save(Account)
                            .flatMap(savedAccount -> {
                                Movement Movement = new Movement();
                                Movement.setDate(LocalDateTime.now());
                                Movement.setMovementType(request.getMovementType().toUpperCase());
                                Movement.setAmount(request.getAmount());
                                Movement.setBalance(newBalance);
                                Movement.setAccountId(savedAccount.getId());
                                return MovementRepository.save(Movement);
                            });
                })
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public Mono<MovementResponseDto> update(Long id, MovementRequestDto request) {
        log.debug("Updating movement with id: {}", id);
        return MovementRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Movement not found with id: " + id)))
                .flatMap(Movement -> {
                    Movement.setMovementType(request.getMovementType().toUpperCase());
                    Movement.setAmount(request.getAmount());
                    return MovementRepository.save(Movement);
                })
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.debug("Deleting movement with id: {}", id);
        return MovementRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Movement not found with id: " + id)))
                .flatMap(m -> MovementRepository.deleteById(id));
    }

    @Override
    public Mono<StatementDto> getStatement(Long customerId, LocalDate startDate, LocalDate endDate) {
        log.debug("Generating statement for customer {} from {} to {}", customerId, startDate, endDate);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.atTime(23, 59, 59);

        // 1. Validate customer exists and get their name
        return customerClient.findCustomerById(customerId)
                .flatMap(customer ->
                    // 2. Get all accounts for this customer
                    AccountRepository.findByCustomerId(customerId)
                            .flatMap(Account ->
                                // 3. For each account, get movements in date range
                                MovementRepository.findByAccountIdAndDateBetween(Account.getId(), start, end)
                                        .collectList()
                                        .map(movements -> {
                                            List<StatementDto.MovementDetail> details = movements.stream()
                                                    .map(m -> new StatementDto.MovementDetail(
                                                            m.getDate(),
                                                            m.getMovementType(),
                                                            m.getAmount(),
                                                            m.getBalance()
                                                    ))
                                                    .collect(Collectors.toList());

                                            return new StatementDto.AccountStatement(
                                                    Account.getAccountNumber(),
                                                    Account.getAccountType(),
                                                    Account.getCurrentBalance(),
                                                    Account.getStatus(),
                                                    details
                                            );
                                        })
                            )
                            .collectList()
                            .map(accounts -> new StatementDto(customerId, customer.getName(), accounts))
                );
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private MovementResponseDto toResponseDto(Movement m) {
        return new MovementResponseDto(
                m.getId(),
                m.getDate(),
                m.getMovementType(),
                m.getAmount(),
                m.getBalance(),
                m.getAccountId()
        );
    }
}