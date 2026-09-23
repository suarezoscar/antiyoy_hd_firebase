package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Cliente REST de Firebase Realtime Database (sin SDK).
 * <p>
 * JSON plano (no "tipado" como Firestore): PUT sobrescribe, PATCH fusiona campos.
 * Con reglas abiertas (test mode) no hace falta autenticación.
 */
public class RtdbClient {

    public interface Callback {
        void onSuccess(JsonValue root);

        void onError(Exception e);
    }

    private final OkHttpClient http;
    private final String baseUrl;   // https://<proyecto>-default-rtdb.<region>.firebasedatabase.app
    private final String apiKey;    // reservado para futura auth OAuth; sin uso con reglas abiertas

    public RtdbClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.http = new OkHttpClient();
    }

    private String endpoint(String path) {
        String url = baseUrl;
        if (path != null && path.length() > 0) url += "/" + path;
        url += ".json";
        return url;
    }

    public void get(String path, Callback cb) {
        Request r = new Request.Builder().url(endpoint(path)).get().build();
        http.newCall(r).enqueue(new SimpleCallback(cb));
    }

    public void put(String path, String json, Callback cb) {
        send("PUT", path, json, cb);
    }

    public void patch(String path, String json, Callback cb) {
        send("PATCH", path, json, cb);
    }

    public void delete(String path, Callback cb) {
        Request r = new Request.Builder().url(endpoint(path)).delete().build();
        http.newCall(r).enqueue(new SimpleCallback(cb));
    }

    private void send(String method, String path, String json, Callback cb) {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
        Request.Builder b = new Request.Builder().url(endpoint(path));
        Request r = "PUT".equals(method) ? b.put(body).build() : b.patch(body).build();
        http.newCall(r).enqueue(new SimpleCallback(cb));
    }

    public static String quote(String s) {
        return s == null ? "\"\"" : "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static class SimpleCallback implements okhttp3.Callback {
        private final Callback cb;

        SimpleCallback(Callback cb) {
            this.cb = cb;
        }

        @Override
        public void onResponse(okhttp3.Call call, Response response) throws IOException {
            String body = response.body() == null ? null : response.body().string();
            if (!response.isSuccessful()) {
                cb.onError(new RtdbException("HTTP " + response.code() + ": " + body));
                return;
            }
            try {
                JsonValue root = (body == null || body.length() == 0) ? null : new JsonReader().parse(body);
                cb.onSuccess(root);
            } catch (Exception e) {
                cb.onError(new RtdbException("JSON parse: " + body, e));
            }
        }

        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            cb.onError(e);
        }
    }

    public static class RtdbException extends RuntimeException {
        public RtdbException(String m) {
            super(m);
        }

        public RtdbException(String m, Throwable c) {
            super(m, c);
        }
    }
}