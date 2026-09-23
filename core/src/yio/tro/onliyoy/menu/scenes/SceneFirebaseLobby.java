package yio.tro.onliyoy.menu.scenes;

import com.badlogic.gdx.utils.JsonValue;
import yio.tro.onliyoy.Fonts;
import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.BackgroundYio;
import yio.tro.onliyoy.menu.elements.ConditionYio;
import yio.tro.onliyoy.menu.elements.LabelElement;
import yio.tro.onliyoy.menu.reactions.Reaction;
import yio.tro.onliyoy.net.firebase.FirebaseGameManager;

/**
 * Lobby de una partida Firebase: lista de jugadores en vivo y, para el creador,
 * el botón "Lanzar" (visible solo cuando hay al menos 2 jugadores).
 */
public class SceneFirebaseLobby extends SceneYio implements FirebaseGameManager.LobbyListener {

    private LabelElement titleLabel;
    private LabelElement[] playerLabels;
    private LabelElement statusLabel;

    private FirebaseGameManager getManager() {
        return yioGdxGame.firebaseGameManager;
    }

    @Override
    public BackgroundYio getBackgroundValue() {
        return BackgroundYio.green;
    }

    @Override
    protected void initialize() {
        playerLabels = new LabelElement[FirebaseGameManager.COLORS.length];
        createTitleLabel();
        createPlayerLabels();
        createStatusLabel();
        createLaunchButton();
        spawnBackButton(getBackReaction());
    }

    private void createTitleLabel() {
        titleLabel = uiFactory.getLabelElement()
                .setSize(0.7, 0.06)
                .centerHorizontal()
                .alignTop(0.05)
                .setFont(Fonts.gameFont)
                .setTitle(" ");
    }

    private void createPlayerLabels() {
        for (int i = 0; i < FirebaseGameManager.COLORS.length; i++) {
            playerLabels[i] = uiFactory.getLabelElement()
                    .setSize(0.7, 0.05)
                    .centerHorizontal()
                    .alignTop(0.13 + i * 0.055)
                    .setFont(Fonts.miniFont)
                    .setTitle(" ");
        }
    }

    private void createStatusLabel() {
        statusLabel = uiFactory.getLabelElement()
                .setSize(0.7, 0.05)
                .centerHorizontal()
                .alignBottom(0.24)
                .setFont(Fonts.miniFont)
                .setTitle(" ");
    }

    private void createLaunchButton() {
        uiFactory.getButton()
                .setSize(0.5, 0.07)
                .centerHorizontal()
                .alignBottom(0.1)
                .setBackground(BackgroundYio.magenta)
                .applyText("lanzar")
                .setAllowedToAppear(getLaunchCondition())
                .setReaction(getLaunchReaction())
                .setAnimation(AnimationYio.up);
    }

    private ConditionYio getLaunchCondition() {
        return new ConditionYio() {
            @Override
            public boolean get() {
                JsonValue players = getManager().getPlayers();
                return players != null && players.size >= 2;
            }
        };
    }

    @Override
    protected void onAppear() {
        super.onAppear();
        getManager().setLobbyListener(this);
        updateLabels();
    }

    @Override
    public void onLobby(JsonValue players) {
        updateLabels();
    }

    private void updateLabels() {
        FirebaseGameManager manager = getManager();
        if (titleLabel != null) {
            titleLabel.setTitle(languagesManager.getString("partida") + " " + shortId(manager.getMatchId()));
        }
        JsonValue players = manager.getPlayers();
        int count = 0;
        for (int i = 0; i < FirebaseGameManager.COLORS.length; i++) {
            String color = FirebaseGameManager.COLORS[i];
            JsonValue player = (players == null) ? null : players.get(color);
            if (player == null) {
                playerLabels[i].setTitle(" ");
                continue;
            }
            count++;
            String name = player.has("name") ? player.getString("name") : "-";
            String me = (player.has("id") && player.getString("id").equals(manager.getPlayerId()))
                    ? " · " + languagesManager.getString("tu")
                    : "";
            playerLabels[i].setTitle(capitalize(color) + " — " + name + me);
        }
        if (statusLabel != null) {
            statusLabel.setTitle((count >= 2)
                    ? languagesManager.getString("listo_para_lanzar")
                    : languagesManager.getString("esperando_jugadores"));
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String shortId(String id) {
        if (id == null || id.length() <= 3) return id;
        return id.substring(0, 3);
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
                Scenes.firebaseHome.create();
            }
        };
    }
}