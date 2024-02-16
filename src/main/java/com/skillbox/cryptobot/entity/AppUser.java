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

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "bitcoin_price")
    private Double bitcoinPrice;
}
