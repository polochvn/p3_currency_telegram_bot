package com.skillbox.cryptobot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "telegram_user_id")
    private Long telegramUserId;

    @Column(name = "bitcoin_price")
    private Long bitcoinPrice;
}
