package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.JsonValue;

import java.util.UUID;

import yio.tro.onliyoy.YioGdxGame;
import yio.tro.onliyoy.game.core_model.events.AbstractEvent;
import yio.tro.onliyoy.game.core_model.events.EventType;
import yio.tro.onliyoy.game.core_model.events.IEventListener;
import yio.tro.onliyoy.game.export_import.ExportParameters;
import yio.tro.onliyoy.game.general.GameController;
import yio.tro.onliyoy.game.general.ObjectsLayer;
import yio.tro.onliyoy.game.loading.LoadingParameters;
import yio.tro.onliyoy.game.loading.LoadingType;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;

/**
 * Orquestador del modo Firebase (BYO-backend sobre RTDB).
 * <p>
 * Identidad (id/name en prefs), creación de partida, unirse por enlace, lobby en
 * vivo (SSE), lanzar partida (generar mapa y publicarlo) y sincronización de turnos
 * (serializar tras cada turn_end y reimportar los cambios remotos).
 */
public class FirebaseGameManager {

    public static final String[] COLORS = { "aqua", "cyan", "yellow", "brown", "blue", "purple" };

    public interface LobbyListener {
        void onLobby(JsonValue players);
    }

    private final YioGdxGame yioGdxGame;
    private final FirebaseConfig config;
    private final Preferences prefs;

    private FirebaseMatchAdapter adapter;
    private LobbyListener lobbyListener;
    private String matchId = "-";
    private String myColor = "-";
    private String creatorId = "-";
    private JsonValue playersCache;
    private int lastVersion = 0;
    private int writtenVersion = 0;
    private boolean registeredTurnListener;
    private long turnEndTimeMillis = 0;

    public int turnSeconds = 60;
    public String levelSize = "small";

    public FirebaseGameManager(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        this.config = new FirebaseConfig();
        this.prefs = Gdx.app.getPreferences(FirebaseConfig.PREFS_NAME);
    }

    public FirebaseConfig getConfig() {
        return config;
    }

    // ------------------------------------------------------------- identidad

    public String getPlayerId() {
        String id = prefs.getString("player_id", null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.putString("player_id", id).flush();
        }
        return id;
    }

    public String getPlayerName() {
        String name = prefs.getString("player_name", null);
        if (name == null) {
            name = "Guest" + (100 + (int) (Math.random() * 900));
            prefs.putString("player_name", name).flush();
        }
        return name;
    }

    public void setPlayerName(String name) {
        prefs.putString("player_name", name).flush();
    }

    // ------------------------------------------------------------- acceso

