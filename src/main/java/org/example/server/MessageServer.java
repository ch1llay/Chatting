package org.example.server;

// Server side

import org.example.User;
import org.example.lib.ServerNode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import static org.example.utils.Credentials.PORT;
import static org.example.utils.Credentials.SERVER_ADDRESS;


public class MessageServer {
    private static ArrayList<ServerNode> serverNodes;
    private static ArrayList<User> users;

    public static void main(String[] args) throws IOException {
        var address = InetAddress.getAllByName(SERVER_ADDRESS);
        var serverSocket = new ServerSocket(PORT, 50, address[0]);
        serverNodes = new ArrayList<>();
        users = new ArrayList<>();

        while (true) {
            var socket = serverSocket.accept();
            var serverNode = new ServerNode(socket, serverNodes, users);
            serverNodes.add(serverNode);
            var t = new Thread(serverNode);
            t.start();
        }
    }
}

