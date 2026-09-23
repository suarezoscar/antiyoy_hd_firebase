package yio.tro.onliyoy;

import com.badlogic.gdx.Input;
import yio.tro.onliyoy.game.core_model.Hex;
import yio.tro.onliyoy.game.general.GameController;
import yio.tro.onliyoy.game.touch_modes.TouchMode;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;
import yio.tro.onliyoy.menu.MenuControllerYio;
import yio.tro.onliyoy.menu.elements.InterfaceElement;
import yio.tro.onliyoy.menu.elements.keyboard.AbstractKbReaction;
import yio.tro.onliyoy.menu.elements.keyboard.CustomKeyboardElement;
import yio.tro.onliyoy.menu.scenes.Scenes;
import yio.tro.onliyoy.stuff.factor_yio.MovementType;

import java.util.ArrayList;

public class OnKeyReactions {

    YioGdxGame yioGdxGame;
    MenuControllerYio menuControllerYio;
    GameController gameController;


    public OnKeyReactions(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
        gameController = yioGdxGame.gameController;
    }


    public void keyDown(int keycode) {
        if (checkForKeyboard(keycode)) return;

        if (keycode == Input.Keys.ESCAPE) {
            keycode = Input.Keys.BACK;
        }

        checkForHotkeyUiReaction(keycode);
        checkOtherStuff(keycode);
    }


    private void checkForHotkeyUiReaction(int keycode) {
        if (keycode == Input.Keys.ENTER && Scenes.keyboard.isCurrentlyVisible()) return;
        ArrayList<InterfaceElement> interfaceElements = menuControllerYio.getInterfaceElements();
        for (int i = interfaceElements.size() - 1; i >= 0; i--) {
            InterfaceElement element = interfaceElements.get(i);
            if (!element.isVisible()) continue;
            if (element.getFactor().getValue() < 0.95) continue;
            if (!element.acceptsKeycode(keycode)) continue;
            element.pressArtificially(keycode);
            break;
        }
    }


    private boolean checkForKeyboard(int keycode) {
        CustomKeyboardElement customKeyboardElement = Scenes.keyboard.customKeyboardElement;
        if (customKeyboardElement == null) return false;
        if (customKeyboardElement.getFactor().getValue() < 0.2) return false;
        customKeyboardElement.onPcKeyPressed(keycode);
        return true;
    }


    private void checkOtherStuff(int keycode) {
        if (YioGdxGame.platformType != PlatformType.pc) return;
        switch (keycode) {
            case Input.Keys.NUM_1:
            case Input.Keys.NUM_2:
                Scenes.provinceManagement.onPcKeyPressed(keycode);
                break;
            case Input.Keys.S:
                yioGdxGame.slowMo = !yioGdxGame.slowMo;
                break;
            case Input.Keys.Z:
                gameController.cameraController.setTargetZoomLevel(gameController.cameraController.comfortableZoomLevel);
                break;
            case Input.Keys.I:
                if (!gameController.yioGdxGame.gamePaused) {
                    gameController.cameraController.changeZoomLevel(0.1);
                }
                break;
            case Input.Keys.H:
                doFindHex();
                break;
        }
    }


    public void doFindHex() {
        Scenes.keyboard.create();
        Scenes.keyboard.setHint("Hex coordinates");
        Scenes.keyboard.setReaction(new AbstractKbReaction() {
            @Override
            public void onInputFromKeyboardReceived(String input) {
                if (input.length() == 0) return;
                String[] split = input.split(" ");
                if (split.length != 2) return;
                if (!Yio.isNumeric(split[0])) return;
                if (!Yio.isNumeric(split[1])) return;
                int c1 = Integer.valueOf(split[0]);
                int c2 = Integer.valueOf(split[1]);
                GameController gameController = menuControllerYio.yioGdxGame.gameController;
                ViewableModel viewableModel = gameController.objectsLayer.viewableModel;
                Hex hex = viewableModel.getHex(c1, c2);
                if (hex == null) {
                    System.out.println("OnKeyReactions.doFindHex: no such hex");
                    return;
                }
                gameController.setTouchMode(TouchMode.tmDefault);
                TouchMode.tmDefault.doHighlight(hex);
                TouchMode.tmDefault.highlightFactor.destroy(MovementType.lighty, 1.1);
            }
        });
    }
}