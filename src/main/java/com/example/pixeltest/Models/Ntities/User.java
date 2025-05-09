package com.example.pixeltest.Models.Ntities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"})
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Size(max = 500)
    private String name;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Column(name = "date_of_birth")
    private LocalDate birthDate;

    @Column(name = "password")
    @Size(min = 8, max = 500)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhoneData> phones = new HashSet<>();

    public void addEmail(EmailData email) {
        emails.add(email);
        email.setUser(this);
    }

    public void removeEmail(EmailData email) {
        emails.remove(email);
        email.setUser(null);
    }

    public void addPhone(PhoneData phone) {
        phones.add(phone);
        phone.setUser(this);
    }

    public void removePhone(PhoneData phone) {
        phones.remove(phone);
        phone.setUser(null);
    }

    public User() {
    }

    public User(String name, LocalDate birthDate, String password) {
        this.name = name;
        this.birthDate = birthDate;
        this.password = password;
    }
}
