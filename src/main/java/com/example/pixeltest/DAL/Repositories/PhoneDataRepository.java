package com.example.pixeltest.DAL.Repositories;

import com.example.pixeltest.DAL.models.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

}
