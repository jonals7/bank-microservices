package com.bank.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {

    private Long id;

    // Person fields
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;

    // Customer fields
    private Boolean status;
    // Note: password is intentionally excluded from response
}