package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.utils.Json;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerNode implements Runnable {

    private final Socket socket;
    private String username;

    BufferedReader in;
    PrintWriter out;

    private final ArrayList<ServerNode> serverNodes;

    public ServerNode(Socket socket, ArrayList<ServerNode> serverNodes) {
        this.socket = socket;
        this.serverNodes = serverNodes;
    }

    private boolean Init() throws IOException {
        System.out.println("New connection from " + socket.getInetAddress().toString());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        username = in.readLine();

        if (username == null) {
            return false;
        }
        else {
            var c = serverNodes.stream().filter(x -> x.username.equals(username)).count();
            System.out.println(c);
            if (c > 1) {
                return false;
            }

            System.out.println("Registered new user: " + username);
            return true;
        }
    }

    public void Execute() throws IOException {
        var isSuccessInit = Init();

        if (!isSuccessInit) {
            System.out.println(false);
            return;
        }
        try {
            while (true) {

                var resp = in.readLine();

                var message = Json.fromJson(resp, Message.class);

                if (message != null) {
                    if (message.Command != null) {
                        if (message.Command.equals("get users")) {
                            System.out.println("users");
                            var users = serverNodes.stream().map(x -> x.username).collect(Collectors.joining(","));
                            var serverResp = new Message(users);
                            serverResp.To = username;
                            serverResp.Command = message.Command;
                            sendTo(serverResp);
                            continue;
                        }
                    }
                    System.out.printf("from %s: %s\n", username, resp);
                    sendTo(message);
//                    for (Map.Entry<String, List<Message>> entry : messages.entrySet()) {
//                        if (!entry.getKey().equals(username)) {
//                            entry.getValue().add(new Message(message));
//                        }
//                    }
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            serverNodes.remove(serverNodes.stream().filter(x -> x.username.equals(username)).findFirst().get());
            System.out.println("Disconnected from " + socket.getInetAddress().toString());
            socket.close();
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            Execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendTo(Message message) {
        var node = serverNodes.stream().filter(x -> x.username.equals(message.To)).findFirst();
        if (node.isEmpty()) {
            return false;
        }

        node.get().Send(message);

        return true;
    }

    public void Send(Message message) {
        out.println(Json.toJson(message));
    }
}
