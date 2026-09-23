package yio.tro.onliyoy.game.loading.loading_processes;

import yio.tro.onliyoy.game.core_model.EntitiesManager;
import yio.tro.onliyoy.game.core_model.EntityType;
import yio.tro.onliyoy.game.core_model.HColor;
import yio.tro.onliyoy.game.core_model.PlayerEntity;
import yio.tro.onliyoy.game.export_import.*;
import yio.tro.onliyoy.game.general.GameMode;
import yio.tro.onliyoy.game.loading.LoadingManager;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;

/**
 * Carga una partida proveniente de la Realtime Database (nodo de partida).
 * <p>
 * Análogo a ProcessNetMatch: importa el levelCode completo y marca "mi" entidad
 * como humana (el resto, net_entity). Espera en LoadingParameters:
 * {@code level_size}, {@code level_code}, {@code my_color}.
 */
public class ProcessFirebaseMatch extends AbstractLoadingProcess {

    public ProcessFirebaseMatch(LoadingManager loadingManager) {
        super(loadingManager);
    }

    @Override
    public void prepare() {
        initGameMode(GameMode.net_match);
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
        initEntities(viewableModel);
        (new IwCoreCurrentIds(viewableModel)).perform(levelCode);
        (new IwCoreTurn(viewableModel)).perform(levelCode);
        (new IwCoreGraph(viewableModel)).perform(levelCode);
        (new IwCoreHexes(viewableModel)).perform(levelCode);
        loadAiVersionCodeFromParameters();
    }

    private void initEntities(ViewableModel viewableModel) {
        (new IwCorePlayerEntities(viewableModel)).perform(getLevelCodeFromParameters());
        EntitiesManager entitiesManager = viewableModel.entitiesManager;
        for (PlayerEntity entity : entitiesManager.entities) {
            entity.type = EntityType.net_entity;
        }
        Object myColorObject = loadingParameters.get("my_color");
        if (myColorObject == null) return;
        PlayerEntity ownedEntity = entitiesManager.getEntity(HColor.valueOf(myColorObject.toString()));
        if (ownedEntity != null) {
            ownedEntity.type = EntityType.human;
        }
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

        yio.tro.onliyoy.game.core_model.EntitiesManager em = viewableModel.entitiesManager;
        System.out.println("[FBG] load ok: mode=" + gameController.gameMode
                + " current=" + em.getCurrentColor()
                + " human=" + em.getCurrentEntity().isHuman()
                + " humanTurn=" + em.isHumanTurnCurrently()
                + " provinces=" + viewableModel.provincesManager.provinces.size()
                + " myColor=" + loadingParameters.get("my_color"));
    }
}