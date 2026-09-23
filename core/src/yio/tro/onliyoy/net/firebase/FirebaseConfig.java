package yio.tro.onliyoy.net.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Configuración del backend por-host (BYO) sobre **Firebase Realtime Database**.
 * <p>
 * Guarda en prefs la URL de la base RTDB del host (+ apiKey reservada para futura
 * auth) y construye/parsea el enlace de invitación:
 * <pre>antiyoy://join?u=&lt;url(encoded)&gt;&amp;k=&lt;apiKey&gt;&amp;m=&lt;matchId&gt;</pre>
 * La url de la base viaja codificada (URL encode) dentro del enlace.
 */
public class FirebaseConfig {

    private static final String PREFS_NAME = "antiyoy_firebase";
    private static final String KEY_DB_URL = "db_url";
    private static final String KEY_API_KEY = "api_key";

    private final Preferences prefs;

    public FirebaseConfig() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public boolean isConfigured() {
        return !"-".equals(getDatabaseUrl());
    }

    public String getDatabaseUrl() {
        return prefs.getString(KEY_DB_URL, "-");
    }

    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "-");
    }

    public void setConfig(String databaseUrl, String apiKey) {
        prefs.putString(KEY_DB_URL, databaseUrl)
                .putString(KEY_API_KEY, apiKey == null ? "-" : apiKey)
                .flush();
    }

    public void clearConfig() {
        prefs.putString(KEY_DB_URL, "-").putString(KEY_API_KEY, "-").flush();
    }

    public static String buildJoinLink(String databaseUrl, String apiKey, String matchId) {
        return "antiyoy://join?u=" + encode(databaseUrl)
                + "&k=" + (apiKey == null ? "" : apiKey)
                + "&m=" + matchId;
    }

    public static InviteData parse(String inviteLink) {
        InviteData data = new InviteData();
        if (inviteLink == null || inviteLink.length() == 0) return data;
        String[] parts = inviteLink.split("\\?");
        if (parts.length != 2) return data;
        for (String pair : parts[1].split("&")) {
            String[] kv = pair.split("=");
            if (kv.length != 2) continue;
            if ("u".equals(kv[0])) data.databaseUrl = decode(kv[1]);
            if ("k".equals(kv[0])) data.apiKey = kv[1];
            if ("m".equals(kv[0])) data.matchId = kv[1];
        }
        return data;
    }

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }

    public static class InviteData {
        public String databaseUrl = "-";
        public String apiKey = "-";
        public String matchId = "-";
    }
}