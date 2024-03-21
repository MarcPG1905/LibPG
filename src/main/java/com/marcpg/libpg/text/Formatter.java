package com.marcpg.libpg.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to format text.
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Formatter {
    private Formatter() {
        throw new AssertionError("Instantiating static-only utility class.");
    }

    /**
     * Convert a {@link String string} in "TEST_STRING" format to pascal case: "Test String".
     * @param in The {@link String string} that should be converted.
     * @return The {@link String string} but formatted to pascal case.
     *         For example, "HUMAN_LIFE" becomes "Human Life".
     */
    public static @NotNull String toPascalCase(@NotNull String in) {
        StringBuilder out = new StringBuilder();
        String[] words = in.toLowerCase().split("[_\\- ]");

        for (String word : words) {
            if (!out.isEmpty()) out.append(" ");

            out.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1)
                out.append(word.substring(1));
        }

        return out.toString();
    }

    /**
     * Wraps a long string into multiple lines based on a max length/width per line.
     * Will split based on words separated by spaces, to avoid splitting in the middle of a word.
     * @param text The text that should be line wrapped.
     * @param maxWidth The maximum length/width per line in characters.
     * @return The line-wrapped text, as a list where each value represents a line.
     * @since 0.0.8
     */
    public static @Unmodifiable List<String> lineWrap(String text, int maxWidth) {
        if (maxWidth <= 0)
            throw new IllegalArgumentException("Maximum line length cannot be 0 or lower!");
        if (text == null)
            return List.of();
        if (text.isEmpty() || text.length() <= maxWidth)
            return List.of(text);

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentWidth = 0;

        for (String word : text.split("\\s+")) {
            if (currentWidth + word.length() + 1 > maxWidth) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
                currentWidth = 0;
            }
            currentLine.append(word).append(" ");
            currentWidth += word.length() + 1;
        }

        if (!currentLine.isEmpty())
            lines.add(currentLine.toString());

        return lines;
    }
}
