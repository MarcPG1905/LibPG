package com.marcpg.storing;

/**
 * A pair is just two values that have a defined type.
 * It's the same as a {@link java.util.Map}, but with only one value.
 * @param <L> The left field's type
 * @param <R> The right field's type
 * @author MarcPG1905
 * @since 0.0.1
 */
public class Pair<L, R> {
    /**
     * A side of a pair
     * @author MarcPG1905
     * @since 0.0.1
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
     * Set or change the left field of the pair
     * @param l The new object for the left field
     * @since 0.0.1
     */
    public void setLeft(L l) {
        left = l;
    }

    /**
     * Get the left field of the pair
     * @return The left field
     * @since 0.0.1
     */
    public L left() {
        return left;
    }

    /**
     * Set or change the right field of the pair
     * @param r The new object for the right field
     * @since 0.0.1
     */
    public void setRight(R r) {
        right = r;
    }

    /**
     * Get the right field of the pair
     * @return The right field
     * @since 0.0.1
     */
    public R right() {
        return right;
    }

    /**
     * Get the field of a specific side from the pair.
     * Not recommend, use {@link #left()} and {@link #right()} instead
     * @param side What side of the pair you want to get
     * @return The object that's on the side
     * @since 0.0.1
     */
    public Object get(Side side) {
        return side == Side.LEFT ? left : right;
    }

    /**
     * Set or change both fields of the pair
     * @param right The new object for the left field
     * @param left The new object for the right field
     * @since 0.0.1
     */
    public void set(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Clear all values.
     * @since 0.0.1
     */
    public void clear() {
        left = null;
        right = null;
    }

    /**
     * Clears the value of a specific side.
     * @param side The side to clear the value at.
     * @since 0.0.1
     */
    public void clear(Side side) {
        if (side == Side.LEFT) {
            left = null;
        } else {
            right = null;
        }
    }

    /**
     * Check if the pair is empty.
     * This can also be checked by doing a null-check for the right and left side.
     * @return If the pair is empty
     * @since 0.0.1
     */
    public boolean isEmpty() {
        return left == null && right == null;
    }

    /**
     * Check if the pair is full.
     * This can also be checked by doing a null-check for the right and left side.
     * @return If the pair is full
     * @since 0.0.1
     */
    public boolean isFull() {
        return left != null && right != null;
    }

    /**
     * This gets the heavier side of the pair based on the length when using {@link Object#toString()}.
     * @return The heavier side of the pair
     * @since 0.0.1
     * @see #getHeavierObject()
     */
    public Side getHeavierSide() {
        return left.toString().length() > right.toString().length() ? Side.LEFT : Side.RIGHT;
    }

    /**
     * This gets the heavier side of the pair based on the length when using {@link Object#toString()}
     * @return The heavier object of the pair
     * @since 0.0.1
     * @see #getHeavierSide()
     */
    public Object getHeavierObject() {
        return get(getHeavierSide());
    }

    /**
     * Convert the Pair to a readable {@link String}. <br>
     * It uses the simple scheme: `{"right":[{@link #right() right field}], "left":[{@link #left() left field}]}`
     * @return The pair in a {@link String} format
     * @since 0.0.1
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
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
