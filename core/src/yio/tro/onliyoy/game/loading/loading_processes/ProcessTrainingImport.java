package yio.tro.onliyoy.game.loading.loading_processes;

import yio.tro.onliyoy.game.export_import.*;
import yio.tro.onliyoy.game.general.GameMode;
import yio.tro.onliyoy.game.loading.LoadingManager;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;

/**
 * Re-importa un levelCode existente (reinicio de partida / rejoin).
 */
public class ProcessTrainingImport extends AbstractLoadingProcess {

    public ProcessTrainingImport(LoadingManager loadingManager) {
        super(loadingManager);
    }

    @Override
    public void prepare() {
        initGameMode(GameMode.training);
        initLevelSize(getLevelSizeFromParameters());
    }

    @Override
    public void initGameRules() {
        ViewableModel viewableModel = getViewableModel();
        (new IwCoreRules(viewableModel)).perform(getLevelCodeFromParameters());
    }

    private ViewableModel getViewableModel() {
        return gameController.objectsLayer.viewableModel;
    }

    @Override
    public void createBasicStuff() {
        String levelCode = getLevelCodeFromParameters();
        ViewableModel viewableModel = getViewableModel();
        (new IwCoreCurrentIds(viewableModel)).perform(levelCode);
        (new IwCoreTurn(viewableModel)).perform(levelCode);
        (new IwCoreGraph(viewableModel)).perform(levelCode);
        (new IwCoreHexes(viewableModel)).perform(levelCode);
        loadAiVersionCodeFromParameters();
    }

    @Override
    public void createAdvancedStuff() {
        String levelCode = getLevelCodeFromParameters();
        ViewableModel viewableModel = getViewableModel();
        (new IwCoreProvinces(viewableModel)).perform(levelCode);
        (new IwCoreDiplomacy(viewableModel)).perform(levelCode);
        (new IwCoreMailBasket(viewableModel)).perform(levelCode);
        (new IwReadiness(viewableModel)).perform(levelCode);
        (new IwCoreFogOfWar(viewableModel)).perform(levelCode);
        gameController.cameraController.flyUp(true);
    }
}