package com.marcpg.libpg.color;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Ansi formatting can be used in nearly any text-based console.
 * It is very commonly used and supports many different formatting options, from bold, to blinking, to colored text, just everything.
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Ansi {
    /** Resets all formatting */ public static final Ansi RESET = new Ansi(0);
    /** Resets the foreground color */ public static final Ansi RESET_FG = new Ansi(39);
    /** Resets the background color */ public static final Ansi RESET_BG = new Ansi(49);
    /** Makes the text bold/thick */ public static final Ansi BOLD = new Ansi(1);
    /** Makes the text italic/cursive */ public static final Ansi ITALIC = new Ansi(3);
    /** Underlines the text */ public static final Ansi UNDERLINE = new Ansi(4);
    /** Makes the text blink, not widely supported */ public static final Ansi BLINK = new Ansi(5);
    /** Swaps the foreground and background colors */ public static final Ansi SWAP_BG_FG = new Ansi(6);
    /** Makes the text strikethrough */ public static final Ansi STRIKETHROUGH = new Ansi(9);
    /** Resets the font */ public static final Ansi DEFAULT_FONT = new Ansi(10);
    /** Use the alternate font */ public static final Ansi ALT_FONT = new Ansi(11);
    /** Turns underlined off */ public static final Ansi UNDERLINE_OFF = new Ansi(24);
    /** Turns the blinking off */ public static final Ansi BLINK_OFF = new Ansi(25);
    /** Turns italic/cursive off */ public static final Ansi ITALIC_OFF = new Ansi(27);
    /** Turns strikethrough off */ public static final Ansi STRIKETHROUGH_OFF = new Ansi(29);
    /** Good Question */ public static final Ansi FRAMED = new Ansi(51);
    /** Good Question */ public static final Ansi ENCIRCLED = new Ansi(52);
    /** Overlines the text, not widely supported */ public static final Ansi OVERLINED = new Ansi(53);
    /** Black */ public static final Ansi BLACK = new Ansi(30, false);
    /** Dark Red */ public static final Ansi DARK_RED = new Ansi(31, false);
    /** Dark Green */ public static final Ansi GREEN = new Ansi(32, false);
    /** Brown / Orange / Ugly Yellow */ public static final Ansi BROWN = new Ansi(33, false);
    /** Blue */ public static final Ansi BLUE = new Ansi(34, false);
    /** Dark Magenta */ public static final Ansi MAGENTA = new Ansi(35, false);
    /** Cyan / Aqua */ public static final Ansi CYAN = new Ansi(36, false);
    /** Gray */ public static final Ansi GRAY = new Ansi(37, false);
    /** Dark Gray */ public static final Ansi DARK_GRAY = new Ansi(90, false);
    /** Red */ public static final Ansi RED = new Ansi(91, false);
    /** Lime / Bright Green */ public static final Ansi LIME = new Ansi(92, false);
    /** Yellow */ public static final Ansi YELLOW = new Ansi(93, false);
    /** Bright Blue */ public static final Ansi BRIGHT_BLUE = new Ansi(94, false);
    /** Pink / Bright Magenta */ public static final Ansi PINK = new Ansi(95, false);
    /** Bright Cyan/Aqua */ public static final Ansi BRIGHT_CYAN = new Ansi(96, false);
    /** White, same as {@link #RESET_FG} / {@link #RESET_BG} */ public static final Ansi WHITE = new Ansi(97, false);

    private final int code;
    private int codeG, codeB = -1;

    /** If it's a color or just a format */
    private final boolean color;

    /** If the color is a custom RGB color or just a basic 4 bit color */
    private final boolean rgbColor;

    @SuppressWarnings("SameParameterValue")
    private Ansi(int c, boolean rgb) {
        this.code = c;
        this.rgbColor = rgb;
        this.color = true;
    }

    private Ansi(int c) {
        this.code = c;
        this.rgbColor = false;
        this.color = false;
    }

    private Ansi(int r, int g, int b) {
        this.code = r;
        this.codeG = g;
        this.codeB = b;
        this.rgbColor = true;
        this.color = true;
    }

    private @NotNull String get(boolean bg) {
        StringBuilder ansi = new StringBuilder("\033[");
        if (!color) {
            ansi.append(code);
        } else {
            ansi.append(bg ? "4" : "3");
            if (rgbColor) {
                ansi.append("8;2;")
                        .append(code).append(";")
                        .append(codeG).append(";")
                        .append(codeB);
            } else {
                ansi.append("0;").append(code);
            }
        }
        return ansi.append('m').toString();
    }

    /**
     * Gets the color as a background color.
     * This only works if it's really a color, otherwise it just returns the same as {@link #get()}
     * @return The color as a background color
     * @since 0.0.1
     */
    public String getBackground() {
        return get(true);
    }

    /**
     * Gets the format as a string, so it's usable.
     * @return The ansi escape sequence
     * @since 0.0.1
     */
    public String get() {
        return get(false);
    }

    /**
     * Same as {@link #get()}
     * @return The ansi escape sequence
     * @since 0.0.1
     */
    @Override
    public String toString() {
        return get();
    }

    /**
     * Get a custom {@link Ansi} color based on red, green and blue color values.
     * @param r Red (0-255)
     * @param g Green (0-255)
     * @param b Blue (0-255)
     * @return A custom ansi color
     * @since 0.0.1
     */
    public static @NotNull Ansi fromRGB(int r, int g, int b) {
        return new Ansi(r, g, b);
    }

    /**
     * Get a custom {@link Ansi} color based on a Java AWT color.
     * @param color The Java AWT color
     * @return A custom ansi color
     * @since 0.0.1
     */
    public static @NotNull Ansi fromColor(@NotNull Color color) {
        return new Ansi(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Apply a custom ansi format to a string
     * @param text The string that should be formatted
     * @param formats What format should be applied
     * @return The formatted string
     * @since 0.0.1
     */
    public static @NotNull String formattedString(String text, Ansi @NotNull ... formats) {
        StringBuilder builder = new StringBuilder();
        for (Ansi format : formats) builder.append(format.get());
        return builder + text + Ansi.RESET;
    }

    /**
     * Colors a specified text {@link #DARK_GRAY gray}, which can be used for secondary text for example. <br>
     * Uses dark gray, as some consoles make {@link #GRAY normal gray} appear white.
     * @param text The text to color gray.
     * @return The colored text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String gray(String text) {
        return formattedString(text, DARK_GRAY);
    }

    /**
     * Colors a specified text {@link #RED red}, which can be used for errors for example.
     * @param text The text to color red.
     * @return The colored text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String red(String text) {
        return formattedString(text, RED);
    }

    /**
     * Colors a specified text {@link #YELLOW yellow}, which can be used for warnings for example.
     * @param text The text to color yellow.
     * @return The colored text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String yellow(String text) {
        return formattedString(text, YELLOW);
    }

    /**
     * Colors a specified text {@link #GREEN green}, which can be used for infos or successes for example.
     * @param text The text to color green.
     * @return The colored text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String green(String text) {
        return formattedString(text, GREEN);
    }

    /**
     * Makes the specified text {@link #BLINK blink}.
     * @param text The text to make blink.
     * @return The blinking text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String blink(String text) {
        return formattedString(text, BLINK);
    }

    /**
     * Makes the specified text {@link #BOLD bold/thick}.
     * @param text The text to make bold/thick.
     * @return The bold/thick text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String bold(String text) {
        return formattedString(text, BOLD);
    }

    /**
     * Makes the specified text {@link #ITALIC italic}.
     * @param text The text to make italic.
     * @return The italic text.
     * @see #formattedString(String, Ansi...)
     */
    public static @NotNull String italic(String text) {
        return formattedString(text, ITALIC);
    }
}
