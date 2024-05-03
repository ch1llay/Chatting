package org.example.lib;

import org.example.User;
import org.example.utils.Json;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ServerNode implements Runnable {

    private final Socket socket;
    private String username;
    private User user;

    BufferedReader in;
    PrintWriter out;

    private final ArrayList<ServerNode> serverNodes;
    private final HashMap<String, User> users;

    public ServerNode(Socket socket, ArrayList<ServerNode> serverNodes, HashMap<String, User> users) {
        this.socket = socket;
        this.serverNodes = serverNodes;
        this.users = users;
    }

    private boolean Init() throws IOException {
        var auth = false;

        System.out.println("New connection from " + socket.getInetAddress().toString());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        username = in.readLine();

        if (username != null) {
            var c = serverNodes.stream().filter(x -> x.username.equals(username)).count();
            System.out.println(c);
            if (c <= 1) {
                auth = true;

                if(!users.containsKey(username)) {
                    user = new User(username);
                    users.put(username, user);
                }
                else{
                    user = (User)users.get(username);
                }
            }

            if(auth){
                var resp = new Message("auth");
                out.println(Json.toJson(resp));
                System.out.println(user.toString());
            }
            else{
                var resp = new Message("not auth");
                out.println(Json.toJson(resp));
            }

        }

        return auth;

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
                            var users = serverNodes.stream().filter(x -> !x.username.equals(username)).map(x -> x.username).collect(Collectors.joining(","));
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
            System.out.println(String.format("user %s disconnected from %s", username, socket.getInetAddress().toString()));
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

    public void sendTo(Message message) {
        var node = serverNodes.stream().filter(x -> x.username.equals(message.To)).findFirst();
        if (node.isEmpty()) {
            return;
        }

        user.AddSendingMessage(message);

        if(users.containsKey(message.To)){
            var userRecipient = users.get(message.To);
            userRecipient.AddReceivingMessage(message);
        }

        node.get().Send(message);
    }

    public void Send(Message message) {
        out.println(Json.toJson(message));
    }
}
