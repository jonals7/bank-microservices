package com.bank.account_service.controller;

import com.bank.account_service.dto.ApiResponseDto;
import com.bank.account_service.dto.MovementRequestDto;
import com.bank.account_service.dto.MovementResponseDto;
import com.bank.account_service.dto.StatementDto;
import com.bank.account_service.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @GetMapping("/api/v1/movements")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<?>> findAll() {
        log.info("GET /api/v1/movements");
        return movementService.findAll()
                .collectList()
                .map(ApiResponseDto::success);
    }

    @GetMapping("/api/v1/movements/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<MovementResponseDto>> findById(@PathVariable Long id) {
        log.info("GET /api/v1/movements/{}", id);
        return movementService.findById(id)
                .map(ApiResponseDto::success);
    }

    @PostMapping("/api/v1/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponseDto<MovementResponseDto>> create(@Valid @RequestBody MovementRequestDto request) {
        log.info("POST /api/v1/movements - accountId: {}, type: {}", request.getAccountId(), request.getMovementType());
        return movementService.create(request)
                .map(ApiResponseDto::created);
    }

    @PutMapping("/api/v1/movements/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<MovementResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody MovementRequestDto request) {
        log.info("PUT /api/v1/movements/{}", id);
        return movementService.update(id, request)
                .map(ApiResponseDto::success);
    }

    @DeleteMapping("/api/v1/movements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/movements/{}", id);
        return movementService.delete(id);
    }

    // F4: Statement report endpoint
    @GetMapping("/reports/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ApiResponseDto<StatementDto>> getStatement(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /reports/{} from {} to {}", customerId, startDate, endDate);
        return movementService.getStatement(customerId, startDate, endDate)
                .map(ApiResponseDto::success);
    }
}