package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.client.BinanceClient;
import com.skillbox.cryptobot.entity.AppUser;
import com.skillbox.cryptobot.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CryptoCurrencyService {

    private final AppUserRepository userRepository;
    private final AtomicReference<Double> price = new AtomicReference<>();
    private final BinanceClient client;

    public CryptoCurrencyService(BinanceClient client, AppUserRepository userRepository) {
        this.client = client;
        this.userRepository = userRepository;
    }

    public double getBitcoinPrice() throws IOException {
        log.info("Getting bitcoin price");
        if (price.get() == null) {
            price.set(client.getBitcoinPrice());
        }
        return price.get();
    }

    public void createUser(Long userId) {
        log.info("Saving the user to the database");
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user == null) {
            user = AppUser.builder()
                    .telegramUserId(userId)
                    .bitcoinPrice(null)
                    .build();
            userRepository.save(user);
        }
    }

    public void updateBitcoinPrice(Long userId, Long price) {
        log.info("Subscription activation");
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user != null) {
            user.setBitcoinPrice(price);
            userRepository.save(user);
        }
    }

    public Long getSubscription(Long userId) {
        log.info("Getting an active subscription");
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user.getBitcoinPrice() == null) {
            return null;
        } else {
            return user.getBitcoinPrice();
        }
    }

    public boolean deleteSubscription(Long userId) {
        log.info("Deleting a subscription");
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user.getBitcoinPrice() != null) {
            user.setBitcoinPrice(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Scheduled(fixedDelay = 120_000)
    public void bitcoinCostTracking() throws IOException {
        price.set(client.getBitcoinPrice());
    }

    public List<Long> getUserChatIds() {
        log.info("Obtaining a list of users with the subscription required for purchase");
        List<AppUser> users = userRepository.findAll();
        return users
                .stream()
                .filter(u -> u.getBitcoinPrice() != null && price.get() != null && price.get() < u.getBitcoinPrice())
                .map(AppUser::getTelegramUserId)
                .collect(Collectors.toList());
    }
}
