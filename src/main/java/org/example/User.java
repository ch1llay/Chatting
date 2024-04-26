package org.example;

import java.util.ArrayList;

public class User {
    private final String name;
    ArrayList<Message> sendingMessages = new ArrayList<>();
    ArrayList<Message> receivingMessages = new ArrayList<>();

    User(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
