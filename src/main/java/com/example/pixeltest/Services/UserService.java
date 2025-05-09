package com.example.pixeltest.Services;

import com.example.pixeltest.DAL.Repositories.AccountRepository;
import com.example.pixeltest.DAL.Repositories.EmailDataRepository;
import com.example.pixeltest.DAL.Repositories.PhoneDataRepository;
import com.example.pixeltest.DAL.Repositories.UserRepository;
import com.example.pixeltest.Models.DTOs.UserDTO;
import com.example.pixeltest.Models.Ntities.Account;
import com.example.pixeltest.Models.Ntities.EmailData;
import com.example.pixeltest.Models.Ntities.PhoneData;
import com.example.pixeltest.Models.Ntities.User;
import com.example.pixeltest.Utils.UserMapper;
import com.example.pixeltest.Utils.UserSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final AccountGrowthService accountGrowthService;
    private final AccountRepository accountRepository;

    public UserService(UserRepository userRepository, EmailDataRepository emailDataRepository,
                       PhoneDataRepository phoneDataRepository, AccountGrowthService accountGrowthService,
                       AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.emailDataRepository = emailDataRepository;
        this.phoneDataRepository = phoneDataRepository;
        this.accountGrowthService = accountGrowthService;
        this.accountRepository = accountRepository;
    }


    private void validateEmailsAndPhones(UserDTO userDTO, Long userId) {
        for (String email : userDTO.getEmails()) {
            boolean emailExists = emailDataRepository.existsByEmailAndUserIdNot(email, userId);
            if (emailExists) {
                throw new IllegalArgumentException("Email '" + email + "' is already used by another user");
            }
        }
        for (String phone : userDTO.getPhones()) {
            boolean phoneExists = phoneDataRepository.existsByPhoneAndUserIdNot(phone, userId);
            if (phoneExists) {
                throw new IllegalArgumentException("Phone '" + phone + "' is already used by another user");
            }
        }
    }


    private void updateEmailsAndPhones(User user, UserDTO userDTO) {
        Set<String> newPhones = new HashSet<>(userDTO.getPhones());
        Set<String> newEmails = new HashSet<>(userDTO.getEmails());

        user.getPhones().removeIf(phoneData -> !newPhones.contains(phoneData.getPhone()));
        user.getEmails().removeIf(emailData -> !newEmails.contains(emailData.getEmail()));

        userDTO.getPhones().forEach(phone -> user.addPhone(new PhoneData(phone)));
        userDTO.getEmails().forEach(email -> user.addEmail(new EmailData(email)));
    }

    @Transactional
    public User getUserByName(String name) {
        logger.info("Getting user by name: {}", name);
        return userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Cacheable("allUsers")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsersDTO() {
        logger.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserDTOById(Long id) {
        logger.info("Getting user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return UserMapper.toDto(user);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        logger.info("Creating new user: {}", userDTO.getName());
        if (userDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (userDTO.getPhones().isEmpty()) {
            throw new IllegalArgumentException("User must have at least one phone number");
        }
        if (userDTO.getEmails().isEmpty()) {
            throw new IllegalArgumentException("User must have at least one email");
        }
        if (userDTO.getBirthDate().isBefore(java.time.LocalDate.now().minusYears(100))) {
            throw new IllegalArgumentException("User birth date cannot be empty");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setBirthDate(userDTO.getBirthDate());
        if (userDTO.getBalance() == null) {
            userDTO.setBalance(BigDecimal.ZERO);
        }

        Account account = new Account(userDTO.getBalance());
        user.setAccount(account);
        account.setUser(user);

        for (String email : userDTO.getEmails()) {
            user.addEmail(new EmailData(email));
        }
        for (String phone : userDTO.getPhones()) {
            user.addPhone(new PhoneData(phone));
        }

        userRepository.save(user);
        accountGrowthService.registerNewAccount(user.getAccount());

        logger.info("User created successfully: {}", userDTO.getName());
        return UserMapper.toDto(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));

        validateEmailsAndPhones(userDTO, id);

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

        updateEmailsAndPhones(user, userDTO);

        logger.info("User with ID: {} updated successfully", id);
        return UserMapper.toDto(user);
    }

    @Transactional
    public void changeUserBalance(Long userId, BigDecimal amount) {
        logger.info("Changing balance for user ID: {} by amount: {}", userId, amount);

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

        logger.info("Balance changed successfully for user ID: {}", userId);
    }

    @Transactional
    public void sendMoney(Long senderId, Long receiverId, BigDecimal amount) {
        logger.info("Sending money from user ID: {} to user ID: {} with amount: {}", senderId, receiverId, amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver must be different");
        }

        Account senderAccount = accountRepository.findAccountForUpdateByUserId(senderId);
        Account receiverAccount = accountRepository.findAccountForUpdateByUserId(receiverId);

        if (senderAccount == null || receiverAccount == null) {
            throw new EntityNotFoundException("Account not found for user with id " +
                    (senderAccount == null ? senderId : receiverId));
        }

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        logger.info("Money transferred successfully from user ID: {} to user ID: {}", senderId, receiverId);
    }

    @Cacheable(value = "userSearch",
            key = "T(java.util.Objects).hash(#name, #phone, #email, #dateOfBirth?.time, #page, #size)")
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String name, String phone, String email, Date dateOfBirth,
                                     int page, int size) {

        Specification<User> spec = Specification.where(UserSpecification.hasNameLike(name))
                .and(UserSpecification.hasPhone(phone))
                .and(UserSpecification.hasEmail(email))
                .and(UserSpecification.hasDateOfBirthGreaterThan(dateOfBirth));

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(spec, pageable);

        return usersPage.map(UserMapper::toDto);
    }
}
