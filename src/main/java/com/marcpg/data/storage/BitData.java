package com.marcpg.data.storage;

/**
 * ByteData is used to convert and store data. <br>
 * Sources: <br>
 * - <a href="https://www.ibm.com/docs/en/storage-insights?topic=overview-units-measurement-storage-data">IBM / Units of Measurement for Storage Data</a> <br>
 * - <a href="https://en.wikipedia.org/wiki/Units_of_information">Wikipedia / Units of information</a> <br>
 * - <a href="http://wiki.webperfect.ch/index.php?title=Data_Units">WebPerfect / Data Units</a>
 * @since 0.0.1
 * @author MarcPG1905
 */
public class BitData {
    private final long bits;

    /**
     * Create a {@link BitData new BitData} object.
     * @param bits The {@link BitData data} in {@link Unit#BIT bits}.
     * @since 0.0.1
     */
    public BitData(long bits) {
        this.bits = bits;
    }

    /**
     * Create a {@link BitData new BitData} object.
     * @param data The {@link BitData data} value.
     * @param unit The {@link Unit unit} of the {@link BitData time} value.
     * @since 0.0.1
     */
    public BitData(long data, Unit unit) {
        bits = data * unit.bits;
    }

    /**
     * Get the {@link BitData bit-data} as a formatted {@link String string} that is readable.
     * The formatted {@link BitData bit-data} uses the highest {@link Unit unit} possible for the best readability.
     * @param format The format to use.
     * @return The {@link BitData bit-data} as a formatted {@link String string}.
     * @see #oneUnitFormat(long, Unit.Format)
     * @since 0.0.1
     */
    public String getOneUnitFormatted(Unit.Format format) {
        return oneUnitFormat(this, format);
    }

    /**
     * Get the {@link BitData bit-data} as a formatted {@link String string} that is readable.
     * The formatted {@link BitData bit-data} uses the highest {@link Unit unit} possible and then lower ones for remaining time.
     * @param format The format to use.
     * @return The {@link BitData bit-data} as a formatted {@link String string}.
     * @see #preciselyFormat(BitData, Unit.Format)
     * @since 0.0.1
     */
    public String getPreciselyFormatted(Unit.Format format) {
        return preciselyFormat(this, format);
    }

    /**
     * Get the {@link BitData bit-data} in a specific {@link Unit unit}.
     * @param unit The {@link Unit unit} in which the {@link BitData bit-data} should be converted.
     * @return The converted {@link BitData bit-data}.
     * @since 0.0.1
     */
    public long getAs(Unit unit) {
        return bits / unit.bits;
    }

    /**
     * Get the {@link BitData bit-data} in a specific {@link Unit unit} as a {@link Double double} for exact precision.
     * @param unit The {@link Unit unit} in which the {@link BitData bit-data} should be converted.
     * @return The converted {@link BitData bit-data}.
     * @since 0.0.1
     */
    public double getAsExact(Unit unit) {
        return (double) bits / unit.bits;
    }



    /**
     * Format the {@link BitData bit-data} as a {@link String string} representation using the highest possible {@link Unit unit}.
     * @param b The data in {@link Unit#BIT bits}.
     * @param format The format to use.
     * @return The formatted {@link BitData bit-data} {@link String string}.
     * @since 0.0.1
     */
    public static String oneUnitFormat(long b, Unit.Format format) {
        Unit[] units = Unit.values();

        for (int i = units.length; i >= 1; i--) {
            Unit unit = units[i - 1];
            if (b >= unit.bits && (unit.format == Unit.Format.BOTH || unit.format == format))
                return (b / unit.bits) + unit.abb;
        }

        return b + Unit.BIT.abb;
    }

    /**
     * Format the {@link BitData bit-data} as a {@link String string} representation using the highest possible {@link Unit unit}.
     * @param bitData The data.
     * @param format The format to use.
     * @return The formatted {@link BitData bit-data} {@link String string}.
     * @since 0.0.1
     */
    public static String oneUnitFormat(BitData bitData, Unit.Format format) {
        return oneUnitFormat(bitData.bits, format);
    }

