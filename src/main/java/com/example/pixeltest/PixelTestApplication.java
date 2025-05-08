package com.example.pixeltest;

import com.example.pixeltest.DAL.Repositories.UserRepository;
import com.example.pixeltest.DAL.models.Account;
import com.example.pixeltest.DAL.models.EmailData;
import com.example.pixeltest.DAL.models.PhoneData;
import com.example.pixeltest.DAL.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class PixelTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixelTestApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            userRepository.deleteAll();


            for (int i = 0; i < 4; i++) {
                User user = new User("user" + (i + 1),
                        LocalDate.of(2000 + i, i + 1, i + 1), "password" + (i + 1));

                Account account = new Account(BigDecimal.valueOf(i * 1000 + 100));
                user.setAccount(account);
                account.setUser(user);

                EmailData email = new EmailData("email" + i + "@example.com");
                user.addEmail(email);

                PhoneData phone = new PhoneData("" + i + i + i + i + i + i);
                user.addPhone(phone);

                userRepository.save(user);
            }
        };
    }
}

