package com.marcpg.libpg.util;

/**
 * Represents an operation that accepts an input, returns no result
 * and throws a specified exception when {@link #accept(Object)} accepted}.
 * @param <T> the type of the first argument to the operation.
 * @param <E> the type of exception that is thrown when accepted.
 * @see java.util.function.Consumer
 * @see ThrowingBiConsumer
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    /**
     * Performs this operation on the given arguments.
     * @param t the first input argument
     * @throws E the specified exception
     */
    void accept(T t) throws E;
}
