package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Configuración del backend Firebase "trae tu propio" (BYO).
 * <p>
 * Guarda en prefs el projectId y la apiKey del proyecto Firebase del host,
 * y construye/parsea el enlace de invitación del tipo:
 * <pre>antiyoy://join?p=&lt;projectId&gt;&amp;k=&lt;apiKey&gt;&amp;m=&lt;matchId&gt;</pre>
 */
public class FirebaseConfig {

    private static final String PREFS_NAME = "antiyoy_firebase";
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_API_KEY = "api_key";

    private final Preferences prefs;

    public FirebaseConfig() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public boolean isConfigured() {
        return !"-".equals(getProjectId()) && !"-".equals(getApiKey());
    }

    public String getProjectId() {
        return prefs.getString(KEY_PROJECT_ID, "-");
    }

    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "-");
    }

    public void setConfig(String projectId, String apiKey) {
        prefs.putString(KEY_PROJECT_ID, projectId).putString(KEY_API_KEY, apiKey).flush();
    }

    public void clearConfig() {
        prefs.putString(KEY_PROJECT_ID, "-").putString(KEY_API_KEY, "-").flush();
    }

    public static String buildJoinLink(String projectId, String apiKey, String matchId) {
        return "antiyoy://join?p=" + projectId + "&k=" + apiKey + "&m=" + matchId;
    }

    public static InviteData parse(String inviteLink) {
        InviteData data = new InviteData();
        if (inviteLink == null || inviteLink.length() == 0) return data;
        String[] parts = inviteLink.split("\\?");
        if (parts.length != 2) return data;
        for (String pair : parts[1].split("&")) {
            String[] kv = pair.split("=");
            if (kv.length != 2) continue;
            if ("p".equals(kv[0])) data.projectId = kv[1];
            if ("k".equals(kv[0])) data.apiKey = kv[1];
            if ("m".equals(kv[0])) data.matchId = kv[1];
        }
        return data;
    }

    public static class InviteData {
        public String projectId = "-";
        public String apiKey = "-";
        public String matchId = "-";
    }
}