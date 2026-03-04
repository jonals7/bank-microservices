package com.bank.customer_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("Person")
public class Person {

    @Id
    private Long id;

    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
}