package com.trufanov.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Transaction {
    @Id
    private Long id;
    private Double amount;

    @ManyToOne
    private Account account;
}