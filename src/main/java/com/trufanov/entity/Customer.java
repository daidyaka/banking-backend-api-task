package com.trufanov.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
public class Customer {

    @Id
    private Long id;
    private String name;
    private String surname;

    @OneToMany(mappedBy = "customer")
    private List<Account> accounts;

}
