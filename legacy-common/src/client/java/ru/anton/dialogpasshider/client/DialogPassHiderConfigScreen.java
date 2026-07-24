package ru.anton.dialogpasshider.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import ru.anton.dialogpasshider.config.DialogPassHiderConfig;

public final class DialogPassHiderConfigScreen extends Screen {

    private static final int CONTENT_WIDTH = 200;

    private final Screen parent;
    private TextFieldWidget symbolField;
    private TextWidget preview;

    public DialogPassHiderConfigScreen(Screen parent) {
        super(Text.translatable("dialog_pass_hider.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = (width - CONTENT_WIDTH) / 2;
        int top = height / 2 - 70;

        addDrawableChild(new TextWidget(
            left,
            top,
            CONTENT_WIDTH,
            20,
            title,
            textRenderer
        ));

        addDrawableChild(new TextWidget(
            left,
            top + 28,
            CONTENT_WIDTH,
            12,
            Text.translatable("dialog_pass_hider.config.mask_symbol"),
            textRenderer
        ));

        symbolField = addDrawableChild(new TextFieldWidget(
            textRenderer,
            left,
            top + 44,
            CONTENT_WIDTH,
            20,
            Text.translatable("dialog_pass_hider.config.mask_symbol")
        ));
        symbolField.setMaxLength(2);
        symbolField.setText(DialogPassHiderConfig.getMaskSymbol());

        preview = addDrawableChild(new TextWidget(
            left,
            top + 70,
            CONTENT_WIDTH,
            12,
            previewText(symbolField.getText()),
            textRenderer
        ));
        symbolField.setChangedListener(value -> preview.setMessage(previewText(value)));

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("dialog_pass_hider.config.reset"),
            button -> symbolField.setText(DialogPassHiderConfig.DEFAULT_MASK_SYMBOL)
        ).dimensions(left, top + 96, 98, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> saveAndClose()
        ).dimensions(left + 102, top + 96, 98, 20).build());
    }

    private Text previewText(String value) {
        String symbol = DialogPassHiderConfig.normalizeMaskSymbol(value);
        return Text.translatable("dialog_pass_hider.config.preview", symbol.repeat(8));
    }

    private void saveAndClose() {
        DialogPassHiderConfig.setMaskSymbol(symbolField.getText());
        close();
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
