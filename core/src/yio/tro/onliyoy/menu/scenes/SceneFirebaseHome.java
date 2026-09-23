package yio.tro.onliyoy.menu.scenes;

import com.badlogic.gdx.Gdx;
import yio.tro.onliyoy.Fonts;
import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.AnnounceViewElement;
import yio.tro.onliyoy.menu.elements.BackgroundYio;
import yio.tro.onliyoy.menu.elements.button.ButtonYio;
import yio.tro.onliyoy.menu.elements.keyboard.AbstractKbReaction;
import yio.tro.onliyoy.menu.reactions.Reaction;
import yio.tro.onliyoy.net.firebase.FirebaseConfig;
import yio.tro.onliyoy.net.firebase.FirebaseGameManager;

/**
 * Pantalla de multijugador Firebase: configurar la base (host), crear partida,
 * unirse pegando el enlace y copiar el enlace de la última partida.
 */
public class SceneFirebaseHome extends SceneYio {

    private AnnounceViewElement infoLabel;

    private FirebaseGameManager getManager() {
        return yioGdxGame.firebaseGameManager;
    }

    @Override
    public BackgroundYio getBackgroundValue() {
        return BackgroundYio.cyan;
    }

    @Override
    protected void initialize() {
        createInfoLabel();
        createConfigureButton();
        createCreateButton();
        createJoinButton();
        createCopyLinkButton();
        spawnBackButton(getBackReaction());
    }

    private void createInfoLabel() {
        infoLabel = uiFactory.getAnnounceViewElement()
                .setSize(0.85, 0.2)
                .centerHorizontal()
                .alignTop(0.04)
                .setText(" ");
    }

    private void createConfigureButton() {
        uiFactory.getButton()
                .setSize(0.5, 0.06)
                .centerHorizontal()
                .alignUnder(infoLabel, 0.005)
                .setBackground(BackgroundYio.green)
                .applyText("configurar_base")
                .setReaction(getConfigureReaction())
                .setAnimation(AnimationYio.up);
    }

    private void createCreateButton() {
        uiFactory.getButton()
                .setSize(0.5, 0.06)
                .centerHorizontal()
                .alignUnder(previousElement, 0.01)
                .setBackground(BackgroundYio.magenta)
                .applyText("crear_partida")
                .setReaction(getCreateReaction())
                .setAnimation(AnimationYio.up);
    }

    private void createJoinButton() {
        uiFactory.getButton()
                .setSize(0.5, 0.06)
                .centerHorizontal()
                .alignUnder(previousElement, 0.01)
                .setBackground(BackgroundYio.orange)
                .applyText("unirme_por_enlace")
                .setReaction(getJoinReaction())
                .setAnimation(AnimationYio.up);
    }

    private void createCopyLinkButton() {
        uiFactory.getButton()
                .setSize(0.5, 0.06)
                .centerHorizontal()
                .alignUnder(previousElement, 0.01)
                .setBackground(BackgroundYio.yellow)
                .applyText("copiar_enlace")
                .setReaction(getCopyLinkReaction())
                .setAnimation(AnimationYio.up);
    }

    @Override
    protected void onAppear() {
        super.onAppear();
        updateInfoLabel();
    }

    private void updateInfoLabel() {
        FirebaseGameManager manager = getManager();
        String base = manager.getConfig().isConfigured() ? manager.getConfig().getDatabaseUrl() : "(sin configurar)";
        infoLabel.setText(
                "Base: " + base + "\n" +
                        "Soy: " + manager.getPlayerName()
        );
    }

    private Reaction getConfigureReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                final FirebaseGameManager manager = getManager();
                Scenes.firebaseInput.create();
                Scenes.firebaseInput.setValue("-".equals(manager.getConfig().getDatabaseUrl()) ? "" : manager.getConfig().getDatabaseUrl());
                Scenes.firebaseInput.setReaction(new AbstractKbReaction() {
                    @Override
                    public void onInputFromKeyboardReceived(String input) {
                        manager.getConfig().setConfig(input.trim(), "-");
                        updateInfoLabel();
                    }
                });
            }
        };
    }

    private Reaction getCreateReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                FirebaseGameManager manager = getManager();
                if (!manager.getConfig().isConfigured()) {
                    Scenes.notification.show("configura_la_base_primero");
                    return;
                }
                manager.createMatch();
                Gdx.app.getClipboard().setContents(manager.getJoinLink());
                Scenes.firebaseLobby.create();
            }
        };
    }

    private Reaction getJoinReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                final FirebaseGameManager manager = getManager();
                Scenes.firebaseInput.create();
                Scenes.firebaseInput.setValue("");
                Scenes.firebaseInput.setReaction(new AbstractKbReaction() {
                    @Override
                    public void onInputFromKeyboardReceived(String input) {
                        FirebaseConfig.InviteData data = FirebaseConfig.parse(input.trim());
                        if ("-".equals(data.matchId)) {
                            Scenes.notification.show("enlace_invalido");
                            return;
                        }
                        manager.join(data.databaseUrl, data.apiKey, data.matchId);
                        Scenes.firebaseLobby.create();
                    }
                });
            }
        };
    }

    private Reaction getCopyLinkReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                String link = getManager().getJoinLink();
                if (link == null) {
                    Scenes.notification.show("crea_primero_una_partida");
                    return;
                }
                Gdx.app.getClipboard().setContents(link);
                Scenes.notification.show("enlace_copiado");
            }
        };
    }

    private Reaction getBackReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                yioGdxGame.setGamePaused(true);
                Scenes.chooseGameMode.create();
            }
        };
    }
}