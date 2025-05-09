package com.example.pixeltest.DAL.Repositories;

import com.example.pixeltest.Models.Ntities.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhoneAndUserIdNot(String phone, Long userId);
}
