package org.example.server;

// Server side

import com.google.gson.reflect.TypeToken;
import org.example.User;
import org.example.lib.ServerNode;
import org.example.utils.Json;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

import static org.example.utils.Credentials.PORT;
import static org.example.utils.Credentials.SERVER_ADDRESS;


public class MessageServer {
    private static ArrayList<ServerNode> serverNodes;
    private static HashMap<String, User> users;

    private static void writeToFile() {
        try (FileWriter writer = new FileWriter("data.json", false)) {
            // запись всей строки
            String text = Json.toJson(users);
            writer.write(text);

            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void getFromFile() {
        try (FileReader reader = new FileReader("data.json")) {
            StringBuilder stringBuilder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }

            var data = stringBuilder.toString();
            Type mapType = new TypeToken<HashMap<String, User>>() {
            }.getType();

            users = Json.fromJsonMap(data);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            writeToFile();
            System.out.println("Shutting down...");
        }));

        getFromFile();


        var address = InetAddress.getAllByName(SERVER_ADDRESS);
        var serverSocket = new ServerSocket(PORT, 50, address[0]);
        serverNodes = new ArrayList<>();

        while (true) {
            var socket = serverSocket.accept();
            var serverNode = new ServerNode(socket, serverNodes, users);
            serverNodes.add(serverNode);
            var t = new Thread(serverNode);
            t.start();
        }
    }
}

