package com.marcpg.formular.question;

import com.marcpg.formular.Formular;
import com.marcpg.formular.FormularResult;
import jdk.jfr.Experimental;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a formular's question, with title, description, input and rendering.
 * @since 0.0.8
 * @author MarcPG1905
 */
@ApiStatus.Experimental
@Experimental
public abstract class Question {
    /** The question's description. */
    protected Formular form;

    /** If the question was already submitted or not. */
    protected boolean submitted = false;

    /** The question's title. */
    protected final String title;

    /** The question's description. */
    protected final String description;

    /**
     * Creates a new question.
     * @param title The question's title.
     * @param description The question's description.
     */
    protected Question(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * Sets the {@link Formular} that manages and renders this question.
     * @param form The {@link Formular} that manages and renders this question.
     */
    public void setForm(Formular form) {
        this.form = form;
    }

    /**
     * Resets the state of this question to be as if it was just created.
     * Won't reset the question's settings, but only the already chosen and rendered things.
     */
    public abstract void resetState();

    /** Submits the question with the current choice and locks in a final answer. */
    public abstract void submit();

    /**
     * Gets the current or preferably final input of the question.
     * @return The current or preferably final input of this question.
     */
    public abstract Object getInput();

    /**
     * Converts this question to a Result, that can be used in a FormularResult.
     * @return The converted result.
     */
    public abstract FormularResult.Result toResult();

    /** Renders the current question to {@link System#out}. Will only work for {@link com.marcpg.formular.CLIFormular}s. */
    public abstract void cliRender();

    /**
     * Represents an exception that's thrown by a question. <br>
     * This is typically only caused by manual code execution and not by user input.
     */
    public static final class QuestionException extends RuntimeException {
        /**
         * Creates a new {@link QuestionException} with a description and the {@link Question} type which caused it.
         * @param message A short description of what went wrong.
         * @param question The question which caused this exception.
         */
        public QuestionException(String message, @NotNull Question question) {
            super(question.getClass().getSimpleName() + ": " + message);
        }
    }
}
