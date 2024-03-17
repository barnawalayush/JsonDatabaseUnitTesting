package ayush.example.server;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.*;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp(){
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @Order(1)
    void aGetRequestWithWrongKey() {

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-t", "get", "-k", "1"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"get\",\"key\":\"1\"}\n" +
                "Received: {\"response\":\"ERROR\",\"reason\":\"No such key\"}\n", outputStream.toString());
    }

    @Test
    @Order(2)
    void bGetRequestWithCorrectPrimitiveKey() {

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-t", "get", "-k", "person"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"get\",\"key\":\"person\"}\n" +
                "Received: {\"response\":\"OK\",\"value\":{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\",\"year\":\"2018\"},\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}\n", outputStream.toString());
    }

    @Test
    @Order(3)
    void cGetRequestWithCorrectNonPrimitiveKey() {

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-in", "getFile.json"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"get\",\"key\":[\"person\",\"name\"]}\n" +
                "Received: {\"response\":\"OK\",\"value\":\"Elon Musk\"}\n", outputStream.toString());
    }

    @Test
    @Order(4)
    void dSetRequestWithPrimitiveKey(){

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-in", "secondSetFile.json"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"set\",\"key\":[\"person\",\"name\"],\"value\":\"Mark\"}\n" +
                "Received: {\"response\":\"OK\"}\n", outputStream.toString());
    }

    @Test
    @Order(5)
    void eSetRequestWithNonPrimitiveKey(){

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-in", "setFile.json"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"set\",\"key\":\"person\",\"value\":{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\",\"year\":\"2018\"},\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}\n" +
                "Received: {\"response\":\"OK\"}\n", outputStream.toString());
    }

    @Test@Order(6)
    void fDeleteRequest() {

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-in", "deleteFile.json"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"delete\",\"key\":[\"person\",\"car\",\"year\"]}\n" +
                "Received: {\"response\":\"OK\"}\n", outputStream.toString());
    }

    @Test
    @Order(7)
    void exitRequest(){

        Thread t = new Thread(() -> {
            Main.main(new String[]{});
        });
        t.start();

        try {
            t.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ayush.example.client.Main.main(new String[]{"-t", "exit"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"exit\",\"key\":\"person\"}\n" +
                "Received: {\"response\":\"OK\"}\n", outputStream.toString());

    }

}