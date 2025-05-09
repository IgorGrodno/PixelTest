package com.example.pixeltest.Models.Ntities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "balance")
    @Min(0)
    private BigDecimal balance;

    public Account() {
    }

    public Account(BigDecimal balance) {
        this.balance = balance;
    }
}
