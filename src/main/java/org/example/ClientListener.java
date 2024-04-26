package org.example;

import org.example.utils.Json;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientListener implements Runnable{

    private BufferedReader in;

    public ClientListener(BufferedReader in) {
        this.in = in;
    }

    private void Execute() throws IOException {
        while(true){
            var resp = in.readLine();
            if(resp == null){
                continue;
            }
            var message = Json.fromJson(resp, Message.class);
            System.out.printf("%s:%s\n", message.From, message.Text);
        }
    }
    @Override
    public void run() {
        try {
            Execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
