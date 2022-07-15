package managers;

import http.KVClient;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVClient kvClient;
    private final String key;

    public HTTPTaskManager(URL url, String key) {
        super("");
        kvClient = new KVClient(url);
        this.key = key;
    }

    public static HTTPTaskManager loadFromKVServer(URL url, String key) {
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(url, key);
        httpTaskManager.read(new StringReader(httpTaskManager.kvClient.load(httpTaskManager.key)));
        return httpTaskManager;
    }

    @Override
    public void save() {
        StringWriter sw = new StringWriter();
        this.write(sw);
        kvClient.put(key, sw.toString());
    }
}
