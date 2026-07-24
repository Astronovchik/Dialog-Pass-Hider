package ru.anton.authmepasswordmask.mixin.client;

import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.InputControlHandlers;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Adds a visual formatter to dialog text inputs whose key is {@code password}.
 *
 * <p>The formatter only changes rendered text. The field's stored value and the
 * value submitted to the server remain unchanged.</p>
 */
@Mixin(DialogControls.class)
public abstract class DialogControlsMixin {

    private static final String PASSWORD_INPUT_KEY = "password";

    @Shadow
    @Final
    private DialogScreen<?> screen;

    @Shadow
    @Final
    private Map<String, DialogAction.ValueGetter> valueGetters;

    @Inject(method = "addInput", at = @At("HEAD"), cancellable = true)
    private void authmePasswordMask$maskPasswordInput(
        DialogInput input,
        Consumer<Widget> widgetConsumer,
        CallbackInfo callbackInfo
    ) {
        if (!PASSWORD_INPUT_KEY.equals(input.key())
            || !(input.control() instanceof TextInputControl)) {
            return;
        }

        InputControlHandlers.addControl(input.control(), screen, (widget, valueGetter) -> {
            widget.forEachChild(child -> {
                if (child instanceof TextFieldWidget textField) {
                    textField.addFormatter((value, firstCharacterIndex) ->
                        Text.literal("*".repeat(value.length())).asOrderedText());
                }
            });

            valueGetters.put(input.key(), valueGetter);
            widgetConsumer.accept(widget);
        });

        callbackInfo.cancel();
    }
}
