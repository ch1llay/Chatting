package org.example;

// Client side

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class MessageClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);

        // Register with the server and send messages
        var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("Enter user name");
        Scanner scanner = new Scanner(System.in);

        String username = scanner.nextLine();
        out.println(username);

        var listener = new ClientListener(in);

        var tListener = new Thread(listener);
        tListener.start();

        var writer = new ClientWriter(out, username);

        var tWriter = new Thread(writer);
        tWriter.start();

        while (true) {}

        //System.out.println("Disconnected from " + socket.getInetAddress().toString());
        //socket.close();
    }
}