package com.marcpg.data.storage;

import org.jetbrains.annotations.NotNull;

/**
 * ByteData is used to convert and store data. <br>
 * Sources: <br>
 * - <a href="https://www.ibm.com/docs/en/storage-insights?topic=overview-units-measurement-storage-data">IBM - Units of Measurement for Storage Data</a> <br>
 * - <a href="https://en.wikipedia.org/wiki/Units_of_information">Wikipedia - Units of information</a> <br>
 * - <a href="http://wiki.webperfect.ch/index.php?title=Data_Units">WebPerfect - Data Units</a>
 * @since 0.0.1
 * @author MarcPG1905
 */
public class ByteData {
    private final long bytes;

    /**
     * Create a {@link ByteData new ByteData} object.
     * @param bytes The {@link ByteData data} in {@link Unit#BYTE bytes}.
     */
    public ByteData(long bytes) {
        this.bytes = bytes;
    }

    /**
     * Create a {@link ByteData new ByteData} object.
     * @param data The {@link ByteData data} value.
     * @param unit The {@link Unit unit} of the {@link ByteData time} value.
     */
    public ByteData(long data, @NotNull Unit unit) {
        this.bytes = data * unit.bytes;
    }

    /**
     * Get the {@link ByteData byte-data} as a formatted {@link String string} that is readable.
     * The formatted {@link ByteData byte-data} uses the highest {@link Unit unit} possible for
     * the best readability.
     * @param format The format to use.
     * @return The {@link ByteData byte-data} as a formatted {@link String string}.
     * @see #oneUnitFormat(long, Unit.Format)
     */
    public String getOneUnitFormatted(Unit.Format format) {
        return oneUnitFormat(this, format);
    }

    /**
     * Get the {@link ByteData byte-data} as a formatted {@link String string} that is readable.
     * The formatted {@link ByteData byte-data} uses the highest {@link Unit unit} possible and
     * then lower ones for remaining time.
     * @param format The format to use.
     * @return The {@link ByteData byte-data} as a formatted {@link String string}.
     * @see #preciselyFormat(ByteData, Unit.Format)
     */
    public String getPreciselyFormatted(Unit.Format format) {
        return preciselyFormat(this, format);
    }

    /**
     * Get the {@link ByteData byte-data} in a specific {@link Unit unit}.
     * @param unit The {@link Unit unit} in which the {@link ByteData byte-data} should be converted.
     * @return The converted {@link ByteData byte-data}.
     */
    public long getAs(@NotNull Unit unit) {
        return bytes / unit.bytes;
    }

    /**
     * Get the {@link ByteData byte-data} in a specific {@link Unit unit} as a {@link Double double}
     * for exact precision.
     * @param unit The {@link Unit unit} in which the {@link ByteData byte-data} should be converted.
     * @return The converted {@link ByteData byte-data}.
     */
    public double getAsExact(@NotNull Unit unit) {
        return (double) bytes / unit.bytes;
    }



    /**
     * Format the {@link ByteData byte-data} as a {@link String string} representation using
     * the highest possible {@link Unit unit}.
     * @param b The data in {@link Unit#BYTE byte}.
     * @param format The format to use.
     * @return The formatted {@link ByteData byte-data} {@link String string}.
     */
    public static @NotNull String oneUnitFormat(long b, Unit.Format format) {
        Unit[] units = Unit.values();

        for (int i = units.length; i >= 1; i--) {
            Unit unit = units[i - 1];
            if (b >= unit.bytes && (unit.format == Unit.Format.BOTH || unit.format == format))
                return (b / unit.bytes) + unit.abb;
        }

        return b + Unit.BYTE.abb;
    }

    /**
     * Format the {@link ByteData byte-data} as a {@link String string} representation using
     * the highest possible {@link Unit unit}.
     * @param byteData The data.
     * @param format The format to use.
     * @return The formatted {@link ByteData byte-data} {@link String string}.
     */
    public static @NotNull String oneUnitFormat(@NotNull ByteData byteData, Unit.Format format) {
        return oneUnitFormat(byteData.bytes, format);
    }

