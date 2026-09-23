package yio.tro.onliyoy.menu;

import yio.tro.onliyoy.menu.elements.*;
import yio.tro.onliyoy.menu.elements.button.ButtonYio;
import yio.tro.onliyoy.menu.elements.customizable_list.CustomizableListYio;
import yio.tro.onliyoy.menu.elements.forefinger.ForefingerElement;
import yio.tro.onliyoy.menu.elements.gameplay.ViewTouchModeElement;
import yio.tro.onliyoy.menu.elements.gameplay.income_graph.IncomeGraphElement;
import yio.tro.onliyoy.menu.elements.gameplay.province_ui.AdvancedConstructionPanelElement;
import yio.tro.onliyoy.menu.elements.gameplay.province_ui.ConstructionViewElement;
import yio.tro.onliyoy.menu.elements.gameplay.province_ui.EconomicsViewElement;
import yio.tro.onliyoy.menu.elements.gameplay.province_ui.MechanicsHookElement;
import yio.tro.onliyoy.menu.elements.highlight_area.HighlightAreaElement;
import yio.tro.onliyoy.menu.elements.icon_label_element.IconLabelElement;
import yio.tro.onliyoy.menu.elements.keyboard.CustomKeyboardElement;
import yio.tro.onliyoy.menu.elements.keyboard.NativeKeyboardElement;
import yio.tro.onliyoy.menu.elements.multi_button.MultiButtonElement;
import yio.tro.onliyoy.menu.elements.resizable_element.ResizableViewElement;
import yio.tro.onliyoy.menu.elements.setup_entities.CondensedEntitiesViewElement;
import yio.tro.onliyoy.menu.elements.setup_entities.EntitiesSetupElement;
import yio.tro.onliyoy.menu.elements.setup_entities.SingleEntityConfigureElement;
import yio.tro.onliyoy.menu.elements.slider.SliderElement;
import yio.tro.onliyoy.menu.scenes.SceneYio;

public class UiFactory {

    MenuControllerYio menuControllerYio;
    SceneYio sceneYio;


    public UiFactory(SceneYio sceneYio) {
        this.sceneYio = sceneYio;
        menuControllerYio = sceneYio.menuControllerYio;
    }


    public ButtonYio getButton() {
        ButtonYio buttonYio = new ButtonYio(menuControllerYio);
        addElementToScene(buttonYio);
        return buttonYio;
    }


    public CheckButtonYio getCheckButton() {
        return (CheckButtonYio) addElementToScene(new CheckButtonYio(menuControllerYio));
    }


    public CircleButtonYio getCircleButton() {
        return (CircleButtonYio) addElementToScene(new CircleButtonYio(menuControllerYio));
    }


    public ScrollableAreaYio getScrollableAreaYio() {
        return (ScrollableAreaYio) addElementToScene(new ScrollableAreaYio(menuControllerYio));
    }


    public NotificationElement getNotificationElement() {
        return (NotificationElement) addElementToScene(new NotificationElement(menuControllerYio));
    }


    public ViewTouchModeElement getViewTouchModeElement() {
        return (ViewTouchModeElement) addElementToScene(new ViewTouchModeElement(menuControllerYio));
    }


    public CustomizableListYio getCustomizableListYio() {
        CustomizableListYio customizableListYio = new CustomizableListYio(menuControllerYio);
        return (CustomizableListYio) addElementToScene(customizableListYio);
    }


    public NativeKeyboardElement getNativeKeyboardElement() {
        return (NativeKeyboardElement) addElementToScene(new NativeKeyboardElement(menuControllerYio));
    }


    public ForefingerElement getForefingerElement() {
        return (ForefingerElement) addElementToScene(new ForefingerElement(menuControllerYio));
    }


    public LabelElement getLabelElement() {
        return (LabelElement) addElementToScene(new LabelElement(menuControllerYio));
    }


    public AdvancedLabelElement getAdvancedLabelElement() {
        return (AdvancedLabelElement) addElementToScene(new AdvancedLabelElement(menuControllerYio));
    }


