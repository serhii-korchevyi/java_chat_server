import http_server.HttpService;
import socket_server.SocketService;

public class Main {

    public static void main(String[] args) {

        SocketService socketService = new SocketService();
        socketService.start();

        HttpService httpService = new HttpService();
        httpService.start();
    }

}