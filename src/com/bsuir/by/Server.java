package com.bsuir.by;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void closeConnection(ServerSocket serverSocket, Socket clientAccepted, ObjectInputStream sois, ObjectOutputStream soos) {
        try {
            if (sois != null) {
                sois.close();
            }
            if (soos != null) {
                soos.close();
            }
            if (clientAccepted != null) {
                clientAccepted.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
