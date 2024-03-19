package ayush.example.server;

import com.google.gson.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    private static final int PORT = 23456;
    private static final String SERVER_ADDRESS = "127.0.0.1";

    public static void main(String[] args) {

        System.out.println("Server started!");

        try (
                ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(SERVER_ADDRESS));
        ) {

//            while (!server.isClosed()) {

            Socket socket = server.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            Thread t = new ClientHandler(input, output, server);
            t.start();

//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class ClientHandler extends Thread {

        DataInputStream input;
        DataOutputStream output;
        ServerSocket server;

        public ClientHandler(DataInputStream input, DataOutputStream output, ServerSocket server) {
            this.input = input;
            this.output = output;
            this.server = server;
        }


        @Override
        public void run() {

            Response response = new Response();
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            Lock readLock = lock.readLock();
            Lock writeLock = lock.writeLock();

            try {
                Request request = new Gson().fromJson(input.readUTF(), Request.class);

                switch (request.getType()) {
                    case "get":
                        readLock.lock();
                        response = processGetRequest(request);
                        readLock.unlock();
                        server.close();
                        break;
                    case "set":
                        writeLock.lock();
                        response = processSetRequest(request);
                        writeLock.unlock();
                        server.close();
                        break;
                    case "delete":
                        writeLock.lock();
                        response = processDeleteRequest(request);
                        writeLock.unlock();
                        server.close();
                        break;
                    case "exit":
                        response.setResponse("OK");
                        output.writeUTF(new Gson().toJson(response));
                        server.close();
                        return;
                }

            } catch (IOException e) {
                System.out.println("got error");
                ;
            } finally {
                try {
                    output.writeUTF(new Gson().toJson(response));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static Response processDeleteRequest(Request request) {

        Path path = Paths.get("/Users/abarnawal/Maven_Implementation/JSON_Database__Maven_Testing/src/main/java/ayush/example/server/data/db.json");

        Response response = new Response();

        JsonElement keySet = request.getKey();

        try {
            String jsonString = new String(Files.readAllBytes(path));
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject originalJsonObject = jsonObject;
            JsonArray keyArray = keySet.getAsJsonArray();

            for (int index = 0; index < keyArray.size() - 1; index++) {
                String key = String.valueOf(keyArray.get(index));
                key = key.substring(1, key.length() - 1);
                if (jsonObject.has(key) && jsonObject.get(key).isJsonObject()) {
                    jsonObject = jsonObject.get(key).getAsJsonObject();
                }
            }

            String key = String.valueOf(keyArray.get(keyArray.size() - 1));
            key = key.substring(1, key.length() - 1);
            jsonObject.remove(key);

            response.setResponse("OK");
            return response;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static Response processSetRequest(Request request) {
        Path path = Paths.get("/Users/abarnawal/Maven_Implementation/JSON_Database__Maven_Testing/src/main/java/ayush/example/server/data/db.json");

        Response response = new Response();

        JsonElement keySet = request.getKey();

        if (keySet.isJsonPrimitive()) {

            try {
                String jsonString = new String(Files.readAllBytes(path));
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                JsonObject originalJsonObject = jsonObject;

                String key = String.valueOf(keySet);
                key = key.substring(1, key.length() - 1);

                JsonObject newJsonObject = new JsonObject();
                newJsonObject.add(key, request.getValue());

                response.setResponse("OK");
                return response;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } else {

            try {
                String jsonString = new String(Files.readAllBytes(path));
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                JsonObject originalJsonObject = jsonObject;
                JsonArray keyArray = keySet.getAsJsonArray();

                for (int index = 0; index < keyArray.size() - 1; index++) {
                    String key = String.valueOf(keyArray.get(index));
                    key = key.substring(1, key.length() - 1);
                    if (jsonObject.has(key) && jsonObject.get(key).isJsonObject()) {
                        jsonObject = jsonObject.get(key).getAsJsonObject();
                    }
                }

                String key = String.valueOf(keyArray.get(keyArray.size() - 1));
                key = key.substring(1, key.length() - 1);
                jsonObject.add(key, request.getValue());

                response.setResponse("OK");
                return response;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static Response processGetRequest(Request request) {

        Path path = Paths.get("/Users/abarnawal/Maven_Implementation/JSON_Database__Maven_Testing/src/main/java/ayush/example/server/data/db.json");

        Response response = new Response();

        JsonElement keySet = request.getKey();

        if (keySet.isJsonPrimitive()) {

            try (FileReader reader = new FileReader(path.toString())) {
                Request myObject = new Gson().fromJson(reader, Request.class);

//                System.out.println(request.getKey() + " " + myObject.getKey());

                JsonObject jsonObject = JsonParser.parseString(Files.readString(path)).getAsJsonObject();

//                System.out.println(request.getKey() + " " + jsonObject.get(request.getKey().getAsString()));

                if (jsonObject.has(keySet.getAsString())) {
                    response.setResponse("OK");
                    response.setValue(jsonObject.get(keySet.getAsString()));
                    return response;
                } else {
                    response.setResponse("ERROR");
                    response.setReason("No such key");
                    return response;
                }
//                if (request.getKey().equals(myObject.getKey())) {
//                    response.setResponse("OK");
//                    response.setValue(myObject.getValue());
//                    return response;
//                } else {
//                    response.setResponse("ERROR");
//                    response.setReason("No such key");
//                    return response;
//                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;

        } else {
            try {
                String jsonString = new String(Files.readAllBytes(path));
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                JsonArray keyArray = keySet.getAsJsonArray();

                for (int index = 0; index < keyArray.size() - 1; index++) {
                    String key = String.valueOf(keyArray.get(index));
                    key = key.substring(1, key.length() - 1);
                    if (jsonObject.has(key) && jsonObject.get(key).isJsonObject()) {
                        jsonObject = jsonObject.get(key).getAsJsonObject();
                    }
                }

                String key = String.valueOf(keyArray.get(keyArray.size() - 1));
                key = key.substring(1, key.length() - 1);
                JsonElement value = jsonObject.get(key);

                response.setResponse("OK");
                response.setValue(value);
                return response;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
