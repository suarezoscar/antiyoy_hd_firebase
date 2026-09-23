package yio.tro.onliyoy.menu.scenes.gameplay;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.onliyoy.SettingsManager;
import yio.tro.onliyoy.SoundType;
import yio.tro.onliyoy.game.debug.DebugFlags;
import yio.tro.onliyoy.game.general.GameMode;
import yio.tro.onliyoy.game.touch_modes.TouchMode;
import yio.tro.onliyoy.game.viewable_model.ViewableModel;
import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.ConditionYio;
import yio.tro.onliyoy.menu.elements.button.ButtonYio;
import yio.tro.onliyoy.menu.elements.gameplay.province_ui.MechanicsHookElement;
import yio.tro.onliyoy.menu.reactions.Reaction;
import yio.tro.onliyoy.menu.scenes.ModalSceneYio;
import yio.tro.onliyoy.menu.scenes.Scenes;
import yio.tro.onliyoy.stuff.GraphicsYio;

public class SceneMechanicsOverlay extends ModalSceneYio {


    private TextureRegion selectionTexture;
    private double bSize;
    private double touchOffset;
    private TextureRegion flagWhiteTexture;
    private TextureRegion flagRedTexture;
    private ButtonYio flagButton;
    private TextureRegion mailWhiteTexture;
    private TextureRegion mailRedTexture;
    private ButtonYio mailButton;
    public ButtonYio endTurnButton;
    public ButtonYio undoButton;
    private MechanicsHookElement mechanicsHookElement;


    public SceneMechanicsOverlay() {
        flagButton = null;
        mailButton = null;
    }


    @Override
    protected void initialize() {
        loadTextures();
        bSize = GraphicsYio.convertToWidth(0.05);
        touchOffset = 0.06;
        createSuperUserButton();
        createMechanicsHook();
        createEndTurnButton();
        createUndoButton();
        createDiplomacyButtons();
    }


    private void createMechanicsHook() {
        mechanicsHookElement = uiFactory.getMechanicsHookElement()
                .setSize(1, 1)
                .alignLeft(0)
                .alignBottom(0);
    }


    private void createDiplomacyButtons() {
        flagButton = uiFactory.getButton()
                .setParent(mechanicsHookElement)
                .setSize(bSize)
                .alignLeft(0)
                .alignAbove(previousElement, GraphicsYio.convertToHeight(touchOffset))
                .setTouchOffset(touchOffset)
                .setIgnoreResumePause(true)
                .setAnimation(AnimationYio.left)
                .setCustomTexture(flagWhiteTexture)
                .setKey("flag")
                .setSelectionTexture(selectionTexture)
                .setAllowedToAppear(getDiplomacyCondition())
                .setReaction(getFlagReaction());

        mailButton = uiFactory.getButton()
                .setParent(mechanicsHookElement)
                .setSize(bSize)
                .alignLeft(0)
                .alignAbove(previousElement, GraphicsYio.convertToHeight(touchOffset))
                .setTouchOffset(touchOffset)
                .setIgnoreResumePause(true)
                .setAnimation(AnimationYio.left)
                .setCustomTexture(mailWhiteTexture)
                .setKey("mail")
                .setSelectionTexture(selectionTexture)
                .setAllowedToAppear(getDiplomacyCondition())
                .setReaction(getMailReaction());
    }


    public void updateMailTexture() {
        if (mailButton == null) return;
        if (getViewableModel().lettersManager.containsLettersToCurrentEntity()) {
            mailButton.setCustomTexture(mailRedTexture);
        } else {
            mailButton.setCustomTexture(mailWhiteTexture);
        }
    }


