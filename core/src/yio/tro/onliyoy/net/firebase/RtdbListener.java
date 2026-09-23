package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Streaming SSE de Firebase Realtime Database (sin polling).
 * <p>
 * GET persistente con "Accept: text/event-stream"; eventos:
 * <ul>
 *   <li>event: put  — estado completo en "data"</li>
 *   <li>event: patch — cambios parciales en "data"</li>
 * </ul>
 * El "data" de cada evento es {"path": "...", "data": {...}}.
 */
public class RtdbListener {

    public interface DataListener {
        /** path relativo al nodo escuchado y el JSON del evento (put/patch). */
        void onData(String path, JsonValue data);
    }

    private final OkHttpClient http;
    private final String baseUrl;

    private volatile boolean running;
    private Thread thread;
    private Call currentCall;

    public RtdbListener(String baseUrl) {
        this.baseUrl = baseUrl;
        this.http = new OkHttpClient();
    }

    public synchronized void listen(String path, DataListener listener) {
        stop();
        running = true;
        Thread t = new Thread(() -> runLoop(path, listener), "rtdb-listen");
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

    private void runLoop(String path, DataListener listener) {
        while (running) {
            try {
                stream(path, listener);
            } catch (Exception e) {
                if (running) System.out.println("RtdbListener: reconnect (" + e.getClass().getSimpleName() + ")");
            }
            if (!running) break;
            try {
                Thread.sleep(2000); // backoff simple
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void stream(String path, DataListener listener) throws IOException {
        String url = baseUrl + (path.length() == 0 ? "" : "/" + path) + ".json";
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "text/event-stream")
                .build();
        currentCall = http.newCall(request);
        try (Response response = currentCall.execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code());
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
            StringBuilder dataBuffer = new StringBuilder();
            String line;
            while (running && (line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    processBlock(dataBuffer, listener);
                    dataBuffer.setLength(0);
                    continue;
                }
                if (line.startsWith("data:")) {
                    if (dataBuffer.length() > 0) dataBuffer.append('\n');
                    dataBuffer.append(line.substring(5).trim());
                }
            }
        }
    }

    private void processBlock(StringBuilder dataBuffer, DataListener listener) {
        if (dataBuffer.length() == 0) return;
        try {
            JsonValue root = new JsonReader().parse(dataBuffer.toString());
            String path = root.has("path") ? root.getString("path") : "/";
            JsonValue data = root.get("data");
            if (data != null) listener.onData(path, data);
        } catch (Exception e) {
            // evento no JSON o keep-alive: ignorar
        }
    }
}