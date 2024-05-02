package org.example;

import org.example.lib.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class User {
    private final String name;
    ArrayList<Message> sendingMessages = new ArrayList<>();
    ArrayList<Message> receivingMessages = new ArrayList<>();
    String regDate;

    public User(String name) {
        this.name = name;
        this.regDate = LocalDateTime.now().toString();
    }

    public void AddSendingMessage(Message message) {
        sendingMessages.add(message);
    }

    public void AddReceivingMessage(Message message) {
        receivingMessages.add(message);
    }

    @Override
    public String toString() {
        return String.format("\nuser - %s: registration date %s\nsending messages: %d\nreceivingMessage: %d\n", name, regDate, sendingMessages.size(), receivingMessages.size());
    }

    public String getName() {
        return name;
    }
}
