package com.example.pixeltest.DAL.Repositories;

import com.example.pixeltest.Models.Ntities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String email);
}
