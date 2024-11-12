package com.trufanov.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
public class Account {
    @Id
    private Long id;
    private Double balance;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;
}
