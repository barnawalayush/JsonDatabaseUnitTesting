package ayush.example.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import ayush.example.server.Request;
import ayush.example.server.converter.JsonElementConverter;

public class Main {

    private static final int PORT = 23456;
    private static final String SERVER_ADDRESS = "127.0.0.1";

    @Parameter(names = "-t", description = "Type Of Operation")
    static String typeOfOperation;
    @Parameter(names = "-k", converter = JsonElementConverter.class, description = "key")
    static JsonElement key;
    @Parameter(names = "-v", converter = JsonElementConverter.class, description = "value")
    static JsonElement value;
    @Parameter(names = "-in", description = "file name")
    static String fileName;

    public static void main(String[] args) {

        Request request;

        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);

        try (
                Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS), PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");

            if (fileName == null) {
                request = new Request(typeOfOperation, key, value);
            } else {
                String content = new String(Files.readAllBytes(Paths.get("/Users/abarnawal/Maven_Implementation/JSON_Database__Maven_Testing/src/main/java/ayush/example/client/data/" + fileName)));
//                System.out.println(content);
                request = new Gson().fromJson(content, Request.class);
//                System.out.println(request.getKey() + " " + request.getValue());
            }

            String sendMessage = new Gson().toJson(request);
//            System.out.println(sendMessage);
            output.writeUTF(sendMessage);
            System.out.println("Sent: " + sendMessage);

            String receivedMessage = input.readUTF();
            System.out.println("Received: " + receivedMessage);
            fileName = null;

        } catch (IOException e) {
            System.out.println("got error");;
        }

    }
}