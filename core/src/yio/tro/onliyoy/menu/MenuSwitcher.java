package yio.tro.onliyoy.menu;

import yio.tro.onliyoy.YioGdxGame;
import yio.tro.onliyoy.game.general.GameController;
import yio.tro.onliyoy.game.general.ObjectsLayer;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;
import yio.tro.onliyoy.menu.scenes.Scenes;

public class MenuSwitcher {

    private static MenuSwitcher instance;
    private MenuControllerYio menuControllerYio;
    private YioGdxGame yioGdxGame;


    public MenuSwitcher() {
        menuControllerYio = null;
    }


    public void onMenuControllerCreated(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        yioGdxGame = menuControllerYio.yioGdxGame;
    }


    public static void initialize() {
        instance = null;
    }


    public static MenuSwitcher getInstance() {
        if (instance == null) {
            instance = new MenuSwitcher();
        }
        return instance;
    }


    public void createPauseMenu() {
        Scenes.defaultPauseMenu.create();
    }


    public void createMenuOverlay() {
        Scenes.gameOverlay.create();
        getGameController().syncMechanicsOverlayWithCurrentTurn();
        getViewableModel().provinceSelectionManager.syncUI();
    }


    private ObjectsLayer getObjectsLayer() {
        return getGameController().objectsLayer;
    }


    private GameController getGameController() {
        return yioGdxGame.gameController;
    }


    private ViewableModel getViewableModel() {
        return getObjectsLayer().viewableModel;
    }
}