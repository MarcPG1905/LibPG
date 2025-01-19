package com.marcpg.libpg.formular.question;

import com.marcpg.libpg.color.Ansi;
import com.marcpg.libpg.formular.CLIFormular;
import com.marcpg.libpg.formular.Formular;
import com.marcpg.libpg.formular.FormularResult;
import com.marcpg.libpg.storing.Pair;
import com.marcpg.libpg.text.Formatter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a formular's question, with title, description, input and rendering. <br>
 * <strong>Note that most features will require you to manually implement the JNA dependency (>5.14.0)!</strong>
 * @since 0.0.8
 * @author MarcPG1905
 */
public abstract class Question {
    /** The question's description. */
    protected Formular form;

    /** If the question was already submitted or not. */
    protected boolean submitted = false;

    /** The question's title. */
    public final String id;

    /** The question's title. */
    public final String title;

    /** The question's description. */
    public final String description;

    /** The question's requirement. Pair(ID, Value). */
    protected Pair<String, Object> requirement;

    /**
     * Creates a new question.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     */
    protected Question(String id, String title, String description) {
        this.id = id;
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
     * Sets the requirement of this question.  It works by checking if
     * the question with the set ID has the specified value or not.
     * The question is checked from this question's formular.
     * @param questionId The checked question's ID.
     * @param questionValue The value to check for.
     * @return A reference to this object.
     */
    public Question setRequirement(String questionId, Object questionValue) {
        requirement = Pair.of(questionId, questionValue);
        return this;
    }

    /**
     * Checks if this question does not meet the set requirement, if it's set.
     * @return {@code true} if the question doesn't meet the set requirement. <br>
     *         {@code false} if the question meets the set requirement.
     */
    protected final boolean invalid() {
        if (requirement == null) return false;

        Question question = form.getQuestion(requirement.left());
        if (form == null || question == null)
            return true;
        return question.toResult().result() != requirement.right();
    }

    /**
     * Resets the state of this question to be as if it was just created.
     * Won't reset the question's settings, but only the already chosen and rendered things.
     */
    public abstract void resetState();

    /** Submits the question with the current choice and locks in a final answer. */
    public abstract void submit();

    /**
     * Checks if the question is already submitted or not.
     * @return If the question is already submitted or not.
     */
    public final boolean isSubmitted() {
        return submitted;
    }

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

    /** Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. */
    public abstract void cliRender();

    protected void title(@NotNull CLIFormular cliForm) {
        cliForm.clearOutput();

        System.out.println(Ansi.formattedString("-> " + title + " <-", cliForm.ansiTheme, Ansi.BOLD));
        for (String line : Formatter.lineWrap(description, Math.max(50, title.length() * 2))) {
            System.out.println(Ansi.formattedString("|", cliForm.ansiTheme) + " " + line);
        }
    }

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
