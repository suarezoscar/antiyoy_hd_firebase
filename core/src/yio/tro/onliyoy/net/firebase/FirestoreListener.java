package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Streaming real de Firestore REST: documents:listen.
 * <p>
 * Abre una conexión HTTP persistente (OkHttp) y parsea NDJSON; cuando el
 * documento cambia se notifica vía DocListener. Reconexión automática con
 * backoff simple. Nada de polling.
 */
public class FirestoreListener {

    public interface DocListener {
        void onDoc(JsonValue fields, String updateTime);
    }

    private static final String API_BASE = "https://firestore.googleapis.com/v1/";

    private final OkHttpClient http;
    private final String projectId;
    private final String apiKey;
    private final String databasePath;

    private volatile boolean running;
    private Thread thread;
    private Call currentCall;

    public FirestoreListener(String projectId, String apiKey) {
        this.projectId = projectId;
        this.apiKey = apiKey;
        this.http = new OkHttpClient();
        this.databasePath = "projects/" + projectId + "/databases/(default)";
    }

    public synchronized void listenDoc(String docPath, DocListener listener) {
        stop();
        running = true;
        Thread t = new Thread(() -> runLoop(docPath, listener), "firestore-listen");
        t.setDaemon(true);
        thread = t;
        t.start();
    }

    public synchronized void stop() {
        running = false;
        if (currentCall != null) {
            currentCall.cancel();
            currentCall = null;
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void runLoop(String docPath, DocListener listener) {
        while (running) {
            try {
                listenOnce(docPath, listener);
            } catch (Exception e) {
                if (running) System.out.println("FirestoreListener: reconnect needed (" + e.getClass().getSimpleName() + ")");
            }
            if (!running) break;
            try {
                Thread.sleep(2000); // backoff simple antes de reconectar
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void listenOnce(String docPath, DocListener listener) throws Exception {
        String docName = databasePath + "/documents/" + docPath;
        String requestJson = "{"
                + "\"database\":\"" + databasePath + "\","
                + "\"addTarget\":{\"documents\":{\"documents\":[\"" + docName + "\"]}}"
                + "}";

        Request request = new Request.Builder()
                .url(API_BASE + databasePath + "/documents:listen?key=" + apiKey)
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), requestJson))
                .build();

        currentCall = http.newCall(request);
        try (Response response = currentCall.execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("listen HTTP " + response.code());
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
            String line;
            while (running && (line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                JsonValue root = new JsonReader().parse(line);
                JsonValue dc = root.get("documentChange");
                if (dc == null) continue;
                JsonValue document = dc.get("document");
                if (document == null) continue;
                listener.onDoc(document.get("fields"), document.getString("updateTime"));
            }
        }
    }
}