    /**
     * Format the {@link ByteData byte-data} as a {@link String string} representation using
     * the highest possible {@link Unit unit} and then lower ones for remaining data.
     * @param b The data in {@link Unit#BYTE byte}.
     * @param format The format to use.
     * @return The formatted {@link ByteData byte-data} {@link String string}.
     */
    public static @NotNull String preciselyFormat(long b, Unit.Format format) {
        Unit[] units = Unit.values();
        StringBuilder result = new StringBuilder();

        for (int i = units.length; i >= 1; i--) {
            Unit unit = units[i - 1];
            if (b >= unit.bytes && (unit.format == Unit.Format.BOTH || unit.format == format)) {
                int quotient = (int) (b / unit.bytes);
                b %= unit.bytes;

                if (!result.isEmpty()) result.append(" ");

                result.append(quotient).append(unit.abb);
            }
        }

        if (b > 0) {
            if (!result.isEmpty()) result.append(" ");
            result.append(b).append("B");
        }

        if (!result.isEmpty())
            return result.toString();
        else return "0B";
    }

    /**
     * Format the {@link ByteData byte-data} as a {@link String string} representation using
     * the highest possible {@link Unit unit} and then lower ones for remaining data.
     * @param byteData The data.
     * @param format The format to use.
     * @return The formatted {@link ByteData byte-data} {@link String string}.
     */
    public static @NotNull String preciselyFormat(@NotNull ByteData byteData, Unit.Format format) {
        return preciselyFormat(byteData.bytes, format);
    }

    /**
     * A data unit in the <a href="https://en.wikipedia.org/wiki/Byte">Byte</a> Format,
     * such as {@link Unit#MEGABYTE megabytes} or {@link Unit#GIGABYTE gigabytes} <br>
     * @since 0.0.1
     */
    public enum Unit {
        /** One byte - 8 bit */ BYTE(1L, "B", Format.BOTH),
        /** One kilobyte - 1000 byte */ KILOBYTE(1_000L, "kB", Format.DECIMAL),
        /** One megabyte - 1000 kilobyte */ MEGABYTE(1_000_000L, "MB", Format.DECIMAL),
        /** One gigabyte - 1000 megabyte */ GIGABYTE(1_000_000_000L, "GB", Format.DECIMAL),
        /** One terabyte - 1000 gigabyte */ TERABYTE(1_000_000_000_000L, "TB", Format.DECIMAL),
        /** One petabyte - 1000 terabyte */ PETABYTE(1_000_000_000_000_000L, "PB", Format.DECIMAL),
        /** One exabyte - 1000 petabyte */ EXABYTE(1_000_000_000_000_000_000L, "EB", Format.DECIMAL),
        /** One kibibyte - 1024 byte */ KIBIBYTE(1_024L, "KiB", Format.BINARY),
        /** One mebibyte - 1024 kibibyte */ MEBIBYTE(1_048_576L, "MiB", Format.BINARY),
        /** One gibibyte - 1024 mebibyte */ GIBIBYTE(1_073_741_824L, "GiB", Format.BINARY),
        /** One tebibyte - 1024 gibibyte */ TEBIBYTE(1_099_511_627_776L, "TiB", Format.BINARY),
        /** One pebibyte - 1024 tebibyte */ PEBIBYTE(1_125_899_906_842_624L, "PiB", Format.BINARY),
        /** One exbibyte - 1024 pebibyte */ EXBIBYTE(1_152_921_504_606_846_976L, "EiB", Format.BINARY);

        /** The number of {@link #BYTE bytes} in one {@link Unit unit}. */
        public final long bytes;

        /** The {@link Unit unit}'s abbreviation. */
        public final String abb;

        /** The unit's format */
        public final Format format;

        Unit(long bytes, String abbreviation, Format format) {
            this.bytes = bytes;
            this.abb = abbreviation;
            this.format = format;
        }

        /**
         * The full english name of the {@link Unit unit} as listed in the english dictionary. <br>
         * Same as {@link #pluralEng()}.
         * @return The english name.
         */
        public @NotNull String eng() {
            return name().toLowerCase();
        }

        /**
         * Same as {@link #eng()}, but with an "s" at the end. <br>
         * @return The english name in plural.
         */
        public @NotNull String pluralEng() {
            return name().toLowerCase() + "s";
        }

        /**
         * Just the same as {@link #eng()} and {@link #pluralEng()}.
         */
        @Override
        public @NotNull String toString() {
            return eng();
        }


        /** The format of data, so ^2 or ^10. */
        public enum Format {
            /** Binary numbers, so ^2 */ BINARY,
            /** Decimal numbers, so ^10 */ DECIMAL,
            /** No specific format, so both ^2 and ^10 */ BOTH
        }
    }
}
