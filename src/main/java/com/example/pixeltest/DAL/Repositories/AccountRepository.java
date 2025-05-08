package com.example.pixeltest.DAL.Repositories;

import com.example.pixeltest.DAL.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}
