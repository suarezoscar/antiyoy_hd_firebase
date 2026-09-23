package yio.tro.onliyoy.game.general;

import yio.tro.onliyoy.SettingsManager;
import yio.tro.onliyoy.game.core_model.ai.Difficulty;
import yio.tro.onliyoy.game.core_model.*;
import yio.tro.onliyoy.game.core_model.ai.AiManager;
import yio.tro.onliyoy.game.core_model.events.EventTurnEnd;
import yio.tro.onliyoy.game.core_model.events.EventsFactory;
import yio.tro.onliyoy.game.core_model.events.EventsManager;
import yio.tro.onliyoy.game.core_model.events.HistoryManager;
import yio.tro.onliyoy.game.debug.DebugFlags;
import yio.tro.onliyoy.game.export_import.ExportManager;
import yio.tro.onliyoy.game.export_import.ExportParameters;
import yio.tro.onliyoy.game.viewable_model.UndoManager;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;
import yio.tro.onliyoy.menu.scenes.Scenes;
import yio.tro.onliyoy.net.firebase.FirebaseGameManager;
import yio.tro.onliyoy.stuff.*;

public class ObjectsLayer implements TouchableYio, AcceleratableYio {

    public GameController gameController;
    public ExportManager exportManager;
    public ViewableModel viewableModel;
    public HistoryManager historyManager;
    public UndoManager undoManager;
    public AiManager aiManager;
    RepeatYio<ObjectsLayer> repeatAI;
    RepeatYio<ObjectsLayer> repeatCheckToEndMatch;
    RepeatYio<ObjectsLayer> repeatAutoSkip;
    public SyncManager syncManager;
    public TreeManager treeManager;
    RepeatYio<ObjectsLayer> repeatForceEndTurn;
    AutoEndTurnWorker autoEndTurnWorker;


    public ObjectsLayer(GameController gameController) {
        this.gameController = gameController;

        exportManager = new ExportManager();
        viewableModel = new ViewableModel(this);
        historyManager = new HistoryManager(viewableModel);
        undoManager = new UndoManager(viewableModel);
        aiManager = new AiManager(viewableModel, Difficulty.balancer);
        syncManager = new SyncManager(viewableModel);
        treeManager = new TreeManager(viewableModel);
        autoEndTurnWorker = new AutoEndTurnWorker(this);

        defaultValues();
        initRepeats();
    }


    private void initRepeats() {
        repeatAI = new RepeatYio<ObjectsLayer>(this, 4, 4) {
            @Override
            public void performAction() {
                parent.checkToMakeAiMove();
            }
        };
        repeatCheckToEndMatch = new RepeatYio<ObjectsLayer>(this, 30, 30) {
            @Override
            public void performAction() {
                if (!viewableModel.entitiesManager.isInAiOnlyMode()) return;
                parent.checkToEndMatch();
            }
        };
        repeatAutoSkip = new RepeatYio<ObjectsLayer>(this, 15, 15) {
            @Override
            public void performAction() {
                parent.checkForAutoSkip();
            }
        };
        repeatForceEndTurn = new RepeatYio<ObjectsLayer>(this, 5, 5) {
            @Override
            public void performAction() {
                parent.checkToForceEndTurn();
            }
        };
    }


    @Override
    public void moveActually() {
        viewableModel.move();
        repeatAI.move();
        repeatCheckToEndMatch.move();
        repeatAutoSkip.move();
        repeatForceEndTurn.move();
    }


    private void checkToForceEndTurn() {
        FirebaseGameManager firebase = gameController.yioGdxGame.firebaseGameManager;
        if (firebase == null || firebase.getAdapter() == null) return;
        long deadline = firebase.getTurnEndTimeMillis();
        if (deadline == 0) return;
        if (System.currentTimeMillis() < deadline) return;
        forceEndTurnNow("firebase");
    }


    private void forceEndTurnNow(String source) {
        EventsManager eventsManager = viewableModel.eventsManager;
        EventTurnEnd endTurnEvent = eventsManager.factory.createEndTurnEvent();
        HColor previousColor = viewableModel.entitiesManager.getCurrentColor();
        eventsManager.applyEvent(endTurnEvent);
        System.out.println("Forced turn end (" + source + "): " + previousColor + " -> " + viewableModel.entitiesManager.getCurrentColor());
    }


