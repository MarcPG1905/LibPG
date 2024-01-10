package com.marcpg.libpg.text;

/**
 * Used to format text.
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Formatter {
    /**
     * Convert a {@link String string} in "TEST_STRING" format to pascal case: "Test String".
     *
     * @param in The {@link String string} that should be converted.
     * @return The {@link String string} but formatted to pascal case.
     *         For example, "HUMAN_LIFE" becomes "Human Life".
     * @since 0.0.1
     */
    public static String toPascalCase(String in) {
        StringBuilder out = new StringBuilder();
        String[] words = in.toLowerCase().split("[_\\- ]");

        for (String word : words) {
            if (!out.isEmpty()) out.append(" ");

            out.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                out.append(word.substring(1));
            }
        }

        return out.toString();
    }
}
