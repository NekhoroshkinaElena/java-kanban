
import http.KVServer;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class HTTPTaskManagerTest extends TasksManagerTest<HTTPTaskManager> {
    static KVServer kvServer;

    @Override
    HTTPTaskManager getTaskManager() {
        try {
            return HTTPTaskManager.loadFromKVServer(new URL("http://localhost:8078"),
                    String.valueOf(new Random()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @BeforeAll
    static public void startServer() {
        assertDoesNotThrow(() -> kvServer = new KVServer());
        kvServer.start();
    }

    @AfterAll
    static public void stopServer() {
        kvServer.stop();
    }
}