    public ImportantConfirmationButton getImportantConfirmationButton() {
        return (ImportantConfirmationButton) addElementToScene(new ImportantConfirmationButton(menuControllerYio));
    }


    public LightBottomPanelElement getLightBottomPanelElement() {
        return (LightBottomPanelElement) addElementToScene(new LightBottomPanelElement(menuControllerYio));
    }


    public ExceptionViewElement getExceptionViewElement() {
        return (ExceptionViewElement) addElementToScene(new ExceptionViewElement(menuControllerYio));
    }


    public IconLabelElement getIconLabelElement() {
        return (IconLabelElement) addElementToScene(new IconLabelElement(menuControllerYio));
    }


    public AnnounceViewElement getAnnounceViewElement() {
        return (AnnounceViewElement) addElementToScene(new AnnounceViewElement(menuControllerYio));
    }


    public ScrollHelperElement getScrollHelperElement() {
        return (ScrollHelperElement) addElementToScene(new ScrollHelperElement(menuControllerYio));
    }


    public SliderElement getSlider() {
        return (SliderElement) addElementToScene(new SliderElement(menuControllerYio));
    }


    public MultiButtonElement getMultiButtonElement() {
        return (MultiButtonElement) addElementToScene(new MultiButtonElement(menuControllerYio));
    }


    public TopCoverElement getTopCoverElement() {
        return (TopCoverElement) addElementToScene(new TopCoverElement(menuControllerYio));
    }


    public CustomKeyboardElement getCustomKeyboardElement() {
        return (CustomKeyboardElement) addElementToScene(new CustomKeyboardElement(menuControllerYio));
    }


    public EntitiesSetupElement getEntitiesSetupElement() {
        return (EntitiesSetupElement) addElementToScene(new EntitiesSetupElement(menuControllerYio));
    }


    public SingleEntityConfigureElement getSingleEntityConfigureElement() {
        return (SingleEntityConfigureElement) addElementToScene(new SingleEntityConfigureElement(menuControllerYio));
    }


    public EconomicsViewElement getEconomicsViewElement() {
        return (EconomicsViewElement) addElementToScene(new EconomicsViewElement(menuControllerYio));
    }


    public ConstructionViewElement getConstructionViewElement() {
        return (ConstructionViewElement) addElementToScene(new ConstructionViewElement(menuControllerYio));
    }


    public ResizableViewElement getResizableViewElement() {
        return (ResizableViewElement) addElementToScene(new ResizableViewElement(menuControllerYio));
    }


    public IncomeGraphElement getIncomeGraphElement() {
        return (IncomeGraphElement) addElementToScene(new IncomeGraphElement(menuControllerYio));
    }


    public HighlightAreaElement getHighlightAreaElement() {
        return (HighlightAreaElement) addElementToScene(new HighlightAreaElement(menuControllerYio));
    }


    public CondensedEntitiesViewElement getCondensedEntitiesViewElement() {
        return (CondensedEntitiesViewElement) addElementToScene(new CondensedEntitiesViewElement(menuControllerYio));
    }


    public DelayedActionElement getDelayedActionElement() {
        return (DelayedActionElement) addElementToScene(new DelayedActionElement(menuControllerYio));
    }


    public DarkenElement getDarkenElement() {
        return (DarkenElement) addElementToScene(new DarkenElement(menuControllerYio));
    }


    public AdvancedConstructionPanelElement getAdvancedConstructionPanelElement() {
        return (AdvancedConstructionPanelElement) addElementToScene(new AdvancedConstructionPanelElement(menuControllerYio));
    }


    public MechanicsHookElement getMechanicsHookElement() {
        return (MechanicsHookElement) addElementToScene(new MechanicsHookElement(menuControllerYio));
    }


    private InterfaceElement addElementToScene(InterfaceElement interfaceElement) {
        sceneYio.addLocalElement(interfaceElement);
        menuControllerYio.addElement(interfaceElement);
        return interfaceElement;
    }
}