    private void checkToMakeAiMove() {
        if (!gameController.doesCurrentGameModeAllowGameplay()) return;
        if (!gameController.yioGdxGame.gameView.coversAllScreen()) return;
        if (viewableModel.isSomethingMovingCurrently()) return;
        aiManager.move();
        EntitiesManager entitiesManager = viewableModel.entitiesManager;
        if (entitiesManager.isInAiOnlyMode() && !DebugFlags.aiPerTurnMovement) {
            while (viewableModel.turnsManager.turnIndex != 0 && aiManager.active) {
                aiManager.move();
            }
        }
        if (entitiesManager.isSingleplayerHumanMatch() && !DebugFlags.aiPerTurnMovement) {
            while (entitiesManager.getCurrentEntity().isArtificialIntelligence() && aiManager.active) {
                aiManager.move();
            }
        }
    }


    public void checkToEndMatch() {
        if (!gameController.doesCurrentGameModeAllowGameplay()) return;
        if (viewableModel.isNetMatch()) return;
        MatchResults matchResults = viewableModel.finishMatchManager.getMatchResults();
        if (matchResults == null) return;
        if (viewableModel.isSomethingMovingCurrently()) return;
        if (!viewableModel.isBufferEmpty()) return;
        if (gameController.yioGdxGame.gamePaused) return;
        historyManager.onMatchEnded();
        aiManager.onMatchEnded();
        matchResults.levelSize = gameController.sizeManager.initialLevelSize;
        matchResults.rulesType = viewableModel.ruleset.getRulesType();
        matchResults.gameMode = gameController.gameMode;
        gameController.yioGdxGame.applyFullTransitionToUI();
        Scenes.matchResults.create();
        Scenes.matchResults.setMatchResults(matchResults);
        checkToSaveProgress(matchResults);
    }


    private void checkToSaveProgress(MatchResults matchResults) {
        PlayerEntity winner = viewableModel.entitiesManager.getEntity(matchResults.winnerColor);
        if (!winner.isHuman()) return;
    }


    @Override
    public void moveVisually() {
        autoEndTurnWorker.move();
    }


    public void onPlayerRequestedToEndTurn() {
        if (!viewableModel.refModel.entitiesManager.getCurrentEntity().isHuman()) return;
        if (!viewableModel.entitiesManager.getCurrentEntity().isHuman()) return;
        EventsFactory factory = viewableModel.eventsManager.factory;
        EventTurnEnd endTurnEvent = factory.createEndTurnEvent();
        viewableModel.humanControlsManager.applyHumanEvent(endTurnEvent);
    }


    void checkForAutoSkip() {
        if (!gameController.doesCurrentGameModeAllowGameplay()) return;
        PlayerEntity currentEntity = viewableModel.entitiesManager.getCurrentEntity();
        if (!currentEntity.isHuman()) return;
        if (viewableModel.isNetMatch()) return; // should be handled on server side
        if (viewableModel.provincesManager.getProvince(currentEntity.color) != null) return;
        EventTurnEnd endTurnEvent = viewableModel.eventsManager.factory.createEndTurnEvent();
        viewableModel.eventsManager.applyEvent(endTurnEvent);
    }


    public void checkForAutosave() {
    }


    private boolean isGameModeGoodForAutosave() {
        switch (gameController.gameMode) {
            default:
                return false;
            case custom:
            case training:
            case campaign:
                return true;
        }
    }


    public void move() {
        for (int speed = gameController.speedManager.getSpeed(); speed > 0; speed--) {
            moveActually();
        }
        moveVisually();
    }


    public void defaultValues() {
        viewableModel.buildGraph(gameController.sizeManager.position, getHexRadius());
    }


    public float getHexRadius() {
        return 0.07f * GraphicsYio.width;
    }


    public void onBasicStuffCreated() {
        viewableModel.onBasicStuffCreated();
    }


    public void onAdvancedStuffCreated() {
        if (shouldActivateTreeManager()) {
            viewableModel.eventsManager.addListener(treeManager);
        }
        viewableModel.onAdvancedStuffCreated();
    }


    private boolean shouldActivateTreeManager() {
        if (viewableModel.isNetMatch()) return false;
        if (gameController.gameMode == GameMode.replay) return false;
        return true;
    }


    public void onClick() {

    }


    @Override
    public boolean onTouchDown(PointYio touchPoint) {
        return false;
    }


    @Override
    public boolean onTouchDrag(PointYio touchPoint) {
        return false;
    }


    @Override
    public boolean onTouchUp(PointYio touchPoint) {
        return false;
    }


    public void onDestroy() {

    }


}
