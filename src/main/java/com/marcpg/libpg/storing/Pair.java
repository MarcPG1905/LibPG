package com.marcpg.libpg.storing;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A pair is just two values that have a defined type.
 * It's the same as a {@link java.util.Map}, but with only one value.
 * @param <L> The left field's type
 * @param <R> The right field's type
 * @since 0.0.1
 * @author MarcPG1905
 */
public class Pair<L, R> {
    /**
     * A side of a pair.
     * @since 0.0.1
     * @author MarcPG1905
     */
    public enum Side {
        /** The right side of a {@link Pair}. */ RIGHT,
        /** The left side of a {@link Pair}. */ LEFT
    }

    private L left;
    private R right;

    /**
     * Construct a pair with both sides already initialized.
     * @param left  The value for the left field.
     * @param right The value for the right field.
     * @since 0.0.4
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Construct a pair.
     * @since 0.0.8
     */
    public Pair() {}

    /**
     * Set or change the left field of the pair.
     * @param l The new object for the left field.
     */
    public void setLeft(L l) {
        left = l;
    }

    /**
     * Get the left field of the pair.
     * @return The left field.
     */
    public L left() {
        return left;
    }

    /**
     * Set or change the right field of the pair.
     * @param r The new object for the right field.
     */
    public void setRight(R r) {
        right = r;
    }

    /**
     * Get the right field of the pair.
     * @return The right field.
     */
    public R right() {
        return right;
    }

    /**
     * Get the field of a specific side from the pair.
     * Not recommend, use {@link #left()} and {@link #right()} instead.
     * @param side What side of the pair you want to get.
     * @return The object that's on the side.
     */
    public Object get(Side side) {
        return side == Side.LEFT ? left : right;
    }

    /**
     * Set or change both fields of the pair.
     * @param right The new object for the left field.
     * @param left The new object for the right field.
     */
    public void set(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Clears both the left and right side's values.
     */
    public void clear() {
        left = null;
        right = null;
    }

    /**
     * Clears the value of a specific side.
     * @param side The side to clear the value at.
     */
    public void clear(Side side) {
        if (side == Side.LEFT) left = null;
        else right = null;
    }

    /**
     * Check if the pair is empty.
     * This can also be checked by doing a null-check for the right and left side.
     * @return If the pair is empty.
     */
    public boolean isEmpty() {
        return left == null && right == null;
    }

    /**
     * Check if the pair is full.
     * This can also be checked by doing a null-check for the right and left side.
     * @return If the pair is full.
     */
    public boolean isFull() {
        return left != null && right != null;
    }

    /**
     * Checks if this pair contains the specified value.
     * @param o The object that's checked.
     * @return {@code true} if this pair contains the value, {@code false} otherwise.
     */
    public boolean contains(Object o) {
        return left == o || right == o;
    }

    /**
     * This gets the heavier side of the pair based on the length when using {@link Object#toString()}.
     * @return The heavier side of the pair.
     * @see #getHeavierObject()
     */
    public Side getHeavierSide() {
        return left.toString().length() > right.toString().length() ? Side.LEFT : Side.RIGHT;
    }

    /**
     * This gets the heavier side of the pair based on the length when using {@link Object#toString()}.
     * @return The heavier object of the pair.
     * @see #getHeavierSide()
     */
    public Object getHeavierObject() {
        return get(getHeavierSide());
    }

    /**
     * Runs some operation on both the left and right side.
     * @param operation The operation to run.
     * @since 0.0.5
     */
    public void both(@NotNull Consumer<Object> operation) {
        operation.accept(left);
        operation.accept(right);
    }

    /**
     * Gets the side matching the predicate's requirements. <br>
     * If both operations match the requirement, it will return the left side.
     * @return The side matching the requirements (prioritizing the left size).
     * @param operation What to check for.
     * @since 0.0.5
     */
    public Object getIf(@NotNull Predicate<Object> operation) {
        if (operation.test(left)) return left;
        if (operation.test(right)) return right;
        return null;
    }

    /**
     * Convert the Pair to a readable {@link String}. <br>
     * It uses the simple scheme: `{"right":[{@link #right() right field}], "left":[{@link #left() left field}]}`.
     * @return The pair in a {@link String} format.
     */
    @Override
    public String toString() {
        return "{\"left\":" + left.toString() + ",\"right\":" + right.toString() + "}";
    }

    /**
     * Static method to create a new Pair instance.
     * @param left The value for the left field.
     * @param right The value for the right field.
     * @param <L> The left value's type.
     * @param <R> The right value's type.
     * @return A new Pair instance with the set values.
     * @since 0.0.4
     */
    public static <L, R> @NotNull Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    /**
     * Static method to convert a {@link java.util.Map.Entry Map.Entry} to a Pair, where the left side is
     * the key and the right side is the value.
     * @param entry The Map.Entry to convert to a Pair.
     * @param <L> The left value's, so map-entry's key, type.
     * @param <R> The right value's, so map-entry's value, type.
     * @return A new Pair instance with the same objects as the Entry.
     */
    public static <L, R> @NotNull Pair<L, R> ofEntry(@NotNull Map.Entry<L, R> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }
}
