package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.client.BinanceClient;
import com.skillbox.cryptobot.entity.AppUser;
import com.skillbox.cryptobot.repository.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

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
        if (price.get() == null) {
            price.set(client.getBitcoinPrice());
        }
        return price.get();
    }

    public void createUser(Long userId) {
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
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user != null) {
            user.setBitcoinPrice(price);
            userRepository.save(user);
        }
    }

    public Long getSubscription(Long userId) {
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user.getBitcoinPrice() == null) {
            return null;
        } else {
            return user.getBitcoinPrice();
        }
    }

    public boolean deleteSubscription(Long userId) {
        AppUser user = userRepository.findAppUserByTelegramUserId(userId);
        if (user.getBitcoinPrice() != null) {
            user.setBitcoinPrice(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void bitcoinCostTracking() {

    }
}
