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
import java.util.concurrent.TimeUnit;

import static com.skillbox.cryptobot.service.command.ServiceCommands.*;


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
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (SUBSCRIBE.equals(text)) {
            message.setText("Введите сумму");
            sendMessage(message);
        } else if (text.matches("\\d*[,.]?\\d*")) {
            message.setText("Новая подписка создана на стоимость " + text + " USD");
            service.updateBitcoinPrice(chatId, Double.valueOf(text.replace(",", ".")));
            sendMessage(message);
        } else if (!(START.equals(text) || GET_SUBSCRIPTION.equals(text) || UNSUBSCRIBE.equals(text) || GET_PRICE.equals(text))) {
            message.setText("Unknown command");
            sendMessage(message);
        }
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelayString = "${scheduled.fixed-delay.notification-to-user}", timeUnit = TimeUnit.MINUTES)
    public void sendAds() {
        service.getUserChatIds().forEach(id -> {
            SendMessage message;
            try {
                message = new SendMessage();
                message.setChatId(id);
                message.setText("Пора покупать, стоимость биткоина " + service.getBitcoinPrice() + " USD");
                execute(message);
            } catch (TelegramApiException | IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
