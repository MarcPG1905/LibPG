package com.marcpg.text;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used for autocompletion, suggestions and search.
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Completer {
    private Completer() {
        throw new AssertionError("Instantiating static-only utility class.");
    }

    /**
     * Filter suggestions based on what was already typed. <br>
     * If suggested has "max", "cat", "thomas" and "mouse" in it, <br>
     * input = "c" would suggest "<b>c</b>at" <br>
     * input = "m" would suggest "<b>m</b>ouse" and "<b>m</b>ax"
     * @param input What was already typed
     * @param suggested All things that can be suggested
     * @return A {@link List list} of filtered suggestions based on input and suggested
     * @see #startComplete(String, String[])
     */
    public static List<String> startComplete(String input, @NotNull Collection<String> suggested) {
        return suggested.stream().filter(string -> string.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * Filter suggestions based on what was already typed. <br>
     * If suggested has "max", "cat", "thomas" and "mouse" in it, <br>
     * input = "c" would suggest "cat" <br>
     * input = "m" would suggest "mouse" and "max"
     * @param input What was already typed
     * @param suggested All things that can be suggested
     * @return A {@link List list} of filtered suggestions based on input and suggested
     * @see #startComplete(String, Collection)
     */
    public static List<String> startComplete(String input, String[] suggested) {
        return Arrays.stream(suggested).filter(string -> string.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * Filter suggestions based on what was already typed. <br>
     * If suggested has "max", "cat", "thomas" and "mouse" in it, <br>
     * input = "c" would suggest "<b>c</b>at" <br>
     * input = "m" would suggest "<b>m</b>ouse", "<b>m</b>ax" and "tho<b>m</b>as"
     * @param input What was already typed
     * @param suggested All things that can be suggested
     * @return A {@link List list} of filtered suggestions based on input and suggested
     * @see #containComplete(String, String[])
     */
    public static List<String> containComplete(String input, @NotNull Collection<String> suggested) {
        return suggested.stream().filter(string -> string.toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * Filter suggestions based on what was already typed. <br>
     * If suggested has "max", "cat", "thomas" and "mouse" in it, <br>
     * input = "c" would suggest "<b>c</b>at" <br>
     * input = "m" would suggest "<b>m</b>ouse", "<b>m</b>ax" and "tho<b>m</b>as"
     * @param input What was already typed
     * @param suggested All things that can be suggested
     * @return A {@link List list} of filtered suggestions based on input and suggested
     * @see #containComplete(String, Collection)
     */
    public static List<String> containComplete(String input, String[] suggested) {
        return Arrays.stream(suggested).filter(string -> string.toLowerCase().contains(input.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * Filter suggestions based on what was already typed. <br>
     * Combines {@link #startComplete(String, Collection)} and {@link #containComplete(String, Collection)}. <br>
     * If the length of the input is three characters or less, it will use start completion and if the input is over three characters long, it will use contain completion. <br>
     * This ensures that the amount of suggested strings is limited and doesn't reach too high numbers.
     * @param input What was already typed
     * @param suggested All things that can be suggested
     * @return A {@link List list} of filtered suggestions based on input and suggested
     * @see #semiSmartComplete(String, String[])
     * @since 0.0.2
     */
    public static List<String> semiSmartComplete(@NotNull String input, Collection<String> suggested) {
        if (input.length() >= 3) {
            return startComplete(input, suggested);
        } else {
            return containComplete(input, suggested);
        }
    }

    /**
     * Filter suggestions based on what was already typed. <br>
     * Combines {@link #startComplete(String, String[])} and {@link #containComplete(String, String[])}. <br>
     * If the length of the input is three characters or less, it will use start completion and if the input is over three characters long, it will use contain completion. <br>
     * This ensures that the amount of suggested strings is limited and doesn't reach too high numbers.
     * @param input What was already typed
     * @param suggested All things that can be suggested
     * @return A {@link List list} of filtered suggestions based on input and suggested
     * @see #semiSmartComplete(String, Collection)
     * @since 0.0.2
     */
    public static List<String> semiSmartComplete(@NotNull String input, String[] suggested) {
        if (input.length() >= 3) {
            return startComplete(input, suggested);
        } else {
            return containComplete(input, suggested);
        }
    }
}
