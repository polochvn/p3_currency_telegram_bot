package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findAppUserByTelegramUserId(Long userId);
}