    public FirebaseMatchAdapter getAdapter() {
        return adapter;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMyColor() {
        return myColor;
    }

    public boolean isHost() {
        return "-".equals(creatorId) || creatorId.equals(getPlayerId());
    }

    public long getTurnEndTimeMillis() {
        return turnEndTimeMillis;
    }

    /** Renueva el deadline local inmediatamente (evita doble fin forzado antes del roundtrip SSE). */
    public void extendLocalDeadline() {
        turnEndTimeMillis = System.currentTimeMillis() + turnSeconds * 1000L;
    }

    public JsonValue getPlayers() {
        return playersCache;
    }

    public void setLobbyListener(LobbyListener listener) {
        this.lobbyListener = listener;
    }

    public String getJoinLink() {
        if (!config.isConfigured()) return null;
        return FirebaseConfig.buildJoinLink(config.getDatabaseUrl(), config.getApiKey(), matchId);
    }

    // ----------------------------------------------------------------- crear

    public void createMatch() {
        if (!config.isConfigured()) {
            System.out.println("FirebaseGameManager: base no configurada");
            return;
        }
        matchId = Math.abs(UUID.randomUUID().getLeastSignificantBits()) + "";
        myColor = "aqua";
        creatorId = getPlayerId();
        playersCache = null;
        lastVersion = 1;

        FirebaseMatchAdapter fresh = new FirebaseMatchAdapter(config.getDatabaseUrl(), config.getApiKey(), matchId);
        adapter = fresh;

        String matchJson = "{"
                + "\"status\":" + q("lobby")
                + ",\"creatorId\":" + q(getPlayerId())
                + ",\"creatorName\":" + q(getPlayerName())
                + ",\"turnSeconds\":" + turnSeconds
                + ",\"levelSize\":" + q(levelSize)
                + ",\"players\":{"
                + "\"" + myColor + "\":{\"id\":" + q(getPlayerId()) + ",\"name\":" + q(getPlayerName()) + "}"
                + "}"
                + ",\"version\":" + lastVersion
                + "}";
        fresh.createMatch(matchJson);
        startListening();
    }

    // ----------------------------------------------------------------- unir

    public void join(String databaseUrl, String apiKey, String matchId) {
        this.matchId = matchId;
        putMatchIdOnPrefs(matchId);

        FirebaseMatchAdapter fresh = new FirebaseMatchAdapter(databaseUrl, apiKey, matchId);
        adapter = fresh;
        fresh.getClient().get("matches/" + matchId, new RtdbClient.Callback() {
            @Override
            public void onSuccess(JsonValue root) {
                if (root == null) {
                    System.out.println("FirebaseGameManager: partida no existe");
                    return;
                }
                creatorId = root.has("creatorId") ? root.getString("creatorId") : "-";
                long ts = root.has("turnSeconds") ? root.getLong("turnSeconds") : 60L;
                turnSeconds = (int) ts;
                String color = pickFreeColor(root.get("players"));
                if (color == null) {
                    System.out.println("FirebaseGameManager: sin color libre");
                    return;
                }
                myColor = color;
                fresh.join(color, playerJson());
                adapter.refresh();
                startListening();
            }

            @Override
            public void onError(Exception e) {
                System.out.println("FirebaseGameManager.join error: " + e.getMessage());
            }
        });
    }

    private void putMatchIdOnPrefs(String matchId) {
        prefs.putString("last_match_id", matchId).flush();
    }

    private String playerJson() {
        return "{\"id\":" + q(getPlayerId()) + ",\"name\":" + q(getPlayerName()) + "}";
    }

    private String pickFreeColor(JsonValue players) {
        if (players == null) return COLORS[0];
        for (String color : COLORS) {
            if (players.get(color) == null) return color;
        }
        return null;
    }

    private String firstColor(JsonValue players) {
        for (String color : COLORS) {
            if (players != null && players.get(color) != null) return color;
        }
        return COLORS[0];
    }

    // --------------------------------------------------------- tiempo real

    public void startListening() {
        if (adapter == null) return;
        adapter.setCallbacks(new FirebaseMatchAdapter.Callbacks() {
            @Override
            public void onState(String status, String levelCode, String currentColor, long turnEndTime, int version) {
                lastVersion = version;
                turnEndTimeMillis = turnEndTime;
                if (!"playing".equals(status)) return;
                if (levelCode == null || levelCode.length() < 3) return;
                if (writtenVersion > 0 && version <= writtenVersion) return; // eco propio: no reimportar
                syncTo(levelCode);
            }

            @Override
            public void onPlayers(JsonValue players) {
                playersCache = players;
                if (lobbyListener != null) lobbyListener.onLobby(players);
            }

            @Override
            public void onError(String message) {
                System.out.println("FirebaseGameManager: " + message);
            }
        });
        adapter.startListening();
    }

    public void stop() {
        if (adapter != null) adapter.stop();
    }

    // ------------------------------------------------------------- lanzar

    public void launch() {
        if (adapter == null) {
            System.out.println("FirebaseGameManager.launch: sin adapter");
            return;
        }
        JsonValue players = playersCache;
        if (players == null || players.size == 0) {
            System.out.println("FirebaseGameManager.launch: sin jugadores");
            return;
        }

        LoadingParameters p = new LoadingParameters();
        p.add("level_size", levelSize);
        p.add("rules_type", "def");
        p.add("diplomacy", "false");
        p.add("fog_of_war", "false");
        p.add("entities", buildEntitiesString(players));
        yioGdxGame.loadingManager.startInstantly(LoadingType.training_create, p);

        String levelCode = serialize();
        String firstColor = firstColor(players);
        long deadline = System.currentTimeMillis() + turnSeconds * 1000L;
        String fields = "\"status\":" + q("playing")
                + ",\"levelCode\":" + q(levelCode)
                + ",\"currentColor\":" + q(firstColor)
                + ",\"turnEndTime\":" + deadline
                + ",\"version\":" + (lastVersion + 1);
        writtenVersion = lastVersion + 1;
        adapter.writeFields(fields);

        syncTo(levelCode);
    }

    private String buildEntitiesString(JsonValue players) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String color : COLORS) {
            if (players.get(color) == null) continue;
            if (!first) sb.append(',');
            first = false;
            sb.append("human ").append(color);
        }
        return sb.toString();
    }

    // ------------------------------------------------------- sincronización

    private void syncTo(String levelCode) {
        if (yioGdxGame.loadingManager.working) return;
        ViewableModel viewableModel = yioGdxGame.gameController.objectsLayer.viewableModel;
        boolean inGame = viewableModel != null && viewableModel.isNetMatch();
        if (inGame) {
            try {
                yioGdxGame.gameController.objectsLayer.syncManager.apply(levelCode);
            } catch (Exception e) {
                System.out.println("FirebaseGameManager.sync error: " + e.getMessage());
            }
            return;
        }
        startMatchLoading(levelCode);
    }

    private void startMatchLoading(String levelCode) {
        LoadingParameters p = new LoadingParameters();
        p.add("level_size", levelSize);
        p.add("level_code", levelCode);
        p.add("my_color", myColor);
        yioGdxGame.loadingManager.startInstantly(LoadingType.firebase_match, p);
        registeredTurnListener = false;
        ensureTurnListener();
    }

    private void ensureTurnListener() {
        if (registeredTurnListener) return;
        registeredTurnListener = true;
        ViewableModel viewableModel = yioGdxGame.gameController.objectsLayer.viewableModel;
        if (viewableModel == null) return;
        viewableModel.eventsManager.addListener(new IEventListener() {
            @Override
            public void onEventValidated(AbstractEvent event) {
            }

            @Override
            public void onEventApplied(AbstractEvent event) {
                if (event.getType() != EventType.turn_end) return;
                pushTurnEnd();
            }

            @Override
            public int getListenPriority() {
                return 4; // despues del cambio de turno del motor (TurnsManager=8)
            }
        });
    }

    private void pushTurnEnd() {
        if (adapter == null) return;
        String levelCode = serialize();
        if (levelCode == null) return;
        String currentColor = yioGdxGame.gameController.objectsLayer.viewableModel.entitiesManager.getCurrentColor().toString();
        long deadline = System.currentTimeMillis() + turnSeconds * 1000L;
        String fields = "\"levelCode\":" + q(levelCode)
                + ",\"currentColor\":" + q(currentColor)
                + ",\"turnEndTime\":" + deadline
                + ",\"version\":" + (lastVersion + 1);
        writtenVersion = lastVersion + 1;
        adapter.writeFields(fields);
        extendLocalDeadline();
    }

    private String serialize() {
        if (yioGdxGame.gameController == null) return null;
        GameController gc = yioGdxGame.gameController;
        ObjectsLayer ol = gc.objectsLayer;
        ExportParameters ep = ExportParameters.getInstance();
        try {
            ep.setCameraCode(gc.cameraController.encode());
            ep.setInitialLevelSize(gc.sizeManager.initialLevelSize);
            ep.setCoreModel(ol.viewableModel);
            ep.setHistoryManager(ol.historyManager);
            ep.setAiVersionCode(ol.aiManager.getUpdatedAiVersionCode());
            ep.setPauseName(ol.viewableModel.pauseName);
            return ol.exportManager.perform(ep);
        } catch (Exception e) {
            System.out.println("FirebaseGameManager.serialize error: " + e.getMessage());
            return null;
        }
    }

    private static String q(String s) {
        return RtdbClient.quote(s);
    }
}