    private Reaction getMailReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                onMailButtonPressed();
            }
        };
    }


    private void onMailButtonPressed() {
        getViewableModel().provinceSelectionManager.onClickedOutside();
    }


    private Reaction getFlagReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                onFlagButtonPressed();
            }
        };
    }


    private void onFlagButtonPressed() {
        getObjectsLayer().viewableModel.provinceSelectionManager.onClickedOutside();
    }


    public void updateFlagTexture() {
        if (flagButton == null) return;
        flagButton.setCustomTexture(flagWhiteTexture);
    }


    @Override
    protected void onAppear() {
        super.onAppear();
        resolvePossibleConflicts();
        updateFlagTexture();
        updateMailTexture();
    }


    private void resolvePossibleConflicts() {
    }


    private ConditionYio getDiplomacyCondition() {
        return new ConditionYio() {
            @Override
            public boolean get() {
                return getObjectsLayer().viewableModel.diplomacyManager.enabled;
            }
        };
    }


    private void createUndoButton() {
        undoButton = uiFactory.getButton()
                .setParent(mechanicsHookElement)
                .setSize(bSize)
                .alignLeft(0)
                .alignBottom(0)
                .setTouchOffset(touchOffset)
                .setIgnoreResumePause(true)
                .setAnimation(AnimationYio.down)
                .setKey("undo")
                .setCustomTexture(getTextureFromAtlas("undo"))
                .setSelectionTexture(selectionTexture)
                .setHotkeyKeycode(Input.Keys.BACKSPACE)
                .setClickSound(SoundType.undo)
                .setReaction(getUndoReaction());
    }


    private Reaction getUndoReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                onUndoButtonPressed();
            }
        };
    }


    public void onUndoButtonPressed() {
        ViewableModel viewableModel = getObjectsLayer().viewableModel;
        boolean success = viewableModel.onUndoRequested();
        if (!success) return;
    }


    private void createEndTurnButton() {
        endTurnButton = uiFactory.getButton()
                .setParent(mechanicsHookElement)
                .setSize(bSize)
                .alignRight(0)
                .alignBottom(0)
                .setTouchOffset(touchOffset)
                .setIgnoreResumePause(true)
                .setAnimation(AnimationYio.down)
                .setCustomTexture(getTextureFromAtlas("end_turn"))
                .setSelectionTexture(selectionTexture)
                .setHotkeyKeycode(Input.Keys.SPACE)
                .setKey("end_turn")
                .setClickSound(SoundType.turn_end)
                .setReaction(getEndTurnReaction());
    }


    private Reaction getEndTurnReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                onTurnEndButtonPressed();
            }
        };
    }


    private void onTurnEndButtonPressed() {
        if (hasToConfirmToEndTurn()) {
            Scenes.confirmEndTurn.create();
            return;
        }
        getObjectsLayer().onPlayerRequestedToEndTurn();
    }


    private boolean hasToConfirmToEndTurn() {
        if (getGameController().gameMode == GameMode.tutorial) return false;
        return SettingsManager.getInstance().confirmToEndTurn;
    }


    public void onEndTurnEventApplied() {
        updateMailTexture();
    }


    public void onMatchStarted() {

    }


    private void createSuperUserButton() {
        uiFactory.getButton()
                .setSize(bSize)
                .alignLeft(0)
                .alignTop(0.1)
                .setTouchOffset(0.06)
                .setIgnoreResumePause(true)
                .setAnimation(AnimationYio.left)
                .setCustomTexture(getTextureFromAtlas("open"))
                .setAllowedToAppear(getSuperUserCondition())
                .setSelectionTexture(selectionTexture);
    }


    private ConditionYio getSuperUserCondition() {
        return new ConditionYio() {
            @Override
            public boolean get() {
                return DebugFlags.superUserEnabled;
            }
        };
    }


    private void loadTextures() {
        selectionTexture = getSelectionTexture();
        flagWhiteTexture = getTextureFromAtlas("flag");
        flagRedTexture = getTextureFromAtlas("red_flag");
        mailWhiteTexture = getTextureFromAtlas("mail_icon");
        mailRedTexture = getTextureFromAtlas("red_mail");
    }

}
