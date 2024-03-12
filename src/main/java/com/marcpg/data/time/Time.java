package com.marcpg.data.time;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Time is used to convert and store times with different {@link Unit}s, etc.
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Time {
    private long seconds;
    private boolean allowNegatives = false;

    /**
     * Create a {@link Time new Time} object.
     * @param seconds The {@link Time time} in {@link Unit#SECONDS seconds}.
     * @since 0.0.1
     */
    public Time(long seconds) {
        this.seconds = seconds;
    }

    /**
     * Create a {@link Time new Time} object.
     * @param time The {@link Time time} value.
     * @param unit The {@link Unit unit} of the {@link Time time} value.
     * @since 0.0.1
     */
    public Time(long time, @NotNull Unit unit) {
        this.seconds = time * unit.sec;
    }

    /**
     * Get the {@link Time time} as a formatted {@link String string} that is readable.
     * The formatted {@link Time time} uses the highest {@link Unit} possible for the best readability.
     * @return The {@link Time time} as a formatted {@link String string}.
     * @see #oneUnitFormat(long)
     * @since 0.0.1
     */
    public String getOneUnitFormatted() {
        return oneUnitFormat(this);
    }

    /**
     * Get the {@link Time time} as a formatted {@link String string} that is readable.
     * The formatted {@link Time time} uses the highest {@link Unit} possible and then lower ones for remaining time.
     * @return The {@link Time time} as a formatted {@link String string}.
     * @see #preciselyFormat(Time)
     * @since 0.0.1
     */
    public String getPreciselyFormatted() {
        return preciselyFormat(this);
    }


    /**
     * Get the {@link Time time} in the default {@link Unit#SECONDS seconds} format.
     * @return The {@link Time time} in {@link Unit#SECONDS seconds}.
     * @since 0.0.2
     */
    public long get() {
        return this.seconds;
    }

    /**
     * Get the {@link Time time} in a specific {@link Unit unit}.
     * @param unit The {@link Unit unit} in which the {@link Time time} should be converted.
     * @return The converted {@link Time time}.
     * @since 0.0.1
     */
    public long getAs(@NotNull Unit unit) {
        return this.seconds / unit.sec;
    }

    /**
     * Get the {@link Time time} in a specific {@link Unit unit} as a {@link Double double} for exact precision.
     * @param unit The {@link Unit unit} in which the {@link Time time} should be converted.
     * @return The converted {@link Time time}.
     * @since 0.0.1
     */
    public double getAsExact(@NotNull Unit unit) {
        return (double) this.seconds / unit.sec;
    }

    /**
     * Increment the {@link Time time}, useful if used in {@link Timer timers}.
     * @param time The {@link Time time} in the {@link Unit unit} of the second parameter.
     * @param unit The {@link Unit unit} of the {@link Time time}.
     * @since 0.0.1
     */
    public void increment(long time, @NotNull Unit unit) {
        this.seconds += time * unit.sec;
    }

    /**
     * Increment the {@link Time time}, useful if used in {@link Timer timers}.
     * @param seconds The {@link Time time} in {@link Unit#SECONDS seconds}
     * @since 0.0.1
     */
    public void increment(long seconds) {
        this.seconds += seconds;
    }

    /**
     * Increment the {@link Time time}, useful if used in {@link Timer timers}. <br>
     * This increments the {@link Time time} by 1 {@link Unit#SECONDS second}.
     * @since 0.0.1
     */
    public void increment() {
        this.seconds++;
    }

    /**
     * Decrement the {@link Time time}, useful if used in {@link Timer timers}/countdowns.
     * @param time The {@link Time time} in the {@link Unit unit} of the second parameter.
     * @param unit The {@link Unit unit} of the {@link Time time}.
     * @since 0.0.1
     */
    public void decrement(long time, @NotNull Unit unit) {
        this.decrement(time * unit.sec);
    }

    /**
     * Decrement the {@link Time time}, useful if used in {@link Timer timers}/countdowns.
     * @param seconds The {@link Time time} in {@link Unit#SECONDS seconds}
     * @since 0.0.1
     */
    public void decrement(long seconds) {
        this.seconds -= seconds;
        if (!this.allowNegatives && this.seconds < 0) this.seconds = 0;
    }

    /**
     * Decrement the {@link Time time}, useful if used in {@link Timer timers}/countdowns. <br>
     * This decrements the {@link Time time} by 1 {@link Unit#SECONDS second}.
     * @since 0.0.1
     */
    public void decrement() {
        this.seconds--;
        if (!this.allowNegatives && this.seconds < 0) this.seconds = 0;
    }

    /**
     * Changes whether the time allows negative values or not.
     * @param allowNegatives If the time allows negative values.
     * @since 0.0.4
     */
    public void setAllowNegatives(boolean allowNegatives) {
        this.allowNegatives = allowNegatives;
    }

    /**
     * Format the {@link Time time} as a {@link String string} representation using the highest possible {@link Unit unit}.
     * @param s The time in {@link Unit#SECONDS seconds}.
     * @return The formatted {@link Time time} {@link String string}.
     * @since 0.0.1
     */
    public static @NotNull String oneUnitFormat(long s) {
        Unit[] units = Unit.values();

        for (int i = units.length; i >= 1; i--) {
            Unit unit = units[i - 1];
            if (s >= unit.sec) return (s / unit.sec) + unit.abb;
        }

        return s + Unit.SECONDS.abb;
    }

    /**
     * Format the {@link Time time} as a {@link String string} representation using the highest possible {@link Unit unit}.
     * @param time The time.
     * @return The formatted {@link Time time} {@link String string}.
     * @since 0.0.1
     */
    public static @NotNull String oneUnitFormat(@NotNull Time time) {
        return oneUnitFormat(time.seconds);
    }

    /**
     * Format the {@link Time time} as a {@link String string} representation using the highest possible {@link Unit unit} and then lower ones for remaining time.
     * @param s The time in {@link Unit#SECONDS seconds}.
     * @return The formatted {@link Time time} {@link String string}.
     * @since 0.0.1
     */
    public static @NotNull String preciselyFormat(long s) {
        Unit[] units = Unit.values();
        StringBuilder result = new StringBuilder();

        for (int i = units.length; i >= 1; i--) {
            Unit unit = units[i - 1];
            if (s >= unit.sec) {
                int quotient = (int) (s / unit.sec);
                s %= unit.sec;

                if (!result.isEmpty()) result.append(" ");

                result.append(quotient).append(unit.abb);
            }
        }

        if (s > 0) {
            if (!result.isEmpty()) result.append(" ");
            result.append(s).append("s");
        }

        if (!result.isEmpty())
            return result.toString();
        else return "0s";
    }

    /**
     * Format the {@link Time time} as a {@link String string} representation using the highest possible {@link Unit unit} and then lower ones for remaining time.
     * @param time The time.
     * @return The formatted {@link Time time} {@link String string}.
     * @since 0.0.1
     */
    public static @NotNull String preciselyFormat(@NotNull Time time) {
        return preciselyFormat(time.seconds);
    }


    /**
     * Parse the {@link String input string} to a {@link Time time}. Useful for parsing text user input to a time object.
     * @param input String to parse, must follow the scheme: [time/int]+[abbreviation], so for example "1min", "3d", etc. Only one single unit is expected!
     * @return The parsed {@link Time time}.
     * @since 0.0.3
     */
    public static @NotNull Time parse(String input) {
        Matcher matcher = Pattern.compile("(\\d+)|([a-zA-Z]+)").matcher(input);

        String text = null;
        String number = null;

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                number = matcher.group(1);
            } else {
                text = matcher.group(2);
            }
        }

        if (text != null && number != null) {
            for (Time.Unit unit : Time.Unit.values()) {
                if (text.startsWith(unit.abb)) {
                    return new Time(Integer.parseInt(number), unit);
                }
            }
        }

        return new Time(0);
    }



    /**
     * A time unit, such as {@link Unit#SECONDS seconds}, {@link Unit#MINUTES minutes}, {@link Unit#HOURS hours}, etc.
     * @since 0.0.1
     */
    public enum Unit {
        /** One second - 1000 milliseconds */ SECONDS(1L, "s"),
        /** One minute - 60 seconds */ MINUTES(60L, "min"),
        /** One hour - 60 minutes / 3600 seconds */ HOURS(3600L, "hr"),
        /** One day - 24 hours */ DAYS(86400L, "d"),
        /** One week - 7 days */ WEEKS(604800L, "wk"),
        /** One month - ~4.34 weeks */ MONTHS(2629800L, "mo"),
        /** One year - 12 months / 365.25 days */ YEARS(31557600L, "yr"),
        /** One decade - 10 years */ DECADES(315576000L, "dec"),
        /** One century - 100 years / 10 decades */ CENTURIES(3155760000L, "cent");

        /**
         * The number of {@link #SECONDS seconds} in one {@link Unit unit}. (source: <a href="https://en.wikipedia.org/wiki/Unit_of_time">Wikipedia/Unit of time</a>)
         * @since 0.0.1
         */
        public final long sec;

        /**
         * The {@link Unit unit}'s abbreviation.
         * @since 0.0.1
         */
        public final String abb;

        Unit(long seconds, String abbreviation) {
            this.sec = seconds;
            this.abb = abbreviation;
        }

        /**
         * Get {@link #pluralEng()} but as singular. <br>
         * The full english name of the {@link Unit unit} as listed in the english dictionary.
         * @return The english name.
         * @since 0.0.1
         */
        public @NotNull String eng() {
            return this.pluralEng().substring(0, this.name().length() - 1).replace("ies", "y");
        }

        /**
         * Get {@link #eng()} but as plural. <br>
         * Most times just with an s, but changes for century = centuries
         * @return Plural form of {@link #eng}
         * @since 0.0.1
         */
        public @NotNull String pluralEng() {
            return this.name().toLowerCase();
        }

        /**
         * Just the same as {@link #eng()}.
         * @since 0.0.1
         */
        @Override
        public @NotNull String toString() {
            return this.eng();
        }
    }
}
