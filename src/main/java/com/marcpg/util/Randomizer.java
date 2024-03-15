package com.marcpg.util;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Used to get random values from things
 * @since 0.0.1
 * @author MarcPG1905
 */
public final class Randomizer {
    private Randomizer() {
        throw new AssertionError("Instantiating static-only utility class.");
    }

    /** The current {@link Random java.util.Random} that's being used. */
    private static Random random = new Random();

    /** All basic characters that are on a keyboard and supported in most fonts and formats. */
    public static final String BASIC_CHARS = "!#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    /**
     * Returns a random boolean value based on a fraction represented by the numerator and denominator.
     * The numerator represents the desired outcomes, while the denominator represents the total possibilities.
     * @param numerator The number of desired outcomes.
     * @param denominator The total number of possibilities.
     * @return Returns true or false randomly based on the fraction represented by the numerator and denominator.
     */
    public static boolean boolByChance(int numerator, int denominator) {
        return random.nextInt(denominator + 1) < numerator;
    }

    /**
     * Returns a random boolean value based on a percentage.
     * A 1% chance would almost always return false.
     * A 99% chance would almost always return true.
     * @param percentage The chance in percentage (0-100%) to return true.
     * @return Returns true or false randomly based on the input percentage.
     */
    public static boolean boolByChance(double percentage) {
        return random.nextDouble() < percentage / 100.0;
    }

    /**
     * Returns a random object from the given {@link Collection collection}.
     * @param <T> The type of elements in the list.
     * @param in The {@link Collection collection} from which a random object should be selected.
     * @return A random {@link T thing} from the input {@link Collection collection}.
     */
    public static <T> T fromCollection(Collection<T> in) {
        List<T> list = new ArrayList<>(in);
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns a random object from the given array.
     * @param <T> The type of elements in the array.
     * @param in The array from which a random object should be selected.
     * @return A random object from the input array.
     */
    public static <T> T fromArray(T @NotNull [] in) {
        return in[random.nextInt(in.length)];
    }

    /**
     * Generates a random string based on available characters and a length.
     * @param length The length of the string.
     * @param charset What characters can be used in the string.
     * @return A randomly generated string.
     */
    public static @NotNull String generateRandomString(Integer length, String charset) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            builder.append(charset.charAt(random.nextInt(charset.length())));
        }
        return builder.toString();
    }

    /**
     * Generates a random string based on {@link #BASIC_CHARS basic characters} and a length.
     * @param length The length of the randomly generated string.
     * @return A randomly generated string.
     */
    public static @NotNull String generateRandomString(int length) {
        return generateRandomString(length, BASIC_CHARS);
    }

    /**
     * Generates a random password based on available characters and a length.
     * What makes this different from {@link #generateRandomString(int)} is the usage of {@link SecureRandom}.
     * @param length The length of the password.
     * @param charset What characters can be used in the password.
     * @return A randomly generated password.
     */
    public static @NotNull String generateRandomPassword(Integer length, String charset) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            builder.append(charset.charAt(secureRandom.nextInt(charset.length())));
        }
        return builder.toString();
    }

    /**
     * Generates a random password based on {@link #BASIC_CHARS basic characters} and a length.
     * What makes this different from {@link #generateRandomString(int)} is the usage of {@link SecureRandom}.
     * @param length The length of the password.
     * @return A randomly generated password.
     */
    public static @NotNull String generateRandomPassword(int length) {
        return generateRandomPassword(length, BASIC_CHARS);
    }

    /**
     * Picks a random character out of a string.
     * @param charset All available characters, can have one character multiple times.
     * @return A random character.
     */
    public static char randomChar(@NotNull String charset) {
        return charset.charAt(random.nextInt(charset.length()));
    }

    /**
     * Picks a random character out of {@link #BASIC_CHARS basic characters}.
     * @return A random character.
     */
    public static char randomChar() {
        return BASIC_CHARS.charAt(random.nextInt(BASIC_CHARS.length()));
    }

    /** Resets the random to get true randomness. */
    public static void newRandom() {
        random = new Random();
    }
}
