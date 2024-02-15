package com.skillbox.cryptobot.bot;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;


@Service
@Slf4j
public class CryptoBot extends TelegramLongPollingCommandBot {

    private final CryptoCurrencyService service;
    private final String botUsername;


    public CryptoBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<IBotCommand> commandList,
            CryptoCurrencyService service
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.service = service;
        commandList.forEach(this::register);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }

    @Scheduled(fixedDelay = 600_000)
    public void sendAds() {
        service.getUserChatIds().forEach(id -> {
            SendMessage message;
            try {
                message = new SendMessage();
                message.setChatId(id);
                message.setText("Пора покупать, стоимость биткоина " + service.getBitcoinPrice() + " USD");
                execute(message);
            } catch (TelegramApiException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
