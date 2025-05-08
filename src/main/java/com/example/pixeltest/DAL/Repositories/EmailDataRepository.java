package com.example.pixeltest.DAL.Repositories;

import com.example.pixeltest.DAL.models.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

}
