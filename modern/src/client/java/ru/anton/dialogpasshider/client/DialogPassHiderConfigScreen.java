package ru.anton.dialogpasshider.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.anton.dialogpasshider.config.DialogPassHiderConfig;

public final class DialogPassHiderConfigScreen extends Screen {

    private static final int CONTENT_WIDTH = 200;

    private final Screen parent;
    private EditBox symbolField;
    private StringWidget preview;

    public DialogPassHiderConfigScreen(Screen parent) {
        super(Component.translatable("dialog_pass_hider.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = (width - CONTENT_WIDTH) / 2;
        int top = height / 2 - 70;

        addRenderableWidget(new StringWidget(
            left,
            top,
            CONTENT_WIDTH,
            20,
            title,
            font
        ));

        addRenderableWidget(new StringWidget(
            left,
            top + 28,
            CONTENT_WIDTH,
            12,
            Component.translatable("dialog_pass_hider.config.mask_symbol"),
            font
        ));

        symbolField = addRenderableWidget(new EditBox(
            font,
            left,
            top + 44,
            CONTENT_WIDTH,
            20,
            Component.translatable("dialog_pass_hider.config.mask_symbol")
        ));
        symbolField.setMaxLength(2);
        symbolField.setValue(DialogPassHiderConfig.getMaskSymbol());

        preview = addRenderableWidget(new StringWidget(
            left,
            top + 70,
            CONTENT_WIDTH,
            12,
            previewText(symbolField.getValue()),
            font
        ));
        symbolField.setResponder(value -> preview.setMessage(previewText(value)));

        addRenderableWidget(Button.builder(
            Component.translatable("dialog_pass_hider.config.reset"),
            button -> symbolField.setValue(DialogPassHiderConfig.DEFAULT_MASK_SYMBOL)
        ).bounds(left, top + 96, 98, 20).build());

        addRenderableWidget(Button.builder(
            Component.translatable("gui.done"),
            button -> saveAndClose()
        ).bounds(left + 102, top + 96, 98, 20).build());
    }

    private Component previewText(String value) {
        String symbol = DialogPassHiderConfig.normalizeMaskSymbol(value);
        return Component.translatable("dialog_pass_hider.config.preview", symbol.repeat(8));
    }

    private void saveAndClose() {
        DialogPassHiderConfig.setMaskSymbol(symbolField.getValue());
        onClose();
    }

    @Override
    public void onClose() {
        try {
            minecraft.getClass()
                .getMethod("setScreen", Screen.class)
                .invoke(minecraft, parent);
            return;
        } catch (ReflectiveOperationException ignored) {
            // Minecraft 26.2 moved setScreen from Minecraft to Gui.
        }

        try {
            minecraft.gui.getClass()
                .getMethod("setScreen", Screen.class)
                .invoke(minecraft.gui, parent);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to close Dialog Pass Hider config", exception);
        }
    }
}
