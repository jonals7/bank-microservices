package com.bank.account_service.service.impl;

import com.bank.account_service.client.CustomerClient;
import com.bank.account_service.dto.AccountRequestDto;
import com.bank.account_service.dto.AccountResponseDto;
import com.bank.account_service.exception.AccountNotFoundException;
import com.bank.account_service.exception.DuplicateAccountNumberException;
import com.bank.account_service.model.Account;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository AccountRepository;
    private final CustomerClient customerClient;

    @Override
    public Flux<AccountResponseDto> findAll() {
        log.debug("Fetching all accounts");
        return AccountRepository.findAll()
                .map(this::toResponseDto);
    }

    @Override
    public Mono<AccountResponseDto> findById(Long id) {
        log.debug("Fetching account with id: {}", id);
        return AccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public Mono<AccountResponseDto> create(AccountRequestDto request) {
        log.debug("Creating account number: {}", request.getAccountNumber());
        // 1. Validate customer exists in customer-service
        return customerClient.findCustomerById(request.getCustomerId())
                .flatMap(customer -> AccountRepository.existsByAccountNumber(request.getAccountNumber()))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateAccountNumberException(request.getAccountNumber()));
                    }
                    Account Account = toEntity(request);
                    return AccountRepository.save(Account);
                })
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public Mono<AccountResponseDto> update(Long id, AccountRequestDto request) {
        log.debug("Updating account with id: {}", id);
        return AccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .flatMap(Account -> {
                    Account.setAccountNumber(request.getAccountNumber());
                    Account.setAccountType(request.getAccountType());
                    Account.setInitialBalance(request.getInitialBalance());
                    Account.setCurrentBalance(request.getInitialBalance());
                    Account.setStatus(request.getStatus());
                    Account.setCustomerId(request.getCustomerId());
                    return AccountRepository.save(Account);
                })
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        log.debug("Deleting account with id: {}", id);
        return AccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .flatMap(Account -> AccountRepository.deleteById(id));
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Account toEntity(AccountRequestDto dto) {
        Account Account = new Account();
        Account.setAccountNumber(dto.getAccountNumber());
        Account.setAccountType(dto.getAccountType());
        Account.setInitialBalance(dto.getInitialBalance());
        Account.setCurrentBalance(dto.getInitialBalance()); // currentBalance starts equal to initialBalance
        Account.setStatus(dto.getStatus());
        Account.setCustomerId(dto.getCustomerId());
        return Account;
    }

    private AccountResponseDto toResponseDto(Account Account) {
        return new AccountResponseDto(
                Account.getId(),
                Account.getAccountNumber(),
                Account.getAccountType(),
                Account.getInitialBalance(),
                Account.getCurrentBalance(),
                Account.getStatus(),
                Account.getCustomerId()
        );
    }
}