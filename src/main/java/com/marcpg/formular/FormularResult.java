package com.marcpg.formular;

import jdk.jfr.Experimental;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * Represents a fully filled out formular's result with all values.
 * Also provides records for the different question type's answers.
 * @since 0.0.8
 * @author MarcPG1905
 */
@ApiStatus.Experimental
@Experimental
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
        this.results.add(result);
        return this;
    }

    /**
     * Removes a result from this formular result.
     * @param result The result to remove.
     * @return A reference to this object.
     */
    public FormularResult removeResult(Result result) {
        this.results.remove(result);
        return this;
    }

    /**
     * Clears all results from this formular result.
     * @return A reference to this object.
     */
    public FormularResult clearResults() {
        this.results.clear();
        return this;
    }

    /**
     * Converts the results to an iterator that can be used to go through all of them.
     * @return The converted iterator.
     */
    public Iterator<Result> toIterator() {
        return this.results.iterator();
    }

    /**
     * All results should extend this.
     * Provides a group for all results and the result() method, to get the value as an Object.
     */
    public interface Result {
        /**
         * Gets this result's value as an Object.
         * @return The result's value.
         */
        Object result();
    }

    /**
     * Represents a result of a {@link com.marcpg.formular.question.TextQuestion}.
     * @param name The question's name.
     * @param text The result's value.
     */
    public record TextResult(String name, String text) implements Result {
        @Override
        public @Nullable Object result() {
            return this.text;
        }
    }

    /**
     * Represents a result of a {@link com.marcpg.formular.question.IntegerQuestion}.
     * @param name The question's name.
     * @param value The result's value.
     */
    public record IntegerResult(String name, int value) implements Result {
        @Override
        public Object result() {
            return this.value;
        }
    }

    /**
     * Represents a result of a {@link com.marcpg.formular.question.BooleanQuestion}.
     * @param name The question's name.
     * @param value The result's value.
     */
    public record BooleanResult(String name, boolean value) implements Result {
        @Override
        public Object result() {
            return this.value;
        }
    }

    /**
     * Represents a result of a {@link com.marcpg.formular.question.MultipleChoiceQuestion}.
     * @param name The question's name.
     * @param value The result's value.
     */
    public record MultipleChoiceResult(String name, String value) implements Result {
        @Override
        public Object result() {
            return this.value;
        }
    }

    /**
     * Represents a result of a {@link com.marcpg.formular.question.CheckboxesQuestion}.
     * @param name The question's name.
     * @param chosen A list of the result's values.
     */
    public record CheckboxesResult(String name, List<String> chosen) implements Result {
        @Override
        public Object result() {
            return this.chosen;
        }
    }
}
