package com.marcpg.libpg.storing;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a simple coordinate with only X, Y, and Z values.
 * @param x The X value as a double.
 * @param y The Y value as a double.
 * @param z The Z value as a double.
 */
public record Cord(double x, double y, double z) implements Serializable {
    /**
     * Adds the input cord to this cord. Does not affect this object and creates a new one for the result.
     * @param cord The second addend of this addition.
     * @return The sum/total of this addition as a new Cord.
     */
    public @NotNull Cord add(@NotNull Cord cord) {
        return new Cord(x + cord.x, y + cord.y, z + cord.z);
    }

    /**
     * Subtracts the input cord from this cord. Does not affect this object and creates a new one for the result.
     * @param cord The subtrahend of this subtraction.
     * @return The difference of this subtraction as a new Cord.
     */
    public @NotNull Cord subtract(@NotNull Cord cord) {
        return new Cord(x - cord.x, y - cord.y, z - cord.z);
    }

    /**
     * Multiplies the input cord with this cord. Does not affect this object and creates a new one for the result.
     * @param cord The second factor of this multiplication.
     * @return The product of this multiplication as a new Cord.
     */
    public @NotNull Cord multiply(@NotNull Cord cord) {
        return new Cord(x * cord.x, y * cord.y, z * cord.z);
    }

    /**
     * Divides this cord by the input cord. Does not affect this object and creates a new one for the result.
     * @param cord The divisor of this division.
     * @return The quotient of this division as a new Cord.
     */
    public @NotNull Cord divide(@NotNull Cord cord) {
        return new Cord(x / cord.x, y / cord.y, z / cord.z);
    }

    /**
     * Calculates the distance between this cord and the input cord.
     * @param cord The other cord to get the distance to.
     * @return The distance in blocks.
     */
    public double distance(@NotNull Cord cord) {
        return Math.sqrt(Math.sqrt(x - cord.x) + Math.sqrt(y - cord.y) + Math.sqrt(z - cord.z));
    }

    /**
     * Swaps the x and z values of this coordinate. Does not affect this object and creates a new one for the result.
     * @return The swapped values as a new Cord.
     */
    public @NotNull Cord rotated() {
        return new Cord(z, y, x);
    }

    /**
     * Checks if this cord lays inside the specified bounds.
     * @param lowCord The bound's corner containing the lowest X, Y, and Z.
     * @param highCord The bound's corner containing the highest X, Y, and Z.
     * @return {@code true} if this {@link Cord} is inside the bounds, {@code false} otherwise.
     * @see #inBounds(double, double, double, double, double, double)
     */
    public boolean inBounds(@NotNull Cord lowCord, @NotNull Cord highCord) {
        return inBounds(lowCord.x, highCord.x, lowCord.y, highCord.y, lowCord.z, highCord.z);
    }

    /**
     * Checks if this {@link Cord} lays inside the specified bounds.
     * @param lowX The lowest X of the bounds.
     * @param highX The highest X of the bounds.
     * @param lowY The lowest Y of the bounds.
     * @param highY The highest Y of the bounds.
     * @param lowZ The lowest Z of the bounds.
     * @param highZ The highest Z of the bounds.
     * @return {@code true} if this {@link Cord} is inside the bounds, {@code false} otherwise.
     * @see #inBounds(Cord, Cord)
     */
    public boolean inBounds(double lowX, double highX, double lowY, double highY, double lowZ, double highZ) {
        return x >= lowX && x <= highX && y >= lowY && y <= highY && z >= lowZ && z <= highZ;
    }

    /**
     * Converts a list of three objects, X, Y, and Z, into a Cord.
     * @param list The list to convert.
     * @return The converted Cord.
     */
    public static @NotNull Cord ofList(@NotNull List<Integer> list) {
        return new Cord(list.get(0), list.get(1), list.get(2));
    }

    /**
     * Converts two corners, so one has the lowest x, y and z and one has the highest x, y and z.
     * @param corner1 The first corner.
     * @param corner2 The second corner.
     * @return A pair where the left side has the lower and the right the higher x, y and z. <br>
     *         {@code Pair<Lowest XYZ, Highest XYZ>}
     */
    public static @NotNull Pair<Cord, Cord> corners(@NotNull Cord corner1, @NotNull Cord corner2) {
        return new Pair<>(
                new Cord(Math.min(corner1.x(), corner2.x()), Math.min(corner1.y(), corner2.y()), Math.min(corner1.z(), corner2.z())),
                new Cord(Math.max(corner1.x(), corner2.x()), Math.max(corner1.y(), corner2.y()), Math.max(corner1.z(), corner2.z()))
        );
    }
}
