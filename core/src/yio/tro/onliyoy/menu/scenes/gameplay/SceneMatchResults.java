package yio.tro.onliyoy.menu.scenes.gameplay;

import yio.tro.onliyoy.Fonts;
import yio.tro.onliyoy.game.core_model.HColor;
import yio.tro.onliyoy.game.core_model.MatchResults;
import yio.tro.onliyoy.game.core_model.PlayerEntity;
import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.BackgroundYio;
import yio.tro.onliyoy.menu.elements.LabelElement;
import yio.tro.onliyoy.menu.reactions.Reaction;
import yio.tro.onliyoy.menu.scenes.SceneYio;
import yio.tro.onliyoy.menu.scenes.Scenes;

public class SceneMatchResults extends SceneYio {

    private LabelElement titleLabel;
    private MatchResults matchResults;


    @Override
    public BackgroundYio getBackgroundValue() {
        return BackgroundYio.cyan;
    }


    @Override
    protected void initialize() {
        createTitleLabel();
        createNextButton();
    }


    private void createTitleLabel() {
        titleLabel = uiFactory.getLabelElement()
                .setSize(0.85, 0.06)
                .centerHorizontal()
                .alignTop(0.2)
                .setFont(Fonts.gameFont)
                .setTitle(" ");
    }


    private void createNextButton() {
        uiFactory.getButton()
                .setSize(0.45, 0.07)
                .centerHorizontal()
                .alignBottom(0.12)
                .setBackground(BackgroundYio.magenta)
                .applyText("next")
                .setReaction(getNextReaction())
                .setAnimation(AnimationYio.down);
    }


    private Reaction getNextReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                Scenes.firebaseHome.create();
            }
        };
    }


    public void setMatchResults(MatchResults matchResults) {
        this.matchResults = matchResults;
        updateLabel();
    }


    private void updateLabel() {
        if (titleLabel == null || matchResults == null) return;
        HColor winnerColor = matchResults.winnerColor;
        String name = winnerColor == null ? "-" : winnerColor.toString();
        titleLabel.setTitle(languagesManager.getString("ganador") + ": " + name);
    }


    @Override
    public boolean isOnlineTargeted() {
        return false;
    }
}