package com.marcpg.storing;

import org.jetbrains.annotations.NotNull;

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
     * A side of a pair
     * @since 0.0.1
     * @author MarcPG1905
     */
    public enum Side {
        /** The right side of a {@link Pair} */ RIGHT,
        /** The left side of a {@link Pair} */ LEFT
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
     * Set or change the left field of the pair.
     * @param l The new object for the left field.
     */
    public void setLeft(L l) {
        this.left = l;
    }

    /**
     * Get the left field of the pair.
     * @return The left field
     */
    public L left() {
        return this.left;
    }

    /**
     * Set or change the right field of the pair.
     * @param r The new object for the right field.
     */
    public void setRight(R r) {
        this.right = r;
    }

    /**
     * Get the right field of the pair.
     * @return The right field
     */
    public R right() {
        return this.right;
    }

    /**
     * Get the field of a specific side from the pair.
     * Not recommend, use {@link #left()} and {@link #right()} instead.
     * @param side What side of the pair you want to get.
     * @return The object that's on the side.
     */
    public Object get(Side side) {
        return side == Side.LEFT ? this.left : this.right;
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
        this.left = null;
        this.right = null;
    }

    /**
     * Clears the value of a specific side.
     * @param side The side to clear the value at.
     */
    public void clear(Side side) {
        if (side == Side.LEFT) this.left = null;
        else this.right = null;
    }

    /**
     * Check if the pair is empty.
     * This can also be checked by doing a null-check for the right and left side.
     * @return If the pair is empty.
     */
    public boolean isEmpty() {
        return this.left == null && this.right == null;
    }

    /**
     * Check if the pair is full.
     * This can also be checked by doing a null-check for the right and left side.
     * @return If the pair is full.
     */
    public boolean isFull() {
        return this.left != null && this.right != null;
    }

    /**
     * Checks if this pair contains the specified value.
     * @param o The object that's checked.
     * @return {@code true} if this pair contains the value, {@code false} otherwise.
     */
    public boolean contains(Object o) {
        return this.left == o || this.right == o;
    }

    /**
     * This gets the heavier side of the pair based on the length when using {@link Object#toString()}.
     * @return The heavier side of the pair.
     * @see #getHeavierObject()
     */
    public Side getHeavierSide() {
        return this.left.toString().length() > this.right.toString().length() ? Side.LEFT : Side.RIGHT;
    }

    /**
     * This gets the heavier side of the pair based on the length when using {@link Object#toString()}.
     * @return The heavier object of the pair.
     * @see #getHeavierSide()
     */
    public Object getHeavierObject() {
        return this.get(this.getHeavierSide());
    }

    /**
     * Runs some operation on both the left and right side.
     * @param operation The operation to run.
     * @since 0.0.5
     */
    public void both(@NotNull Consumer<Object> operation) {
        operation.accept(this.left);
        operation.accept(this.right);
    }

    /**
     * Gets the side matching the predicate's requirements. <br>
     * If both operations match the requirement, it will return the left side.
     * @return The side matching the requirements (prioritizing the left size).
     * @param operation What to check for.
     * @since 0.0.5
     */
    public Object getIf(@NotNull Predicate<Object> operation) {
        if (operation.test(this.left)) return this.left;
        if (operation.test(this.right)) return this.right;
        return null;
    }

    /**
     * Convert the Pair to a readable {@link String}. <br>
     * It uses the simple scheme: `{"right":[{@link #right() right field}], "left":[{@link #left() left field}]}`.
     * @return The pair in a {@link String} format.
     */
    @Override
    public String toString() {
        return "{\"left\":" + this.left.toString() + ",\"right\":" + this.right.toString() + "}";
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
}
