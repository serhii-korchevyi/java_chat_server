package Controllers;

import Models.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class SignUpController implements HttpHandler {

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

        User user = new User(
                (String) map.get("first_name"),
                (String) map.get("last_name"),
                (String) map.get("login"),
                (String) map.get("password")
        );

        System.out.println(map.get("login"));
        System.out.println(map.get("password"));

        try {
            Connection connection  = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chat",
                    "root", "[password]");
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (first_name, last_name, login, password) VALUES" +
                    " ('" + user.getFirstName() + "','" + user.getLastName() + "', '" + user.getLogin() + "','" + user.getPassword() + "')"
            );
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
