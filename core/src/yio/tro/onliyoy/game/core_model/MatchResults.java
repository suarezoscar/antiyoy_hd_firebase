package yio.tro.onliyoy.game.core_model;

import yio.tro.onliyoy.game.core_model.ruleset.RulesType;
import yio.tro.onliyoy.game.general.GameMode;
import yio.tro.onliyoy.game.general.LevelSize;

public class MatchResults {

    public HColor winnerColor;
    public EntityType entityType;
    public LevelSize levelSize;
    public RulesType rulesType;
    public GameMode gameMode;


    public MatchResults() {
        winnerColor = null;
        entityType = null;
        levelSize = null;
        rulesType = null;
        gameMode = null;
    }

}