package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Cliente REST de Firestore (sin SDK: solo projectId + apiKey).
 * <p>
 * Todo es asíncrono (OkHttp enqueue); los callbacks llegan en hilos de OkHttp,
 * así que el llamante debe saltar al hilo de render con Gdx.app.postRunnable().
 * <p>
 * Operaciones:
 * <ul>
 *   <li>getDoc(path)</li>
 *   <li>createDoc(colPath, docId, fieldsJson) — 409 si ya existe</li>
 *   <li>commit(docPath, fieldsJson, fieldPaths, preconditionUpdateTime) — candado optimista</li>
 * </ul>
 */
public class FirestoreClient {

    public interface NetCallback {
        void onSuccess(JsonValue root);

        void onError(Exception e);
    }

    private static final String API_BASE = "https://firestore.googleapis.com/v1/";

    private final OkHttpClient http;
    private final String projectId;
    private final String apiKey;
    private final String databasePath; // projects/{pid}/databases/(default)

    public FirestoreClient(String projectId, String apiKey) {
        this.projectId = projectId;
        this.apiKey = apiKey;
        this.http = new OkHttpClient();
        this.databasePath = "projects/" + projectId + "/databases/(default)";
    }

    public String documentName(String path) {
        return databasePath + "/documents/" + path;
    }

    public String documentUrl(String path) {
        return API_BASE + databasePath + "/documents/" + path + "?key=" + apiKey;
    }

    /** GET de un documento. root tiene "fields" y "updateTime". */
    public void getDoc(String path, NetCallback cb) {
        Request request = new Request.Builder().url(documentUrl(path)).get().build();
        http.newCall(request).enqueue(new SimpleCallback(cb));
    }

    /** Crea un documento en la colección de primer nivel ("matches/M1" -> collection "matches"). */
    public void createDoc(String collectionPath, String docId, String fieldsJson, NetCallback cb) {
        String url = API_BASE + databasePath + "/documents/" + collectionPath + "?documentId=" + docId + "&key=" + apiKey;
        RequestBody body = RequestBody.create(okhttp3.MediaType.get("application/json; charset=utf-8"), FirestoreJson.fields(fieldsJson));
        Request request = new Request.Builder().url(url).post(body).build();
        http.newCall(request).enqueue(new SimpleCallback(cb));
    }

    /**
     * Escribe con candado optimista. Si preconditionUpdateTime != null, Firestore
     * devuelve FAILED_PRECONDITION (409) si el doc cambió desde esa lectura.
     */
    public void commit(String docPath, String fieldsJson, String[] fieldPaths, String preconditionUpdateTime, NetCallback cb) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"writes\":[{");
        sb.append("\"update\":{");
        sb.append("\"name\":\"").append(documentName(docPath)).append("\",");
        sb.append("\"fields\":{").append(fieldsJson).append("}");
        if (fieldPaths != null && fieldPaths.length > 0) {
            sb.append(",\"updateMask\":{\"fieldPaths\":[");
            for (int i = 0; i < fieldPaths.length; i++) {
                if (i > 0) sb.append(',');
                sb.append('"').append(fieldPaths[i]).append('"');
            }
            sb.append("]}");
        }
        sb.append("}");
        if (preconditionUpdateTime != null) {
            sb.append(",\"currentDocument\":{\"updateTime\":\"").append(preconditionUpdateTime).append("\"}");
        }
        sb.append("}]}");

        String url = API_BASE + databasePath + "/documents:commit?key=" + apiKey;
        RequestBody body = RequestBody.create(okhttp3.MediaType.get("application/json; charset=utf-8"), sb.toString());
        Request request = new Request.Builder().url(url).post(body).build();
        http.newCall(request).enqueue(new SimpleCallback(cb));
    }

    /** Valor "desempaquetado" de un campo tipado: "stringValue"/"integerValue"/"booleanValue". */
    public static String valueOf(JsonValue field) {
        if (field == null) return null;
        for (String type : new String[]{"stringValue", "integerValue", "booleanValue", "doubleValue"}) {
            JsonValue v = field.get(type);
            if (v != null) return v.asString();
        }
        return null;
    }

    private static class SimpleCallback implements okhttp3.Callback {
        private final NetCallback cb;

        SimpleCallback(NetCallback cb) {
            this.cb = cb;
        }

        @Override
        public void onResponse(okhttp3.Call call, Response response) throws IOException {
            String body = response.body() == null ? null : response.body().string();
            if (!response.isSuccessful()) {
                cb.onError(new FirestoreException("HTTP " + response.code() + ": " + body));
                return;
            }
            try {
                JsonValue root = (body == null || body.length() == 0)
                        ? new JsonValue(com.badlogic.gdx.utils.JsonValue.ValueType.object)
                        : new JsonReader().parse(body);
                cb.onSuccess(root);
            } catch (Exception e) {
                cb.onError(new FirestoreException("JSON parse error: " + body, e));
            }
        }

        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            cb.onError(e);
        }
    }

    public static class FirestoreException extends RuntimeException {
        public final String httpMessage;

        public FirestoreException(String message) {
            super(message);
            this.httpMessage = message;
        }

        public FirestoreException(String message, Throwable cause) {
            super(message, cause);
            this.httpMessage = message;
        }
    }
}