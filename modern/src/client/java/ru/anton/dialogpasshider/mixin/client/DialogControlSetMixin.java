package ru.anton.dialogpasshider.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.dialog.DialogControlSet;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandlers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.input.TextInput;
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

@Mixin(DialogControlSet.class)
public abstract class DialogControlSetMixin {

    @Unique
    private static final FontDescription dialogPassHider$eyeIconFont =
        new FontDescription.Resource(
            Identifier.fromNamespaceAndPath("dialog_pass_hider", "eye_icons")
        );

    @Shadow
    @Final
    private DialogScreen<?> screen;

    @Shadow
    @Final
    private Map<String, Action.ValueGetter> valueGetters;

    @Unique
    private boolean dialogPassHider$hasPasswordInput;

    @Inject(method = "addInput", at = @At("HEAD"), cancellable = true)
    private void dialogPassHider$maskPasswordInput(
        Input input,
        Consumer<LayoutElement> output,
        CallbackInfo callbackInfo
    ) {
        if (!(input.control() instanceof TextInput)
            || !dialogPassHider$shouldMask(input.key())) {
            return;
        }

        InputControlHandlers.createHandler(input.control(), screen, (element, valueGetter) -> {
            EditBox[] editBox = new EditBox[1];
            element.visitWidgets(widget -> {
                if (widget instanceof EditBox field) {
                    editBox[0] = field;
                }
            });

            LayoutElement outputElement = element;
            if (editBox[0] != null) {
                boolean[] passwordVisible = {false};
                editBox[0].addFormatter((value, firstCharacterIndex) ->
                    passwordVisible[0]
                        ? null
                        : Component.literal(
                            DialogPassHiderConfig.getMaskSymbol().repeat(value.length())
                        ).getVisualOrderText()
                );

                Button eyeButton = Button.builder(
                    dialogPassHider$eyeIcon(false),
                    button -> {
                        passwordVisible[0] = !passwordVisible[0];
                        dialogPassHider$updateEyeButton(button, passwordVisible[0]);
                    }
                ).bounds(0, 0, 24, 20).build();
                dialogPassHider$updateEyeButton(eyeButton, false);

                LinearLayout row = LinearLayout.horizontal().spacing(4);
                row.addChild(element);
                row.addChild(eyeButton, settings -> settings.alignVerticallyBottom());
                row.arrangeElements();
                outputElement = row;
            }

            valueGetters.put(input.key(), valueGetter);
            output.accept(outputElement);
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
        Button button,
        boolean passwordVisible
    ) {
        button.setMessage(dialogPassHider$eyeIcon(passwordVisible));
        button.setTooltip(Tooltip.create(Component.translatable(
            passwordVisible ? "dialog_pass_hider.eye.hide" : "dialog_pass_hider.eye.show"
        )));
    }

    @Unique
    private static Component dialogPassHider$eyeIcon(boolean passwordVisible) {
        return Component.literal(passwordVisible ? "\ue001" : "\ue000")
            .withStyle(style -> style.withFont(dialogPassHider$eyeIconFont));
    }
}
