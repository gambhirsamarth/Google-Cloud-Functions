package org.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App implements HttpFunction {

    private static final Gson gson = new Gson();
    String url = "jdbc:mysql://localhost:3306/product";
    String username = "root";
    String password = "root";

    public static void main(String[] args) {
        System.out.println( "Hello World!" );
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse ) throws Exception {

        // create writer object
        BufferedWriter writer = httpResponse.getWriter();

        if (httpRequest.getMethod().equals("POST")) {
            // get the requestBody type
            String contentType = httpRequest.getContentType().orElse("");
            // if it is json
            if (contentType.equals("application/json")) {

                // create JSON reader from gson object
                JsonObject body = gson.fromJson(httpRequest.getReader(), JsonObject.class);

                // if body has key "id"
                if (body.has("id")) {


                    try {
                        Connection connection = DriverManager.getConnection(url, username, password);
                        String sqlQuery = "SELECT id, name, client_id from product where id = " + body.get("id");
                        PreparedStatement statement = connection.prepareStatement(sqlQuery);
                        ResultSet resultSet = statement.executeQuery();
                        resultSet.next();

                        writer.write(
                                "Id: " + resultSet.getString("id") + "\n" +
                                        "Client Id: " + resultSet.getString("client_id") + "\n" +
                                        "Name: " + resultSet.getString("name") + "\n"
                        );
                    } catch (Exception e) {
                        writer.write(String.valueOf(e));
                    }
                }
                else{
                    writer.write("Enter Id");
                }
            }
            else{
                writer.write("Body not of JSON type");
            }
        }
        else {
            writer.write("Method not suitable");
        }
    }
}

