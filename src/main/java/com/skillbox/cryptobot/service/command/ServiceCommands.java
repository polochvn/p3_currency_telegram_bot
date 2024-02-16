package com.skillbox.cryptobot.service.command;

public enum ServiceCommands {

    START("/start"),
    GET_PRICE("/get_price"),
    SUBSCRIBE("/subscribe"),
    GET_SUBSCRIPTION("/get_subscription"),
    UNSUBSCRIBE("/unsubscribe");

    private final String command;

    ServiceCommands(String command) {
        this.command = command;
    }

    public boolean equals(String command) {
        return this.toString().equals(command);
    }
}
