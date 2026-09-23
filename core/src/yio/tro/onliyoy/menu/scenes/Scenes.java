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

    public static SceneChooseEntity chooseEntity;
    public static SceneComposeLetter composeLetter;
    public static SceneReadLetter readLetter;
    public static SceneInbox inbox;
    public static SceneMessageDialog messageDialog;
    public static SceneSetupMoneyCondition setupMoneyCondition;
    public static SceneSetupRelationCondition setupRelationCondition;
    public static SceneSetupRveNotification setupRveNotification;
    public static SceneSetupSmileysCondition setupSmileysCondition;
    public static SceneSingleEntityConfigure singleEntityConfigure;
    public static SceneTmChooseLandsOverlay tmChooseLandsOverlay;

    public static SceneDefaultPauseMenu defaultPauseMenu;
    public static SceneSettings settings;
    public static SceneLanguages languages;
    public static SceneConfirmResetSettings confirmResetSettings;
    public static SceneNotification notification;
    public static SceneToast toast;
    public static SceneExceptionReport exceptionReport;
    public static SceneKeyboard keyboard;
    public static SceneLoadingTraining loadingTraining;
    public static SceneConfirmRestart confirmRestart;

    public static SceneForefinger forefinger;
    public static SceneHighlightArea highlightArea;
    public static SceneAboutGame aboutGame;


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

        chooseEntity = new SceneChooseEntity();
        composeLetter = new SceneComposeLetter();
        readLetter = new SceneReadLetter();
        inbox = new SceneInbox();
        messageDialog = new SceneMessageDialog();
        setupMoneyCondition = new SceneSetupMoneyCondition();
        setupRelationCondition = new SceneSetupRelationCondition();
        setupRveNotification = new SceneSetupRveNotification();
        setupSmileysCondition = new SceneSetupSmileysCondition();
        singleEntityConfigure = new SceneSingleEntityConfigure();
        tmChooseLandsOverlay = new SceneTmChooseLandsOverlay();

        defaultPauseMenu = new SceneDefaultPauseMenu();
        settings = new SceneSettings();
        languages = new SceneLanguages();
        confirmResetSettings = new SceneConfirmResetSettings();
        notification = new SceneNotification();
        toast = new SceneToast();
        exceptionReport = new SceneExceptionReport();
        keyboard = new SceneKeyboard();
        loadingTraining = new SceneLoadingTraining();
        confirmRestart = new SceneConfirmRestart();

        forefinger = new SceneForefinger();
        highlightArea = new SceneHighlightArea();
        aboutGame = new SceneAboutGame();
    }
}