package com.marcpg.libpg.formular;

import com.marcpg.libpg.formular.question.*;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a fully filled out formular's result with all values.
 * Also provides records for the different question type's answers. <br>
 * <strong>Note that most features will require you to manually implement the JNA dependency (>5.14.0)!</strong>
 * @since 0.0.8
 * @author MarcPG1905
 */
public class FormularResult {
    /** A list of all results of this formular result. */
    public final List<Result> results;

    /**
     * Creates a new formular result with a list of the results.
     * @param results The list of all results.
     */
    public FormularResult(List<Result> results) {
        this.results = results;
    }

    /**
     * Creates a new formular result with all the results.
     * @param results All results.
     */
    public FormularResult(Result... results) {
        this.results = List.of(results);
    }

    /**
     * Adds a result to this formular result.
     * @param result The result to add.
     * @return A reference to this object.
     */
    public FormularResult addResult(Result result) {
        results.add(result);
        return this;
    }

    /**
     * Removes a result from this formular result.
     * @param result The result to remove.
     * @return A reference to this object.
     */
    public FormularResult removeResult(Result result) {
        results.remove(result);
        return this;
    }

    /**
     * Clears all results from this formular result.
     * @return A reference to this object.
     */
    public FormularResult clearResults() {
        results.clear();
        return this;
    }

    /**
     * Converts the results to an iterator that can be used to go through all of them.
     * @return The converted iterator.
     */
    public Iterator<Result> toIterator() {
        return results.iterator();
    }

    /**
     * All results should extend this.
     * Provides a group for all results and the result() method, to get the value as an Object.
     */
    public interface Result {
        /**
         * Gets this result's id.
         * @return The result's id.
         */
        String id();

        /**
         * Gets this result's value as an Object.
         * @return The result's value.
         */
        Object result();
    }

    /**
     * Represents a result of a {@link TextQuestion}.
     * @param id The question's id.
     * @param text The result's value.
     */
    public record TextResult(String id, String text) implements Result {
        @Override
        public String result() {
            return text;
        }
    }

    /**
     * Represents a result of a {@link IntegerQuestion}.
     * @param id The question's id.
     * @param value The result's value.
     */
    public record IntegerResult(String id, long value) implements Result {
        @Override
        public Long result() {
            return value;
        }
    }

    /**
     * Represents a result of a {@link BooleanQuestion}.
     * @param id The question's id.
     * @param value The result's value.
     */
    public record BooleanResult(String id, boolean value) implements Result {
        @Override
        public Boolean result() {
            return value;
        }
    }

    /**
     * Represents a result of a {@link MultipleChoiceQuestion}.
     * @param id The question's id.
     * @param value The result's value.
     */
    public record MultipleChoiceResult(String id, String value) implements Result {
        @Override
        public String result() {
            return value;
        }
    }

    /**
     * Represents a result of a {@link CheckboxesQuestion}.
     * @param id The question's id.
     * @param chosen A list of the result's values.
     */
    public record CheckboxesResult(String id, List<String> chosen) implements Result {
        @Override
        public List<String> result() {
            return chosen;
        }
    }
}
