package com.marcpg.data.time;

/**
 * A template for a custom timer
 * @since 0.0.1
 * @author MarcPG1905
 */
public abstract class Timer {
    /** The time that's ticking down. */
    protected final Time timer;

    /** The initial time that was set. */
    protected final Time initialTime;

    /**
     * Creates a new Timer object with a specified starting time.
     * @param time The starting time.
     */
    public Timer(Time time) {
        this.timer = time;
        this.initialTime = time;
    }

    /**
     * Starts the timer, to decrement the time.
     * @see #stop()
     */
    public abstract void start();

    /**
     * Stops the timer, cannot be started again after that.
     */
    public abstract void stop();

    /**
     * Get the time that's left before the timer finishes.
     * @return The time that's left.
     */
    public Time getLeft() {
        return timer;
    }

    /**
     * Get the time that the timer already ran for.
     * @return The time that's done.
     */
    public Time getDone() {
        return new Time(initialTime.get() - timer.get());
    }

    /**
     * Pauses the timer, can be resumed afterward.
     * Just {@link #stop()} but it isn't stopped completely.
     * @return true = The timer got paused.
     *         false = The timer was already paused, so nothing changed.
     */
    public boolean pause() { return false; }

    /**
     * Resumes the timer after it got paused.
     * @return true = The timer got resumed.
     *         false = The timer wasn't paused, so nothing changed.
     */
    public boolean resume() { return false; }
}
