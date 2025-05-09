package com.example.pixeltest.Services;

import com.example.pixeltest.DAL.Repositories.UserRepository;
import com.example.pixeltest.DAL.Utils.UserMapper;
import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Models.Ntities.Account;
import com.example.pixeltest.Models.Ntities.EmailData;
import com.example.pixeltest.Models.Ntities.PhoneData;
import com.example.pixeltest.Models.Ntities.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Cacheable("allUsers")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsersDTO() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return UserMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setBirthDate(userDTO.getBirthDate());
        Account account = new Account(userDTO.getBalance());
        user.setAccount(account);
        account.setUser(user);
        for (String email : userDTO.getEmails()) {
            EmailData emailData = new EmailData(email);
            user.addEmail(emailData);
        }
        for (String phone : userDTO.getPhones()) {
            PhoneData phoneData = new PhoneData(phone);
            user.addPhone(phoneData);
        }
        userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        user.setName(userDTO.getName());
        user.setBirthDate(userDTO.getBirthDate());
        Account account = user.getAccount();
        if (account != null) {
            account.setBalance(userDTO.getBalance());
        } else {
            account = new Account(userDTO.getBalance());
            user.setAccount(account);
            account.setUser(user);
        }
        user.getEmails().clear();
        for (String email : userDTO.getEmails()) {
            EmailData emailData = new EmailData(email);
            user.addEmail(emailData);
        }
        user.getPhones().clear();
        for (String phone : userDTO.getPhones()) {
            PhoneData phoneData = new PhoneData(phone);
            user.addPhone(phoneData);
        }
        return UserMapper.toDto(user);
    }

    @Transactional
    public void changeUserBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
        Account account = user.getAccount();
        if (account != null) {
            BigDecimal currentBalance = account.getBalance();
            if (currentBalance.add(amount).compareTo(BigDecimal.ZERO) >= 0) {
                account.setBalance(currentBalance.add(amount));
            } else {
                throw new IllegalArgumentException("Insufficient funds");
            }
        } else {
            throw new EntityNotFoundException("Account not found for user with id " + userId);
        }
    }

    @Transactional
    public void sendMoney(Long senderId, Long receiverId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver must be different");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with id " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with id " + receiverId));

        Account senderAccount = sender.getAccount();
        Account receiverAccount = receiver.getAccount();

        if (senderAccount == null || receiverAccount == null) {
            throw new EntityNotFoundException("Account not found for user with id " +
                    (senderAccount == null ? senderId : receiverId));
        }

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
    }

}