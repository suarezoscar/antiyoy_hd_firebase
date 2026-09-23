package yio.tro.onliyoy.menu.menu_renders;

import yio.tro.onliyoy.menu.MenuViewYio;
import yio.tro.onliyoy.menu.menu_renders.render_custom_list.*;
import yio.tro.onliyoy.menu.menu_renders.rve_renders.*;

import java.util.ArrayList;

public class MenuRenders {

    static ArrayList<RenderInterfaceElement> list = new ArrayList<>();

    public static RenderButton renderButton = new RenderButton();
    public static RenderCheckButton renderCheckButton = new RenderCheckButton();
    public static RenderScrollableArea renderScrollableArea = new RenderScrollableArea();
    public static RenderCircleButton renderCircleButton = new RenderCircleButton();
    public static RenderNotification renderNotification = new RenderNotification();
    public static RenderRoundShape renderRoundShape = new RenderRoundShape();
    public static RenderShadow renderShadow = new RenderShadow();
    public static RenderRoundBorder renderRoundBorder = new RenderRoundBorder();
    public static RenderViewTouchMode renderViewTouchMode = new RenderViewTouchMode();
    public static RenderCustomizableList renderCustomizableList = new RenderCustomizableList();
    public static RenderNativeKeyboard renderNativeKeyboard = new RenderNativeKeyboard();
    public static RenderForefinger renderForefinger = new RenderForefinger();
    public static RenderChoiceListItem renderChoiceListItem = new RenderChoiceListItem();
    public static RenderLabelElement renderLabelElement = new RenderLabelElement();
    public static RenderAdvancedLabelElement renderAdvancedLabelElement = new RenderAdvancedLabelElement();
    public static RenderIcButton renderIcButton = new RenderIcButton();
    public static RenderLightBottomPanel renderLightBottomPanel = new RenderLightBottomPanel();
    public static RenderExceptionViewElement renderExceptionViewElement = new RenderExceptionViewElement();
    public static RenderIconLabelElement renderIconLabelElement = new RenderIconLabelElement();
    public static RenderBigTextItem renderBigTextItem = new RenderBigTextItem();
    public static RenderScrollHelperElement renderScrollHelperElement = new RenderScrollHelperElement();
    public static RenderAnnounceViewElement renderAnnounceViewElement = new RenderAnnounceViewElement();
    public static RenderSlider renderSlider = new RenderSlider();
    public static RenderMultiButtonElement renderMultiButtonElement = new RenderMultiButtonElement();
    public static RenderTopCoverElement renderTopCoverElement = new RenderTopCoverElement();
    public static RenderCustomKeyboardElement renderCustomKeyboardElement = new RenderCustomKeyboardElement();
    public static RenderEntitiesSetupElement renderEntitiesSetupElement = new RenderEntitiesSetupElement();
    public static RenderEconomicsViewElement renderEconomicsViewElement = new RenderEconomicsViewElement();
    public static RenderConstructionViewElement renderConstructionViewElement = new RenderConstructionViewElement();
    public static RenderResizableViewElement renderResizableViewElement = new RenderResizableViewElement();
    public static RenderRveSampleItem renderRveSampleItem = new RenderRveSampleItem();
    public static RenderRveEmptyItem renderRveEmptyItem = new RenderRveEmptyItem();
    public static RenderRveTextItem renderRveTextItem = new RenderRveTextItem();
    public static RenderRveWinnerItem renderRveWinnerItem = new RenderRveWinnerItem();
    public static RenderIncomeGraphElement renderIncomeGraphElement = new RenderIncomeGraphElement();
    public static RenderRveAddConditionItem renderRveAddConditionItem = new RenderRveAddConditionItem();
    public static RenderRveNotificationItem renderRveNotificationItem = new RenderRveNotificationItem();
    public static RenderPlaceholderListItem renderPlaceholderListItem = new RenderPlaceholderListItem();
    public static RenderUiColors renderUiColors = new RenderUiColors();
    public static RenderHighlightAreaElement renderHighlightAreaElement = new RenderHighlightAreaElement();
    public static RenderSectionStartListItem renderSectionStartListItem = new RenderSectionStartListItem();
    public static RenderCondensedEntitiesViewElement renderCondensedEntitiesViewElement = new RenderCondensedEntitiesViewElement();
    public static RenderDelayedActionElement renderDelayedActionElement = new RenderDelayedActionElement();
    public static RenderDarkenElement renderDarkenElement = new RenderDarkenElement();
    public static RenderNumberedSeparatorItem renderNumberedSeparatorItem = new RenderNumberedSeparatorItem();
    public static RenderAdvancedConstructionPanelElement renderAdvancedConstructionPanelElement = new RenderAdvancedConstructionPanelElement();
    public static RenderMechanicsHookElement renderMechanicsHookElement = new RenderMechanicsHookElement();


    public static void updateRenderSystems(MenuViewYio menuViewYio) {
        for (RenderInterfaceElement renderInterfaceElement : list) {
            renderInterfaceElement.update(menuViewYio);
        }
    }
}