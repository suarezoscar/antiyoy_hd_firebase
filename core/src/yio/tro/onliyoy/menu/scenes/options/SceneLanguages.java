package yio.tro.onliyoy.menu.scenes.options;

import yio.tro.onliyoy.menu.LanguageChooseItem;
import yio.tro.onliyoy.menu.LanguagesManager;
import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.BackgroundYio;
import yio.tro.onliyoy.menu.reactions.Reaction;
import yio.tro.onliyoy.menu.scenes.SceneYio;
import yio.tro.onliyoy.menu.scenes.Scenes;

import java.util.ArrayList;

public class SceneLanguages extends SceneYio {

    @Override
    public BackgroundYio getBackgroundValue() {
        return BackgroundYio.green;
    }


    @Override
    protected void initialize() {
        spawnBackButton(getBackReaction());
        ArrayList<LanguageChooseItem> items = LanguagesManager.getInstance().getChooseListItems();
        double h = 0.07;
        for (int i = 0; i < items.size(); i++) {
            final LanguageChooseItem item = items.get(i);
            uiFactory.getButton()
                    .setSize(0.5, h)
                    .centerHorizontal()
                    .alignTop(0.05 + i * (h + 0.012))
                    .applyText(item.title)
                    .setReaction(getApplyReaction(item.name))
                    .setAnimation(AnimationYio.up);
        }
    }


    private Reaction getApplyReaction(final String language) {
        return new Reaction() {
            @Override
            protected void apply() {
                LanguagesManager.getInstance().setLanguage(language);
                Scenes.notification.show("language_changed");
                Scenes.settings.create();
            }
        };
    }


    private Reaction getBackReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                Scenes.settings.create();
            }
        };
    }
}