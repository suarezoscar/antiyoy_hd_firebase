package yio.tro.onliyoy.menu.scenes;

import yio.tro.onliyoy.menu.elements.AnimationYio;
import yio.tro.onliyoy.menu.elements.BackgroundYio;
import yio.tro.onliyoy.menu.elements.keyboard.AbstractKbReaction;
import yio.tro.onliyoy.menu.elements.keyboard.NativeKeyboardElement;
import yio.tro.onliyoy.menu.reactions.Reaction;

/**
 * Modal de entrada de texto NORMAL (teclado nativo de Android + IME), pensado
 * para pegar URLs/enlaces de invitación. Botón OK para confirmar.
 */
public class SceneFirebaseInput extends ModalSceneYio {

    public NativeKeyboardElement nativeKeyboardElement;
    private AbstractKbReaction reaction;

    @Override
    protected void initialize() {
        nativeKeyboardElement = uiFactory.getNativeKeyboardElement()
                .setSize(1, 0.1);
        createOkButton();
        createCancelButton();
    }

    public void setReaction(AbstractKbReaction reaction) {
        this.reaction = reaction;
        nativeKeyboardElement.setReaction(reaction);
    }

    public void setValue(String value) {
        nativeKeyboardElement.setValue(value);
    }

    private void createOkButton() {
        uiFactory.getButton()
                .setSize(0.3, 0.055)
                .centerHorizontal()
                .alignBottom(0.02)
                .setBackground(BackgroundYio.green)
                .applyText("ok")
                .setReaction(getOkReaction())
                .setAnimation(AnimationYio.down);
    }

    private void createCancelButton() {
        uiFactory.getButton()
                .setSize(0.3, 0.055)
                .alignRight(0.03)
                .alignBottom(0.02)
                .setBackground(BackgroundYio.red)
                .applyText("cancel")
                .setReaction(getCancelReaction())
                .setAnimation(AnimationYio.down);
    }

    private Reaction getOkReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                String text = nativeKeyboardElement.getText();
                if (reaction != null) {
                    reaction.onInputFromKeyboardReceived(text);
                }
                destroy();
            }
        };
    }

    private Reaction getCancelReaction() {
        return new Reaction() {
            @Override
            protected void apply() {
                if (reaction != null) {
                    reaction.onInputCancelled();
                }
                destroy();
            }
        };
    }
}