package ru.anton.dialogpasshider.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DialogPassHiderConfig {

    public static final String DEFAULT_MASK_SYMBOL = "*";

    private static final String MASK_SYMBOL_PROPERTY = "mask-symbol";
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("dialog-pass-hider.properties");

    private static boolean loaded;
    private static String maskSymbol = DEFAULT_MASK_SYMBOL;

    private DialogPassHiderConfig() {
    }

    public static synchronized String getMaskSymbol() {
        loadIfNeeded();
        return maskSymbol;
    }

    public static synchronized void setMaskSymbol(String value) {
        loadIfNeeded();
        maskSymbol = normalizeMaskSymbol(value);
        save();
    }

    public static String normalizeMaskSymbol(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_MASK_SYMBOL;
        }

        int codePoint = value.codePointAt(0);
        return new String(Character.toChars(codePoint));
    }

    private static void loadIfNeeded() {
        if (loaded) {
            return;
        }

        loaded = true;
        if (!Files.isRegularFile(CONFIG_PATH)) {
            return;
        }

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            properties.load(reader);
            maskSymbol = normalizeMaskSymbol(properties.getProperty(
                MASK_SYMBOL_PROPERTY,
                DEFAULT_MASK_SYMBOL
            ));
        } catch (IOException exception) {
            System.err.println("[Dialog Pass Hider] Could not read config: " + exception.getMessage());
        }
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty(MASK_SYMBOL_PROPERTY, maskSymbol);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                properties.store(writer, "Dialog Pass Hider");
            }
        } catch (IOException exception) {
            System.err.println("[Dialog Pass Hider] Could not save config: " + exception.getMessage());
        }
    }
}
