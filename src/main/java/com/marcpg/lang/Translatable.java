package com.marcpg.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Locale;

/**
 * Simple abstract class for making every translatable class organized into the same translation methods.
 * @since 0.0.6
 * @author MarcPG1905
 */
public abstract class Translatable {
    /**
     * Translates the class based on the given locale and outputs it as a {@link String} representation.
     * @param locale The locale which will be translated to.
     * @return The translated result as a string.
     */
    public abstract String getTranslated(Locale locale);

    /**
     * Translates the class based on the given locale and outputs it as an adventure api
     * {@link Component} ({@link TextComponent}) representation.
     * @param locale The locale which will be translated to.
     * @return The translated result as a string.
     */
    public Component getTranslatedComponent(Locale locale) { return Component.empty(); }

    /**
     * This is a secondary method to be used, if only one translation isn't enough. <br>
     * Translates the class based on the given locale and outputs it as a {@link String} representation.
     * @param locale The locale which will be translated to.
     * @return The translated result as a string.
     */
    public String getSecondaryTranslated(Locale locale) { return null; }

    /**
     * This is a secondary method to be used, if only one translation isn't enough. <br>
     * Translates the class based on the given locale and outputs it as an adventure api
     * {@link Component} ({@link TextComponent}) representation.
     * @param locale The locale which will be translated to.
     * @return The translated result as a string.
     */
    public Component getSecondaryTranslatedComponent(Locale locale) { return null; }
}
