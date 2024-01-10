package com.marcpg.libpg.data.time;

/**
 * A template for a custom timer
 * @since 0.0.1
 * @author MarcPG1905
 */
public interface Timer {
    /**
     * Starts the timer
     * @since 0.0.1
     */
    void start();

    /**
     * Stops the timer, cannot be started again after that.
     * @since 0.0.1
     */
    void stop();

    /**
     * Get the time that's left before the timer finishes.
     * @return The time that's left
     * @since 0.0.1
     */
    Time getLeft();

    /**
     * Get the time that the timer already ran for.
     * @return The time that's done
     * @since 0.0.1
     */
    Time getDone();

    /**
     * Pauses the timer, can be resumed afterward.
     * Just {@link #stop()} but it isn't stopped completely.
     * @return true = The timer got paused.
     *         false = The timer was already paused, so nothing changed.
     * @since 0.0.1
     */
    default boolean pause() {
        return false;
    }

    /**
     * Resumes the timer after it got paused.
     * @return true = The timer got resumed.
     *         false = The timer wasn't paused, so nothing changed.
     * @since 0.0.1
     */
    default boolean resume() {
        return false;
    }
}
