package com.marcpg.libpg.util;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This is just another word for a backdoor. This should not be used for malicious purposes,
 * but only in situations where there's a high chance the software not being paid for.
 * @since 0.1.2
 * @author MarcPG1905
 */
public class GraceAccess {
    /**
     * Represents the state of the backdoor.
     * The server should either return the ordinal, or the name of the state.
     * @since 0.1.2
     * @author MarcPG1905
     */
    public enum State {
        /** The server could not be reached. */ UNAVAILABLE,
        /** The time is not yet up. Waiting for a result. */ WAIT,
        /** The software was paid for. Disable backdoor. */ PAID,
        /** The software was not paid for. Activate backdoor. */ UNPAID;

        /**
         * Tries getting a state out of the string. Considers these two possibilities:
         * <ol>
         *     <li>The state's name (case insensitive) is in the string.</li>
         *     <li>The state's ordinal integer (index) is in the string.</li>
         * </ol>
         * @param name The input string containing the name or index.
         * @return The state, or {@link #UNAVAILABLE} if the string did not contain a valid state.
         */
        public static State of(String name) {
            try {
                return State.valueOf(name.toUpperCase());
            } catch (Exception e1) {
                try {
                    return State.values()[Integer.parseInt(name)];
                } catch (Exception e2) {
                    return State.UNAVAILABLE;
                }
            }
        }
    }

    /** The address to the server that returns the backdoor state. */
    protected final URI uri;

    /**
     * Creates a new grace access (backdoor).
     * @param uri The server that returns the state.
     */
    public GraceAccess(URI uri) {
        this.uri = uri;
    }

    /**
     * Checks the current state of the backdoor.
     * This does not execute any additional logic and only checks the state.
     * @return The state of the backdoor.
     * @see #check(Runnable, Runnable)
     */
    public State state() {
        try {
            return State.of(HttpClient.newHttpClient().send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofString()).body());
        } catch (Exception e) {
            return State.UNAVAILABLE;
        }
    }

    /**
     * Checks the current state of the backdoor and executes accordingly.
     * @param backdoor Logic to be ran if the software did not get paid for.
     * @param disable Logic to be ran if the software got paid for.
     * @return The state of the backdoor.
     * @see #state()
     */
    public State check(@Nullable Runnable backdoor, @Nullable Runnable disable) {
        State state = state();
        if (state == State.PAID && disable != null) disable.run();
        if (state == State.UNPAID && backdoor != null) backdoor.run();
        return state;
    }
}
