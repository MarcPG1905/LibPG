package com.marcpg.formular;

import com.marcpg.formular.question.Question;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a simple formular, similar to your everyday formulars, like Google Forms, but in Java.
 * @see CLIFormular
 * @since 0.0.8
 * @author MarcPG1905
 */
public abstract class Formular {
    /** The formular's title. */
    protected String title;

    /** The formular's description, which is only displayed on the first page. */
    protected String description;

    /** The formular's theme / accent color. */
    protected Color theme;

    /** If the formular was already submitted. */
    protected boolean submitted;

    /** The formular's callback, which is called once the formular is done. */
    protected Consumer<FormularResult> callback;

    private final List<Question> questions = new ArrayList<>();
    private int questionIndex = -1;

    /**
     * Creates a new formular with the basic required options.
     * @param title The formular's title.
     * @param description A detailed description of the formular. (displayed on first page)
     * @param theme The theme color. Will be used for decorations and as an accent color.
     * @param callback What will be executed once the formular is fully filled out.
     * @param questions A list of all questions in this formular.
     */
    protected Formular(String title, String description, @Nullable Color theme, Consumer<FormularResult> callback, @NotNull List<Question> questions) {
        this.title = title;
        this.description = description;
        this.theme = theme == null ? Color.WHITE : theme;
        this.callback = callback;
        this.questions.addAll(questions.stream().peek(q -> q.setForm(this)).toList());
    }

    /**
     * Creates a new formular with the basic required options.
     * @param title The formular's title.
     * @param description A detailed description of the formular. (displayed on first page)
     * @param theme The theme color. Will be used for decorations and as an accent color.
     * @param callback What will be executed once the formular is fully filled out.
     * @param questions All questions in this formular.
     */
    protected Formular(String title, String description, @Nullable Color theme, Consumer<FormularResult> callback, Question... questions) {
        this.title = title;
        this.description = description;
        this.theme = theme == null ? Color.WHITE : theme;
        this.callback = callback;
        this.questions.addAll(Arrays.stream(questions).peek(q -> q.setForm(this)).toList());
    }

    /**
     * Gets the formular's current title.
     * @return The formular's title.
     */
    public final String getTitle() {
        return this.title;
    }

    /**
     * Sets the formular's title. <br>
     * Should not be modified while the formular is displayed!
     * @param title The new title for this formular.
     * @return A reference to this object.
     */
    public final Formular setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Gets the formular's current description, which is only displayed on the first page.
     * @return The formular's description.
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Sets the formular's description. <br>
     * Should not be modified while the formular is displayed!
     * @param description The new description for this formular.
     * @return A reference to this object.
     */
    public final Formular setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Gets the formular's current color theme, which is used for decoration and as an accent color.
     * @return The formular's color theme.
     */
    public final Color getTheme() {
        return this.theme;
    }

    /**
     * Sets the formular's theme color. <br>
     * Should not be modified while the formular is displayed!
     * @param theme The new theme for this formular.
     * @return A reference to this object.
     */
    public final Formular setTheme(Color theme) {
        this.theme = theme;
        return this;
    }

    /**
     * Gets this formular's callback.
     * @return The formular's callback.
     */
    public final Consumer<FormularResult> getCallback() {
        return this.callback;
    }

    /**
     * Sets this formular's callback.
     * @param callback The consumer to set the callback to.
     * @return A reference to this object.
     */
    public final Formular setCallback(Consumer<FormularResult> callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Converts this formular to a {@link FormularResult result}.
     * @return The converted formular result..
     */
    public final @NotNull FormularResult toResult() {
        return new FormularResult(this.questions.stream().filter(Question::isSubmitted).map(Question::toResult).toList());
    }

    /**
     * Gets a question of this formular based on its ID.
     * @param id The question's ID.
     * @return The question matching the specified ID or {@code null} otherwise.
     */
    public final @Nullable Question getQuestion(String id) {
        for (Question question : this.questions) {
            if (question.id.equals(id))
                return question;
        }
        return null;
    }

    /**
     * Gets a list of the formular's current questions.
     * @return The formular's current questions.
     */
    public final List<Question> getQuestions() {
        return this.questions;
    }

    /**
     * Appends some new questions to the end of this formular.
     * @param questions The questions to append.
     * @return A reference to this object.
     */
    public final Formular addQuestions(@NotNull List<Question> questions) {
        this.questions.addAll(questions.stream().peek(q -> q.setForm(this)).toList());
        return this;
    }

    /**
     * Appends some new questions to the end of this formular.
     * @param questions The questions to append.
     * @return A reference to this object.
     */
    public final Formular addQuestions(Question... questions) {
        this.questions.addAll(Arrays.stream(questions).peek(q -> q.setForm(this)).toList());
        return this;
    }

    /**
     * Appends a new question to the end of this formular.
     * @param question The question to append.
     * @return A reference to this object.
     */
    public final Formular addQuestion(@NotNull Question question) {
        question.setForm(this);
        this.questions.add(question);
        return this;
    }

    /** Goes to the next question/page. If this is already on the last page, it will throw a {@link FormularException}*/
    public void nextQuestion() {
        if (this.questionIndex + 1 >= this.questions.size()) {
            this.submitted = true;
            if (this.callback != null) {
                this.callback.accept(this.toResult());
            }
        }
        this.questionIndex++;
    }

    /**
     * Gets the formular's current question/page as a {@link Question} object.
     * @return The formular's current question/page.
     * @throws FormularException if this is still at the description, or the page is over the amount of questions.
     */
    public final Question currentQuestion() {
        if (this.questionIndex == -1)
            throw new FormularException("Cannot get current question, still at description.", this);
        if (this.questionIndex >= this.questions.size())
            throw new FormularException("Cannot get current question, formular is already done.", this);
        return this.questions.get(this.questionIndex);
    }

    /**
     * Gets the formular's current question/page as an index starting at 0. <br>
     * A value of {@code -1} means it's still on the description page. <br>
     * A value of {@code 0} is page 1, so the first question, after the description.
     * @return The formular's current question/page number.
     */
    public final int getPage() {
        return this.questionIndex;
    }

    /** Renders the current question/page. */
    public abstract void render();

    /** Represents an {@link RuntimeException exception} thrown if there was an issue with the formular. */
    public static final class FormularException extends RuntimeException {
        /**
         * Creates a new {@link FormularException} with a description and the {@link Formular} type which caused it.
         * @param message A short description of what went wrong.
         * @param formular The formular which caused this exception.
         */
        public FormularException(String message, @NotNull Formular formular) {
            super(formular.getClass().getSimpleName() + ": " + message);
        }
    }
}
