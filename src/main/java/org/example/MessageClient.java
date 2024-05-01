package org.example;

// Client side

import org.example.utils.Json;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.example.utils.Credentials.PORT;
import static org.example.utils.Credentials.SERVER_ADDRESS;


public class MessageClient {

    private String username;
    private PrintWriter out;
    private BufferedReader in;


    public MessageClient(String username) throws IOException {
        this.username = username;
        Socket socket = new Socket(SERVER_ADDRESS, PORT);

        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(username);
    }

    public boolean Send(String usernameTo, String message) {
        var req = Json.toJson(new Message(message, username, usernameTo));
        out.println(req);
        return true;
    }

    public void StartReceiving(Consumer<Message> f) throws IOException {
        Runnable task = () -> {
            while (true) {
                System.out.println("Waiting for message...");
                String resp = null;
                try {
                    resp = in.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (resp == null) {
                    continue;
                }
                var message = Json.fromJson(resp, Message.class);
                f.accept(message);
            }
        };

        Thread thread = new Thread(task);
        thread.start();

    }
}