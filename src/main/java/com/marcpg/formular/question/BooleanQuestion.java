package com.marcpg.formular.question;

import com.marcpg.color.Ansi;
import com.marcpg.formular.CLIFormular;
import com.marcpg.formular.FormularResult;
import com.marcpg.text.Formatter;
import org.intellij.lang.annotations.Pattern;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * A question with either true or false, with advanced input supporting multiple languages, based on starting letters.
 * @see Question
 * @since 0.0.8
 * @author MarcPG1905
 */
public class BooleanQuestion extends Question {
    /** All starting letters that correspond to a negative answer. */
    public static final List<Character> FALSE_LETTERS = List.of('f', 'n');

    /** All starting letters that correspond to a positive answer. */
    public static final List<Character> TRUE_LETTERS = List.of('t', 'y', 's', 'j', 'k');

    private final boolean defaultChoice;
    private boolean choice;

    /**
     * Creates a new boolean question.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param defaultChoice The question's default choice.
     */
    public BooleanQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, boolean defaultChoice) {
        super(id, title, description);
        this.defaultChoice = defaultChoice;
        this.choice = defaultChoice;
    }

    /**
     * Creates a new boolean question with the default choice set to true.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     */
    public BooleanQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description) {
        super(id, title, description);
        this.defaultChoice = true;
        this.choice = true;
    }

    /**
     * Gets the set default choice. By default, this is true.
     * @return The question's default choice.
     */
    public boolean getDefaultChoice() {
        return defaultChoice;
    }

    @Override
    public void resetState() {
        choice = defaultChoice;
    }

    /**
     * Sets the choice, if the question wasn't already submitted.
     * @param choice The new choice.
     */
    public void setChoice(boolean choice) {
        if (submitted)
            throw new QuestionException("Cannot set choice, the question was already submitted!", this);
        this.choice = choice;
    }

    /**
     * Submits the question with a specific value.
     * @param choice The choice to be submitted.
     */
    public void submit(boolean choice) {
        setChoice(choice);
        submit();
    }

    @Override
    public void submit() {
        if (submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);

        submitted = true;
        form.nextQuestion();
    }

    @Override
    public Boolean getInput() {
        return choice;
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.BooleanResult(id, choice);
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * If the default choice is true, it will ask this: {@code Choice [Y|n]: }
     */
    @Override
    public void cliRender() {
        if (form instanceof CLIFormular cliForm) {
            if (invalid()) {
                form.nextQuestion();
                form.render();
                return;
            }

            try {
                cliForm.input.resetConsoleMode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            boolean error = false;
            while (true) {
                cliForm.clearOutput();

                System.out.println(Ansi.formattedString("-> " + title + " <-", cliForm.ansiTheme, Ansi.BOLD));
                for (String line : Formatter.lineWrap(description, Math.max(50, title.length() * 2))) {
                    System.out.println(Ansi.formattedString("|", cliForm.ansiTheme) + " " + line);
                }
                System.out.println(Ansi.gray("\n|| [ENTER]: Submit ||\n"));

                if (error) {
                    System.out.println(Ansi.red("Invalid Input! Try again:"));
                    error = false;
                }

                System.out.print("Choice [" + (defaultChoice ? "Y|n" : "y|N") + "]: ");

                String input = new Scanner(System.in).nextLine();
                if (input.isBlank()) {
                    submit(defaultChoice);
                } else if (FALSE_LETTERS.contains(input.charAt(0))) {
                    submit(false);
                } else if (TRUE_LETTERS.contains(input.charAt(0))) {
                    submit(true);
                } else {
                    error = true;
                }
                if (submitted) {
                    form.render();
                    return;
                }
            }
        } else {
            throw new IllegalStateException("Cannot use CLI rendering in a non-cli formular!");
        }
    }
}
