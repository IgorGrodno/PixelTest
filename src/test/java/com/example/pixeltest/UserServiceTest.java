package com.example.pixeltest;

import com.example.pixeltest.DAL.Repositories.AccountRepository;
import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Models.Ntities.Account;
import com.example.pixeltest.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Testcontainers
@AutoConfigureMockMvc
public class UserServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("pixeltest")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Mock
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;



    @Test
    void testCreateUser_successful() {
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setName("user10");
        userDTO1.setPhones(Set.of("1111111"));
        userDTO1.setEmails(Set.of("user1@example.com"));
        userDTO1.setBirthDate(java.time.LocalDate.of(1991, 1, 1));
        userDTO1.setBalance(new BigDecimal("100.00"));

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setName("user20");
        userDTO2.setPhones(Set.of("2222222"));
        userDTO2.setEmails(Set.of("user2@example.com"));
        userDTO2.setBirthDate(java.time.LocalDate.of(1992, 2, 2));
        userDTO2.setBalance(new BigDecimal("200.00"));

        userService.createUser(userDTO1);
        userService.createUser(userDTO2);

        UserDTO createdUser1 = userService.getUserDTOByName("user10");
        UserDTO createdUser2 = userService.getUserDTOByName("user20");

        assertNotNull(createdUser1);
        assertEquals("user10", createdUser1.getName());
        assertEquals("1111111", createdUser1.getPhones().stream().findFirst().get());
        assertEquals("user1@example.com", createdUser1.getEmails().stream().findFirst().get());
        assertEquals(new BigDecimal("100.00"), createdUser1.getBalance());

        assertNotNull(createdUser2);
        assertEquals("user20", createdUser2.getName());
        assertEquals("2222222", createdUser2.getPhones().stream().findFirst().get());
        assertEquals("user2@example.com", createdUser2.getEmails().stream().findFirst().get());
        assertEquals(new BigDecimal("200.00"), createdUser2.getBalance());
    }



    @Test
    void sendMoney_successfulTransfer() {
        UserDTO createdUser1 = userService.getUserDTOByName("user10");
        UserDTO createdUser2 = userService.getUserDTOByName("user20");
        BigDecimal amount = new BigDecimal("50.00");

        userService.sendMoney(createdUser1.getId(), createdUser2.getId(), amount);

        UserDTO updatedUser1 = userService.getUserDTOByName("user10");
        UserDTO updatedUser2 = userService.getUserDTOByName("user20");

        assertEquals(new BigDecimal("50.00"), updatedUser1.getBalance());
        assertEquals(new BigDecimal("250.00"), updatedUser2.getBalance());
    }


    @Test
    void sendMoney_insufficientFunds_shouldThrow() {
        Long senderId = 1L;
        Long receiverId = 2L;
        BigDecimal amount = new BigDecimal("1000.00");

        Account sender = new Account(new BigDecimal("50.00"));
        Account receiver = new Account(new BigDecimal("20.00"));

        when(accountRepository.findAccountForUpdateByUserId(senderId)).thenReturn(sender);
        when(accountRepository.findAccountForUpdateByUserId(receiverId)).thenReturn(receiver);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.sendMoney(senderId, receiverId, amount));

        assertEquals("Insufficient funds", exception.getMessage());
    }


}
