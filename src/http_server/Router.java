package http_server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class Router implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        StringBuilder bodyBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bodyBuilder.append(line);
            }
        }
        String requestBody = bodyBuilder.toString();




        System.out.println("pre line");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("after line");
        HashMap<String, Object> map = mapper.readValue(requestBody, new TypeReference<>() {});
        System.out.println(map.get("login"));
        System.out.println(map.get("password"));

        try {
            Connection connection  = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chat",
                    "root", "[password]");
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (login, password) VALUES ('" + map.get("login") + "','" + map.get("password") + "')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String response = "Test response";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
