package com.marcpg.libpg.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    static final Map<Locale, Map<String, String>> TRANSLATIONS = new HashMap<>();

    /**
     * Goes through all of the files inside the specified folder and loads them as {@link Properties}
     * files into the translations. <br>
     * The translation files should follow the simple name scheme {@code [language]_[COUNTRY].properties},
     * so for example {@code en_US.properties}.
     * @param folder The folder in which all the translation files are stored.
     * @throws IOException if the language folder doesn't exist or there was an error while creating the {@link FileInputStream}.
     */
    public static void loadProperties(@NotNull File folder) throws IOException {
        if (!folder.exists())
            throw new IOException("The specified translation folder does not exist: " + folder.getAbsolutePath());

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            String[] languageAndCountry = file.getName().replace(".properties", "").split("_");
            loadSingleProperties(new Locale(languageAndCountry[0], languageAndCountry[1]), properties);
        }
    }

    /**
     * Goes through all of the properties and loads them as {@link Properties .properties} files into the translations.
     * @param languages All the properties files and their languages.
     */
    public static void loadProperties(@NotNull Map<Locale, Properties> languages) {
        languages.forEach(Translation::loadSingleProperties);
    }

    /**
     * Loads a {@link Properties .properties} file into the translations.
     * @param locale The locale to load into the translations.
     * @param properties The properties object to load.
     */
    public static void loadSingleProperties(Locale locale, @NotNull Properties properties) {
        TRANSLATIONS.put(locale, properties.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()), (prev, next) -> next, HashMap::new)));
    }

    /**
     * Goes through all of the maps and locales and saves them into the translations.
     * @param maps All maps and their languages.
     */
    public static void loadMaps(@NotNull Map<Locale, Map<String, String>> maps) {
        maps.forEach(Translation::loadSingleMap);
    }

    /**
     * Loads a {@link Map} file into the translations.
     * @param locale The locale to load into the translations.
     * @param map The map object to load.
     */
    public static void loadSingleMap(Locale locale, @NotNull Map<String, String> map) {
        TRANSLATIONS.put(locale, map);
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

    /**
     * Reverse searches through all languages and all their values, to find out in what language and under
     * what key this translation can be found.
     * @param value The translation to search from. Can be any language.
     * @return A map with all the locales and keys that the translation is found at.
     *         Null if the reverse search didn't give any results.
     */
    public static @Nullable Map<Locale, String> reverse(String value) {
        Map<Locale, String> result = new HashMap<>();
        for (Map.Entry<Locale, Map<String, String>> localeEntry : TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> translation : localeEntry.getValue().entrySet()) {
                if (value.equals(translation.getValue())) {
                    result.put(localeEntry.getKey(), translation.getKey());
                }
            }
        }
        return result.isEmpty() ? null : result;
    }
}
