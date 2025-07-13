package http_server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpService extends Thread {

    HttpServer httpServer;

    public void run() {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(8111), 0);

            this.httpServer.createContext("/sign-up", new Router());

            this.httpServer.start();

            System.out.println("http server started");
        } catch (IOException e) {
            if (this.httpServer != null) {
                this.httpServer.stop(0);
            }
            System.out.println(e.getMessage());
        }
    }
}