    /**
     * Format the {@link BitData bit-data} as a {@link String string} representation using the highest possible {@link Unit unit} and then lower ones for remaining data.
     * @param b The data in {@link Unit#BIT bit}.
     * @param format The format to use.
     * @return The formatted {@link BitData bit-data} {@link String string}.
     * @since 0.0.1
     */
    public static String preciselyFormat(long b, Unit.Format format) {
        Unit[] units = Unit.values();
        StringBuilder result = new StringBuilder();

        for (int i = units.length; i >= 1; i--) {
            Unit unit = units[i - 1];
            if (b >= unit.bits && (unit.format == Unit.Format.BOTH || unit.format == format)) {
                int quotient = (int) (b / unit.bits);
                b %= unit.bits;

                if (!result.isEmpty()) result.append(" ");

                result.append(quotient).append(unit.abb);
            }
        }

        if (b > 0) {
            if (!result.isEmpty()) result.append(" ");
            result.append(b).append("b");
        }

        if (!result.isEmpty())
            return result.toString();
        else return "0b";
    }

    /**
     * Format the {@link BitData bit-data} as a {@link String string} representation using the highest possible {@link Unit unit} and then lower ones for remaining data.
     * @param bitData The data.
     * @param format The format to use.
     * @return The formatted {@link BitData bit-data} {@link String string}.
     * @since 0.0.1
     */
    public static String preciselyFormat(BitData bitData, Unit.Format format) {
        return preciselyFormat(bitData.bits, format);
    }



    /**
     * A data unit in the <a href="https://en.wikipedia.org/wiki/Bit">Bit</a> Format,
     * such as {@link Unit#MEGABIT megabit} or {@link Unit#GIGABIT gigabit} <br>
     * @since 0.0.1
     */
    public enum Unit {
        /** One bit - on or off */ BIT(1L, "b", Format.BOTH),
        /** One kilobit - 1000 bit */ KILOBIT(1_000L, "kbit", Format.DECIMAL),
        /** One megabit - 1000 kilobit */ MEGABIT(1_000_000L, "Mbit", Format.DECIMAL),
        /** One gigabit - 1000 megabit */ GIGABIT(1_000_000_000L, "Gbit", Format.DECIMAL),
        /** One terabit - 1000 gigabit */ TERABIT(1_000_000_000_000L, "Tbit", Format.DECIMAL),
        /** One petabit - 1000 terabit */ PETABIT(1_000_000_000_000_000L, "Pbit", Format.DECIMAL),
        /** One exabit - 1000 petabit */ EXABIT(1_000_000_000_000_000_000L, "Ebit", Format.DECIMAL),
        /** One kibibit - 1024 bit */ KIBIBYTE(1_024L, "Kibit", Format.BINARY),
        /** One mebibit - 1024 kibibit */ MEBIBYTE(1_048_576L, "Mibit", Format.BINARY),
        /** One gibibit - 1024 mebibit */ GIBIBYTE(1_073_741_824L, "Gibit", Format.BINARY),
        /** One tebibit - 1024 gibibit */ TEBIBYTE(1_099_511_627_776L, "Tibit", Format.BINARY),
        /** One pebibit - 1024 tebibit */ PEBIBYTE(1_125_899_906_842_624L, "Pibit", Format.BINARY),
        /** One exbibit - 1024 pebibit */ EXBIBYTE(1_152_921_504_606_846_976L, "Eibit", Format.BINARY);

        /**
         * The number of {@link #BIT bits} in one {@link Unit unit}.
         * @since 0.0.1
         */
        public final long bits;

        /**
         * The {@link Unit unit}'s abbreviation.
         * @since 0.0.1
         */
        public final String abb;

        /**
         * The unit's format
         */
        public final Format format;

        Unit(long bits, String abbreviation, Format format) {
            abb = abbreviation;
            this.bits = bits;
            this.format = format;
        }

        /**
         * The full english name of the {@link Unit unit} as listed in the english dictionary. <br>
         * Same as {@link #pluralEng()}.
         * @return The english name.
         * @since 0.0.1
         */
        public String eng() {
            return this.name().toLowerCase();
        }

        /**
         * Same as {@link #eng()}. <br>
         * @return The english name.
         * @since 0.0.1
         */
        public String pluralEng() {
            return this.name().toLowerCase();
        }

        /**
         * Just the same as {@link #eng()} and {@link #pluralEng()}.
         * @since 0.0.1
         */
        public String toString() {
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
