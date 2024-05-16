package com.marcpg.libpg.util;

import org.jetbrains.annotations.Range;

/**
 * Utility object that can be used for limiting something to a specific amount.
 * Useful for things like warnings that should only be sent n amount of times.
 */
public class Limiter {
    private final int limit;
    private int counter = 0;

    /**
     * Creates a new Limiter with a specific limit. The can not be 0 or less.
     * @param limit The limit, which cannot be changed afterwards.
     */
    public Limiter(@Range(from = 1, to = Integer.MAX_VALUE) int limit) {
        this.limit = limit;
    }

    /**
     * Increments the counter by one. If it already reached the limit, nothing happens.
     */
    public void increment() {
        if (counter < limit) counter++;
    }

    /**
     * First increments the counter by one. If it already reached the limit, nothing happens.
     * After that, it checks whether the limit is reached or not.
     * @return true if the counter is still under the limit. <br>
     *         false if it already reached the limit.
     * @see #increment()
     * @see #get()
     */
    public boolean incrementAndGet() {
        increment();
        return get();
    }

    /**
     * Gets whether the counter is still under the set limit or not.
     * @return true if the counter is still under the limit. <br>
     *         false if it already reached the limit.
     */
    public boolean get() {
        return counter >= limit;
    }

    /**
     * Gets the current counter, which will never be higher than the set limit.
     * @return The current counter.
     */
    public int getCounter() {
        return counter;
    }
}
