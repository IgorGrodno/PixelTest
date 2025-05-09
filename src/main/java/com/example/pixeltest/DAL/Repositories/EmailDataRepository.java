package com.example.pixeltest.DAL.Repositories;

import com.example.pixeltest.Models.Ntities.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmailAndUserIdNot(String email, Long userId);
}
