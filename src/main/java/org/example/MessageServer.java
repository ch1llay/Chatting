package org.example;

// Server side

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import static org.example.utils.Credentials.PORT;


public class MessageServer {
    private static final String ADDRESS = "localhost";
    private static ArrayList<ServerNode> serverNodes;

    public static void main(String[] args) throws IOException {
        var serverSocket = new ServerSocket(PORT);
        serverNodes = new ArrayList<>();

        while (true) {
            var socket = serverSocket.accept();
            var serverNode = new ServerNode(socket, serverNodes);
            serverNodes.add(serverNode);
            var t = new Thread(serverNode);
            t.start();
        }
    }
}

