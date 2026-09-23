package yio.tro.onliyoy.menu.scenes;

import com.badlogic.gdx.utils.JsonValue;
import yio.tro.onliyoy.Fonts;
import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.AnnounceViewElement;
import yio.tro.onliyoy.menu.elements.BackgroundYio;
import yio.tro.onliyoy.menu.elements.ConditionYio;
import yio.tro.onliyoy.menu.reactions.Reaction;
import yio.tro.onliyoy.net.firebase.FirebaseGameManager;

/**
 * Lobby de una partida Firebase: jugadores en vivo y, para el creador, el botón
 * "Lanzar" que genera el mapa y lo publica en RTDB.
 */
public class SceneFirebaseLobby extends SceneYio implements FirebaseGameManager.LobbyListener {

    private AnnounceViewElement playersLabel;

    private FirebaseGameManager getManager() {
        return yioGdxGame.firebaseGameManager;
    }

    @Override
    public BackgroundYio getBackgroundValue() {
        return BackgroundYio.green;
    }

    @Override
    protected void initialize() {
        createPlayersLabel();
        createLaunchButton();
        spawnBackButton(getBackReaction());
    }

    private void createPlayersLabel() {
        playersLabel = uiFactory.getAnnounceViewElement()
                .setSize(0.85, 0.5)
                .centerHorizontal()
                .alignTop(0.06)
                .setText(" ");
    }

    private void createLaunchButton() {
        uiFactory.getButton()
                .setSize(0.5, 0.07)
                .centerHorizontal()
                .alignBottom(0.05)
                .setBackground(BackgroundYio.magenta)
                .applyText("lanzar")
                .setAllowedToAppear(getHostCondition())
                .setReaction(getLaunchReaction())
                .setAnimation(AnimationYio.up);
    }

    private ConditionYio getHostCondition() {
        return new ConditionYio() {
            @Override
            public boolean get() {
                return getManager().isHost();
            }
        };
    }

    @Override
    protected void onAppear() {
        super.onAppear();
        getManager().setLobbyListener(this);
        updatePlayersLabel();
    }

    @Override
    public void onLobby(JsonValue players) {
        updatePlayersLabel();
    }

    private void updatePlayersLabel() {
        if (playersLabel == null) return;
        FirebaseGameManager manager = getManager();
        JsonValue players = manager.getPlayers();
        StringBuilder sb = new StringBuilder();
        sb.append("Partida: ").append(manager.getMatchId()).append("\n\n");
        if (players == null || players.size == 0) {
            sb.append("Esperando jugadores...");
        } else {
            for (String color : FirebaseGameManager.COLORS) {
                JsonValue player = players.get(color);
                if (player == null) continue;
                String id = player.has("id") ? player.getString("id") : "-";
                String name = player.has("name") ? player.getString("name") : "-";
                sb.append(color).append(": ").append(name);
                if (id.equals(manager.getPlayerId())) sb.append("  (yo)");
                sb.append("\n");
            }
        }
        playersLabel.setText(sb.toString());
    }

    private Reaction getLaunchReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                getManager().launch();
            }
        };
    }

    private Reaction getBackReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                getManager().stop();
                yioGdxGame.setGamePaused(true);
                Scenes.chooseGameMode.create();
            }
        };
    }
}