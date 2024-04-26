package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.utils.Json;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
        } else {
            System.out.println("Registered new user: " + username);
            return true;
        }
    }

    public void Execute() throws IOException {
        var isSuccessInit = Init();

        if(!isSuccessInit){
            return;
        }

        while (true) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            var resp = in.readLine();

            var message = Json.fromJson(resp, Message.class);

            if (message != null) {
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

        System.out.println("Disconnected from " + socket.getInetAddress().toString());
        socket.close();
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
