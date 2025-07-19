package Controllers;

import Models.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class SignInController implements HttpHandler {

    private User user;

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
            ResultSet result = statement.executeQuery("SELECT * FROM users WHERE login = '" + map.get("login") + "'");

            while (result.next()) {
                this.user = new User(
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getString("login"),
                        result.getString("password")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!map.get("password").equals(this.user.getPassword())) {
            String response = "Invalid password";
            exchange.sendResponseHeaders(400, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();

            throw new RuntimeException(response);
        } else {
            String response = "Success";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }
}
