package org.example.server;

// Server side

import org.example.lib.ServerNode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import static org.example.utils.Credentials.PORT;


public class MessageServer {
    private static final String ADDRESS = "localhost";
    private static ArrayList<ServerNode> serverNodes;

    public static void main(String[] args) throws IOException {
        var address = InetAddress.getAllByName("192.168.1.148");
        var serverSocket = new ServerSocket(PORT, 50, address[0]);
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

