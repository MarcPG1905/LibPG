package com.marcpg.libpg.color;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * <a href="https://minecraft.net/">Minecraft</a> Colors <br>
 * From the <a href="https://minecraft.fandom.com/wiki/Formatting_codes#Usage">Formatting Codes article</a> on the <a href="https://minecraft.fandom.com/wiki">Minecraft Wiki</a>
 * @since 0.0.1
 * @author MarcPG1905
 */
@SuppressWarnings("SpellCheckingInspection")
public enum McFormat {
    /** Reset the color to the default one */ RESET('r', null, true, true, 0),
    /** Makes the text constantly change */ MAGIC('k', null, true, true, 8),
    /** Bold */ BOLD('l', null, true, true, 1),
    /** Italic */ ITALIC('o', null, true, true, 3),
    /** Strikethrough */ STRIKETHROUGH('m', null, true, false, 9),
    /** Underlined */ UNDERLINED('n', null, true, false, 4),
    /** Dark Red */ DARK_RED('4', "AA0000", true, true, 31),
    /** Light Red */ RED('c', "FF5555", true, true, 91),
    /** Gold / Orange */ GOLD('6', "FFAA00", true, true, 33),
    /** Yellow */ YELLOW('e', "FFFF55", true, true, 93),
    /** Dark Green */ GREEN('2', "00AA00", true, true, 32),
    /** Light Green / Lime */ LIME('a', "55FF55", true, true, 92),
    /** Light Aqua */ AQUA('b', "55FFFF", true, true, 96),
    /** Dark Aqua */ DARK_AQUA('3', "00AAAA", true, true, 36),
    /** Dark Blue */ DARK_BLUE('1', "0000AA", true, true, 34),
    /** Light Blue */ BLUE('9', "5555FF", true, true, 94),
    /** Light Pink */ PINK('d', "FF55FF", true, true, 95),
    /** Purple / Magenta */ PURPLE('5', "AA00AA", true, true, 35),
    /** White */ WHITE('f', "FFFFFF", true, true, 97),
    /** Light Gray */ GRAY('7', "AAAAAA", true, true, 37),
    /** Dark Gray */ DARK_GRAY('8', "555555", true, true, 90),
    /** Black */ BLACK('0', "000000", true, true, 30),
    /** Minecoin Color */ MINECOIN_GOLD('g', "DDD605", false, true, null),
    /** Quartz Color */ MATERIAL_QUARTZ('h', "E3D4D1", false, true, null),
    /** Iron Color */ MATERIAL_IRON('i', "CECACA", false, true, null),
    /** Netherite Color */ MATERIAL_NETHERITE('j', "443A3B", false, true, null),
    /** Redstone Color */ MATERIAL_REDSTONE('m', "971607", false, true, null),
    /** Copper Color */ MATERIAL_COPPER('n', "B4684D", false, true, null),
    /** Gold Color */ MATERIAL_GOLD('p', "DEB12D", false, true, null),
    /** Emerald Color */ MATERIAL_EMERALD('q', "47A036", false, true, null),
    /** Diamond Color */ MATERIAL_DIAMOND('s', "2CBAA8", false, true, null),
    /** Lapislazuli Color */ MATERIAL_LAPIS('t', "21497B", false, true, null),
    /** Amethyst Color */ MATERIAL_AMETHYST('u', "9A5CC6", false, true, null);

    /** The <a href="https://minecraft.net/">Minecraft</a> color code */
    public final char code;

    /** The color's hexadecimal code */
    public final String hex;

    /** If the color can be used in <a href="https://www.minecraft.net/store/minecraft-java-bedrock-edition-pc">Minecraft: Java Edition</a> */
    public final boolean java;

    /** If the color can be used in <a href="https://www.minecraft.net/store/minecraft-java-bedrock-edition-pc">Minecraft: Bedrock Edition</a> */
    public final boolean bedrock;

    /** The ANSI escape code */
    public final Integer ansiCode;

    McFormat(char code, String hex, boolean java, boolean bedrock, Integer ansiCode) {
        this.code = code;
        this.hex = hex;
        this.java = java;
        this.bedrock = bedrock;
        this.ansiCode = ansiCode;
    }

    /**
     * Get the color as a <a href="https://minecraft.net/">Minecraft</a> chat color.
     * Similar to <a href="https://bukkit.org/">Bukkit</a>/<a href="https://www.spigotmc.org/wiki/bungeecord/">BungeeCord</a>'s ChatColor
     * @return § + the code, so you can use it in text.
     * @since 0.0.1
     */
    public @NotNull String chatColor() {
        return "§" + code;
    }

    /**
     * Get the color's equivalent {@link Color Java AWT color}.
     * @return Equivalent {@link Color Java AWT color}
     * @since 0.0.1
     */
    public @NotNull Color awtColor() {
        return Color.decode("#" + hex);
    }

    /**
     * Get the color's equivalent ANSI escape code.
     * @return Equivalent ANSI escape code
     * @since 0.0.1
     */
    public @NotNull String ansi() {
        return "\\e[0;" + ansiCode + "m";
    }

    /**
     * Get the color's equivalent console ANSI escape code that can be used to format the console.
     * @return Equivalent console ANSI escape code
     * @since 0.0.1
     */
    public @NotNull String consoleAnsi() {
        return "\u001B[" + ansiCode + "m";
    }

    /**
     * Does the same as {@link #chatColor()}, just there so you can just use {@link McFormat}.COLOR without the {@link #chatColor()}
     * @return § + the code, so you can use it in text
     * @since 0.0.1
     */
    @Override
    public @NotNull String toString() {
        return chatColor();
    }
}
