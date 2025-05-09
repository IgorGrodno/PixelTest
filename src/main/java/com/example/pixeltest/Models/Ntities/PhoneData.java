package com.example.pixeltest.Models.Ntities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "phone_data",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"phone"})
        })
public class PhoneData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "phone")
    private String phone;

    public PhoneData() {
    }

    public PhoneData(String phone) {
        this.phone = phone;
    }
}
