package com.marcpg.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Used to get random values from things
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Randomizer {
    private Randomizer() {
        throw new AssertionError("Instantiating static-only utility class.");
    }

    /** The current {@link Random java.util.Random} that's being used. */
    private static Random random = new Random();

    /**
     * All basic {@link Character characters} that are on a keyboard and supported in most fonts and formats.
     * @since 0.0.1
     */
    public static final String BASIC_CHARS = "!#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    /**
     * Returns a random {@link Boolean boolean} value based on a fraction represented by the numerator and denominator.
     * The numerator represents the desired outcomes, while the denominator represents the total possibilities.
     * @param numerator The number of desired outcomes.
     * @param denominator The total number of possibilities.
     * @return Returns true or false randomly based on the fraction represented by the numerator and denominator.
     * @since 0.0.1
     */
    public static boolean boolByChance(int numerator, int denominator) {
        return random.nextInt(denominator + 1) < numerator;
    }

    /**
     * Returns a random {@link Boolean boolean} value based on a percentage.
     * A 1% chance would almost always return false.
     * A 99% chance would almost always return true.
     * @param percentage The chance in percentage (0-100%) to return true.
     * @return Returns true or false randomly based on the input percentage.
     * @since 0.0.1
     */
    public static boolean boolByChance(double percentage) {
        return random.nextDouble() < percentage / 100.0;
    }

    /**
     * Returns a random {@link Object object} from the given {@link Collection collection}.
     *
     * @param <T> The type of elements in the list.
     * @param in The {@link Collection collection} from which a random {@link Object object} should be selected.
     * @return A random {@link T thing} from the input {@link Collection collection}.
     * @since 0.0.1
     */
    public static <T> T fromCollection(Collection<T> in) {
        List<T> list = new ArrayList<>(in);
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns a random {@link Object object} from the given array.
     *
     * @param <T> The type of elements in the array.
     * @param in The array from which a random {@link Object object} should be selected.
     * @return A random {@link Object object} from the input array.
     * @since 0.0.1
     */
    public static <T> T fromArray(T @NotNull [] in) {
        return in[random.nextInt(in.length)];
    }

    /**
     * Generates a random {@link String String} based on available {@link Character characters} and a length.
     * @param length The length of the randomly generated  {@link String String}
     * @param charset What {@link Character characters} can be used in the randomly generated {@link String String}
     * @return A randomly generated {@link String String}
     * @since 0.0.1
     */
    public static @NotNull String generateRandomString(Integer length, String charset) {
        StringBuilder builder = new StringBuilder(length);

        for(int i = 0; i < length; ++i) {
            int randomIndex = random.nextInt(charset.length());
            char randomChar = charset.charAt(randomIndex);
            builder.append(randomChar);
        }

        return builder.toString();
    }

    /**
     * Generates a random {@link String String} based on {@link #BASIC_CHARS basic characters} and a length.
     * @param length The length of the randomly generated {@link String String}
     * @return A randomly generated {@link String String}
     * @since 0.0.1
     */
    public static @NotNull String generateRandomString(int length) {
        return generateRandomString(length, BASIC_CHARS);
    }

    /**
     * Picks a random {@link Character character} out of a {@link String String}
     * @param charset All available {@link Character characters}, can have one {@link Character character} multiple times.
     * @return A random {@link Character character}
     * @since 0.0.1
     */
    public static char randomChar(@NotNull String charset) {
        return charset.charAt(random.nextInt(charset.length()));
    }

    /**
     * Picks a random {@link Character character} out of {@link #BASIC_CHARS basic characters}
     * @return A random {@link Character character}
     * @since 0.0.1
     */
    public static char randomChar() {
        return BASIC_CHARS.charAt(random.nextInt(BASIC_CHARS.length()));
    }

    /**
     * Resets the random to get true randomness
     * @since 0.0.1
     */
    public static void newRandom() {
        random = new Random();
    }
}
