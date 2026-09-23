package yio.tro.onliyoy.game.tutorial;

import java.util.ArrayList;

/**
 * Núcleo mínimo del motor de tutoriales (sin escenas de tutorial).
 * Mantiene la interfaz que el resto del juego usa; ejecuta nada.
 */
public class ScriptManager {

    private final ArrayList<Object> items = new ArrayList<>();


    public ScriptManager(yio.tro.onliyoy.game.general.GameController gameController) {
    }


    public boolean hasSomeAliveScripts() {
        return false;
    }


    public boolean isDirectPlayerControlAllowed() {
        return true;
    }


    public void clear() {
        items.clear();
    }


    public void applySkipMessages() {
    }


    public void prepareToExecuteNextScriptFaster() {
    }


    public void onMatchStarted() {
    }


    public void onMatchEnded() {
    }
}