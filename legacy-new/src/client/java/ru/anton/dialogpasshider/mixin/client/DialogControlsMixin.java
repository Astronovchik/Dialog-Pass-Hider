package ru.anton.dialogpasshider.mixin.client;

import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.InputControlHandlers;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.anton.dialogpasshider.config.DialogPassHiderConfig;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(DialogControls.class)
public abstract class DialogControlsMixin {

    @Unique
    private static final StyleSpriteSource dialogPassHider$eyeIconFont =
        new StyleSpriteSource.Font(Identifier.of("dialog_pass_hider", "eye_icons"));

    @Shadow
    @Final
    private DialogScreen<?> screen;

    @Shadow
    @Final
    private Map<String, DialogAction.ValueGetter> valueGetters;

    @Unique
    private boolean dialogPassHider$hasPasswordInput;

    @Inject(method = "addInput", at = @At("HEAD"), cancellable = true)
    private void dialogPassHider$maskPasswordInput(
        DialogInput input,
        Consumer<Widget> widgetConsumer,
        CallbackInfo callbackInfo
    ) {
        if (!(input.control() instanceof TextInputControl)
            || !dialogPassHider$shouldMask(input.key())) {
            return;
        }

        InputControlHandlers.addControl(input.control(), screen, (widget, valueGetter) -> {
            TextFieldWidget[] textField = new TextFieldWidget[1];
            widget.forEachChild(child -> {
                if (child instanceof TextFieldWidget field) {
                    textField[0] = field;
                }
            });

            Widget outputWidget = widget;
            if (textField[0] != null) {
                boolean[] passwordVisible = {false};
                textField[0].addFormatter((value, firstCharacterIndex) ->
                    passwordVisible[0]
                        ? null
                        : Text.literal(DialogPassHiderConfig.getMaskSymbol().repeat(value.length()))
                            .asOrderedText()
                );

                ButtonWidget eyeButton = ButtonWidget.builder(
                    dialogPassHider$eyeIcon(false),
                    button -> {
                        passwordVisible[0] = !passwordVisible[0];
                        dialogPassHider$updateEyeButton(button, passwordVisible[0]);
                    }
                ).dimensions(0, 0, 24, 20).build();
                dialogPassHider$updateEyeButton(eyeButton, false);

                DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(4);
                row.add(widget);
                row.add(eyeButton, positioner -> positioner.alignBottom());
                row.refreshPositions();
                outputWidget = row;
            }

            valueGetters.put(input.key(), valueGetter);
            widgetConsumer.accept(outputWidget);
        });

        callbackInfo.cancel();
    }

    @Unique
    private boolean dialogPassHider$shouldMask(String key) {
        String normalized = key.toLowerCase(Locale.ROOT).replace('-', '_');
        boolean confirmation = normalized.equals("confirm")
            || normalized.equals("confirmation")
            || normalized.contains("confirm_password")
            || normalized.contains("password_confirm");
        boolean password = !confirmation && (
            normalized.equals("pass")
                || normalized.equals("passwd")
                || normalized.contains("password")
        );

        if (password) {
            dialogPassHider$hasPasswordInput = true;
        }

        return password || (confirmation && dialogPassHider$hasPasswordInput);
    }

    @Unique
    private static void dialogPassHider$updateEyeButton(
        ButtonWidget button,
        boolean passwordVisible
    ) {
        button.setMessage(dialogPassHider$eyeIcon(passwordVisible));
        button.setTooltip(Tooltip.of(Text.translatable(
            passwordVisible ? "dialog_pass_hider.eye.hide" : "dialog_pass_hider.eye.show"
        )));
    }

    @Unique
    private static Text dialogPassHider$eyeIcon(boolean passwordVisible) {
        return Text.literal(passwordVisible ? "\ue001" : "\ue000")
            .styled(style -> style.withFont(dialogPassHider$eyeIconFont));
    }
}
