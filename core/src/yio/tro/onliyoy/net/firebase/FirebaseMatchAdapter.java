package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Pegamento entre el juego y la Realtime Database.
 * <p>
 * Toda la escritura se hace con PATCH a paths concretos; el tiempo real llega por
 * SSE (RtdbListener) y se resuelve con un GET fresco del nodo (una lectura por
 * evento: barato con tráfico por-turno). El patron es "leer -> modificar -> PATCH":
 * sin transacciones; confiamos en el grupo (modelo de confianza).
 */
public class FirebaseMatchAdapter {

    public interface Callbacks {
        void onState(String status, String levelCode, String currentColor, long turnEndTime, int version);

        void onPlayers(JsonValue players);

        void onError(String message);
    }

    private final RtdbClient client;
    private final RtdbListener listener;
    private final String matchId;

    private Callbacks callbacks;
    private long lastUpdatedAt;

    public FirebaseMatchAdapter(String databaseUrl, String apiKey, String matchId) {
        this.matchId = matchId;
        this.client = new RtdbClient(databaseUrl, apiKey);
        this.listener = new RtdbListener(databaseUrl);
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public String getMatchId() {
        return matchId;
    }

    public RtdbClient getClient() {
        return client;
    }

    // ---------------------------------------------------------------- host

    /** Crea el nodo de partida (status=lobby, settings y players del host). */
    public void createMatch(String jsonRootMatch) {
        client.put("matches/" + matchId, jsonRootMatch, new SimpleWrite("create"));
    }

    /** Unirse reclamando un color: PATCH solo sobre ese color. */
    public void join(String color, String jsonPlayer) {
        String fields = "{\"" + color + "\":" + jsonPlayer + "}";
        client.patch("matches/" + matchId + "/players", fields, new SimpleWrite("join"));
    }

    // ------------------------------------------------------------- escritura

    /** PATCH general sobre el nodo de partida (levelCode, currentColor, turnEndTime, version, status...). */
    public void writeFields(String jsonFields) {
        client.patch("matches/" + matchId, "{" + jsonFields + "}", new SimpleWrite("write"));
    }

    // -------------------------------------------------------------- tiempo real

    public void startListening() {
        listener.listen("matches/" + matchId, (path, data) ->
                Gdx.app.postRunnable(this::refresh));
    }

    public void stop() {
        listener.stop();
    }

    public void refresh() {
        client.get("matches/" + matchId, new RtdbClient.Callback() {
            @Override
            public void onSuccess(JsonValue root) {
                if (callbacks == null || root == null) return;
                String status = root.getString("status", "lobby");
                String levelCode = root.has("levelCode") ? root.getString("levelCode") : null;
                String currentColor = root.has("currentColor") ? root.getString("currentColor") : null;
                long turnEndTime = root.has("turnEndTime") ? root.getLong("turnEndTime") : 0L;
                int version = root.has("version") ? root.getInt("version") : 0;
                callbacks.onState(status, levelCode, currentColor, turnEndTime, version);
                callbacks.onPlayers(root.get("players"));
            }

            @Override
            public void onError(Exception e) {
                if (callbacks != null) callbacks.onError(e.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- helper

    private SimpleWrite onWrite(final String what) {
        return new SimpleWrite(what);
    }

    private class SimpleWrite implements RtdbClient.Callback {
        private final String what;

        SimpleWrite(String what) {
            this.what = what;
        }

        @Override
        public void onSuccess(JsonValue root) {
            System.out.println("FirebaseMatchAdapter: " + what + " ok (" + matchId + ")");
        }

        @Override
        public void onError(Exception e) {
            if (callbacks != null) callbacks.onError(what + ": " + e.getMessage());
        }
    }
}