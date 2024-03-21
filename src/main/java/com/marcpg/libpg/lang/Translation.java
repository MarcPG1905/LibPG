package com.marcpg.libpg.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides easy translations based on simple {@link Properties .properties} files.
 * @since 0.0.6
 * @author MarcPG1905
 */
public class Translation {
    /** The locale to default to if the loaded translations don't contain the given locale. */
    private static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    /**
     * Map of all currently loaded translation files. The first value is the locale of the translation file
     * and the second one is the file as a {@link Properties properties} object representing all the entries
     * with keys and translations, in the same way as a {@link java.util.Map map}
     */
    static final HashMap<Locale, Map<String, String>> TRANSLATIONS = new HashMap<>();

    /**
     * Goes through all of the files inside the specified folder and loads them as {@link Properties}
     * files into the translations. <br>
     * The translation files should follow the simple name scheme {@code [language]_[COUNTRY].properties},
     * so for example {@code en_US.properties}.
     * @param folder The folder in which all the translation files are stored.
     * @throws IOException if the language folder doesn't exist or there was an error while creating the {@link FileInputStream}.
     */
    public static void loadProperties(@NotNull File folder) throws IOException {
        if (!folder.exists()) {
            throw new IOException("The specified translation folder does not exist: " + folder.getAbsolutePath());
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            String[] languageAndCountry = file.getName().replace(".properties", "").split("_");
            TRANSLATIONS.put(new Locale(languageAndCountry[0], languageAndCountry[1]), properties.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()), (prev, next) -> next, HashMap::new)));
        }
    }

    /**
     * Gets the translation of a key as a string.
     * @param locale The language to get the key from. Will use en_US by default, if the locale wasn't found.
     * @param key What translation to get from the locale's translations.
     * @return The translation if everything worked or just the key if the translation wasn't found.
     */
    public static String string(Locale locale, String key) {
        return TRANSLATIONS.get(TRANSLATIONS.containsKey(locale) ? locale : DEFAULT_LOCALE).getOrDefault(key, key);
    }

    /**
     * Gets the translation of a key as a string and replaces the {@link MessageFormat placeholders} with specified {@link Object variables}.
     * @param locale The language to get the key from. Will use en_US by default, if the locale wasn't found.
     * @param key What translation to get from the locale's translations.
     * @param variables The variables that replace the placeholders, in the order {0};{1};{2};...
     * @return The translation with all placeholders replaced with the variables if
     *         everything worked or just the key if the translation wasn't found.
     */
    public static @NotNull String string(Locale locale, String key, Object... variables) {
        return MessageFormat.format(string(locale, key), variables);
    }

    /**
     * Gets the translation of a key as an adventure api {@link Component} ({@link TextComponent}).
     * @param locale The language to get the key from. Will use en_US by default, if the locale wasn't found.
     * @param key What translation to get from the locale's translations.
     * @return The translation if everything worked or just the key if the translation wasn't found.
     */
    public static @NotNull Component component(Locale locale, String key) {
        return Component.text(string(locale, key));
    }

    /**
     * Gets the translation of a key as an adventure api {@link Component} ({@link TextComponent}) and replaces
     * the {@link MessageFormat placeholders} with specified {@link Object variables}.
     * @param locale The language to get the key from. Will use en_US by default, if the locale wasn't found.
     * @param key What translation to get from the locale's translations.
     * @param variables The variables that replace the placeholders, in the order {0};{1};{2};...
     * @return The translation with all placeholders replaced with the variables if
     *         everything worked or just the key if the translation wasn't found.
     */
    public static @NotNull Component component(Locale locale, String key, Object... variables) {
        return Component.text(MessageFormat.format(string(locale, key), variables));
    }
}
