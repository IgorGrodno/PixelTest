package com.example.pixeltest.Services;

import com.example.pixeltest.DAL.Repositories.AccountRepository;
import com.example.pixeltest.Models.Ntities.Account;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountGrowthService {

    private final AccountRepository accountRepository;
    private final Map<Long, BigDecimal> initialBalances = new ConcurrentHashMap<>();

    public AccountGrowthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void increaseBalances() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            Long accountId = account.getId();
            BigDecimal current = account.getBalance();

            initialBalances.putIfAbsent(accountId, current);

            BigDecimal initial = initialBalances.get(accountId);
            BigDecimal max = initial.multiply(BigDecimal.valueOf(2.07));

            if (current.compareTo(max) < 0) {
                BigDecimal increased = current.multiply(BigDecimal.valueOf(1.1));
                BigDecimal capped = increased.min(max);
                account.setBalance(capped);
            }
        }
    }

    public void registerNewAccount(Account account) {
        initialBalances.put(account.getId(), account.getBalance());
    }
}

