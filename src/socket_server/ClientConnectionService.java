package socket_server;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class ClientConnectionService extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private long currentTread;
    public static LinkedList<ClientConnectionService> connectionList = new LinkedList<>();

    public ClientConnectionService(Socket socket) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            this.close(e.getMessage());
        }

        connectionList.add(this);
    }

    @Override
    public void run() {
        this.currentTread = Thread.currentThread().threadId();
        String incomingMessage;
        while (this.socket.isConnected()) {

            try {
                System.out.println("Before read line(" + this.currentTread + "):");
                incomingMessage = this.in.readLine();
                System.out.println("After read line(" + this.currentTread + "):" + incomingMessage);

                if (incomingMessage == null || incomingMessage.equals("exit")) {
                    this.close("exit");
                    break;
                } else {
                    this.sendMessage(incomingMessage);
                }
            } catch (IOException e) {
                this.close(e.getMessage());
                break;
            }
        }
    }

    private void sendMessage(String incomingMessage) {
        try {
            for (ClientConnectionService connection : connectionList) {
                System.out.println("New message1(" + this.currentTread + "): " + incomingMessage);
                connection.out.write(incomingMessage);
                connection.out.newLine();
                connection.out.flush();
            }
        } catch (IOException e) {
            this.close(e.getMessage());
        }
    }

    private void close(String message) {
        connectionList.remove(this);
        System.out.println(message);
        try {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage() + "(" + this.currentTread + ")");
        }
    }
}
