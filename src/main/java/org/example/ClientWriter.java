package org.example;

import org.example.utils.Json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientWriter implements Runnable{

    private PrintWriter out;
    private String username;

    public ClientWriter(PrintWriter out, String username) {
        this.out = out;
        this.username = username;
    }


    private void Execute() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while(true){
            while (true) {
                System.out.print("To: ");
                String to = scanner.nextLine();
                System.out.print("Message: ");
                String message = scanner.nextLine();
                if (message != null) {
                    var req = Json.toJson(new Message(message, username, to));
                    out.println(req);
                } else {
                    break;
                    // The server has disconnected
                }
            }
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
