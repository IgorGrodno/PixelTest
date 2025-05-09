package com.example.pixeltest.Models.Ntities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "email_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"})
        })
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email")
    private String email;

    public EmailData() {
    }
    public EmailData(String email) {
        this.email = email;
    }
}
