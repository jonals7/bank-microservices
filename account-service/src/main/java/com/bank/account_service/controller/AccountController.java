package com.bank.account_service.controller;

import com.bank.account_service.dto.AccountRequestDto;
import com.bank.account_service.dto.AccountResponseDto;
import com.bank.account_service.dto.ApiResponseDto;
import com.bank.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<?>> findAll() {
        log.info("GET /api/v1/accounts");
        return accountService.findAll()
                .collectList()
                .map(ApiResponseDto::success);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<AccountResponseDto>> findById(@PathVariable Long id) {
        log.info("GET /api/v1/accounts/{}", id);
        return accountService.findById(id)
                .map(ApiResponseDto::success);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponseDto<AccountResponseDto>> create(@Valid @RequestBody AccountRequestDto request) {
        log.info("POST /api/v1/accounts - accountNumber: {}", request.getAccountNumber());
        return accountService.create(request)
                .map(ApiResponseDto::created);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<AccountResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDto request) {
        log.info("PUT /api/v1/accounts/{}", id);
        return accountService.update(id, request)
                .map(ApiResponseDto::success);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/accounts/{}", id);
        return accountService.delete(id);
    }
}