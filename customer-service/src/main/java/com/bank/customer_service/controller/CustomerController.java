package com.bank.customer_service.controller;

import com.bank.customer_service.dto.ApiResponseDto;
import com.bank.customer_service.dto.CustomerRequestDto;
import com.bank.customer_service.dto.CustomerResponseDto;
import com.bank.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<?>> findAll() {
        log.info("GET /api/v1/customers");
        return customerService.findAll()
                .collectList()
                .map(ApiResponseDto::success);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<CustomerResponseDto>> findById(@PathVariable Long id) {
        log.info("GET /api/v1/customers/{}", id);
        return customerService.findById(id)
                .map(ApiResponseDto::success);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponseDto<CustomerResponseDto>> create(@Valid @RequestBody CustomerRequestDto request) {
        log.info("POST /api/v1/customers - identification: {}", request.getIdentification());
        return customerService.create(request)
                .map(ApiResponseDto::created);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<CustomerResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto request) {
        log.info("PUT /api/v1/customers/{}", id);
        return customerService.update(id, request)
                .map(ApiResponseDto::success);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/customers/{}", id);
        return customerService.delete(id);
    }
}