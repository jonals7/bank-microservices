package com.bank.account_service.service;

import com.bank.account_service.dto.AccountRequestDto;
import com.bank.account_service.dto.AccountResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    Flux<AccountResponseDto> findAll();

    Mono<AccountResponseDto> findById(Long id);

    Mono<AccountResponseDto> create(AccountRequestDto request);

    Mono<AccountResponseDto> update(Long id, AccountRequestDto request);

    Mono<Void> delete(Long id);
}