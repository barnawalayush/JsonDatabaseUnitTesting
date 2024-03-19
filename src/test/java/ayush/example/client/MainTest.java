package ayush.example.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testMainWithGetOperation() {

        Thread serverThread = new Thread(() -> {
            ayush.example.server.Main.main(new String[]{});
        });
        serverThread.start();
        try {
            serverThread.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Main.main(new String[]{"-t", "get", "-k", "1"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"get\",\"key\":\"1\"}\n" +
                "Received: {\"response\":\"ERROR\",\"reason\":\"No such key\"}\n", outputStream.toString());

    }

    @Test
    void testMainWithFile() {

        Thread serverThread = new Thread(() -> {
            ayush.example.server.Main.main(new String[]{});
        });
        serverThread.start();
        try {
            serverThread.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Main.main(new String[]{"-in", "setFile.json"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "Sent: {\"type\":\"set\",\"key\":\"person\",\"value\":{\"name\":\"Elon Musk\",\"car\":{\"model\":\"Tesla Roadster\",\"year\":\"2018\"},\"rocket\":{\"name\":\"Falcon 9\",\"launches\":\"88\"}}}\n" +
                "Received: {\"response\":\"OK\"}\n", outputStream.toString());
    }

    @Test
    void testMainWIthException() {
        Thread serverThread = new Thread(() -> {
            ayush.example.server.Main.main(new String[]{});
        });
        serverThread.start();
        try {
            serverThread.join(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Main.main(new String[]{"-in", "dummyFile.json"});

        assertEquals("Server started!\n" +
                "Client started!\n" +
                "got error\n", outputStream.toString());
    }

}