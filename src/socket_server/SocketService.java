package socket_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService extends Thread {

    public static final int PORT = 9234;
    public ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("socket server started");

            while (!this.serverSocket.isClosed()) {
                Socket socket = this.serverSocket.accept();
                System.out.println("A new client has connected");
                ClientConnectionService clientConnection = new ClientConnectionService(socket);
                clientConnection.start();
            }
        } catch (IOException e) {
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(e.getMessage());
        }
    }
}
