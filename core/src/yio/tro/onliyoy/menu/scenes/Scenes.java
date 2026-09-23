package yio.tro.onliyoy.menu.scenes;

import yio.tro.onliyoy.menu.scenes.gameplay.*;
import yio.tro.onliyoy.menu.scenes.info.SceneAboutGame;
import yio.tro.onliyoy.menu.scenes.options.SceneConfirmResetSettings;
import yio.tro.onliyoy.menu.scenes.options.SceneLanguages;
import yio.tro.onliyoy.menu.scenes.options.SceneSettings;

/**
 * Registro de escenas de Fireyoy (solo modo Firebase).
 */
public class Scenes {

    public static SceneFirebaseHome firebaseHome;
    public static SceneFirebaseLobby firebaseLobby;
    public static SceneFirebaseInput firebaseInput;

    public static SceneGameOverlay gameOverlay;
    public static SceneMechanicsOverlay mechanicsOverlay;
    public static SceneProvinceManagement provinceManagement;
    public static SceneIncomeGraph incomeGraph;
    public static SceneMatchResults matchResults;
    public static SceneConfirmEndTurn confirmEndTurn;

    public static SceneDefaultPauseMenu defaultPauseMenu;
    public static SceneSettings settings;
    public static SceneLanguages languages;
    public static SceneConfirmResetSettings confirmResetSettings;
    public static SceneNotification notification;
    public static SceneToast toast;
    public static SceneExceptionReport exceptionReport;
    public static SceneKeyboard keyboard;
    public static SceneConfirmRestart confirmRestart;

    public static SceneForefinger forefinger;
    public static SceneHighlightArea highlightArea;


    public static void createAllScenes() {
        firebaseHome = new SceneFirebaseHome();
        firebaseLobby = new SceneFirebaseLobby();
        firebaseInput = new SceneFirebaseInput();

        gameOverlay = new SceneGameOverlay();
        mechanicsOverlay = new SceneMechanicsOverlay();
        provinceManagement = new SceneProvinceManagement();
        incomeGraph = new SceneIncomeGraph();
        matchResults = new SceneMatchResults();
        confirmEndTurn = new SceneConfirmEndTurn();

        defaultPauseMenu = new SceneDefaultPauseMenu();
        settings = new SceneSettings();
        languages = new SceneLanguages();
        confirmResetSettings = new SceneConfirmResetSettings();
        notification = new SceneNotification();
        toast = new SceneToast();
        exceptionReport = new SceneExceptionReport();
        keyboard = new SceneKeyboard();
        confirmRestart = new SceneConfirmRestart();

        forefinger = new SceneForefinger();
        highlightArea = new SceneHighlightArea();
    }
}