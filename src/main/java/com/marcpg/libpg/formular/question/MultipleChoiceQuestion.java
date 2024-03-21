package com.marcpg.libpg.formular.question;

import com.marcpg.libpg.color.Ansi;
import com.marcpg.libpg.formular.CLIFormular;
import com.marcpg.libpg.formular.FormularResult;
import com.marcpg.libpg.text.Formatter;
import org.intellij.lang.annotations.Pattern;

import java.io.IOException;
import java.util.List;

/**
 * A question with multiple choices. Only one choice can be checked.
 * @see Question
 * @see CheckboxesQuestion
 * @since 0.0.8
 * @author MarcPG1905
 */
public class MultipleChoiceQuestion extends Question {
    private final List<String> choices;
    private int cursorIndex = 0;
    private String choice;

    /**
     * Creates a new multiple-choice question.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param choices A list of the choices that can be chosen from.
     */
    public MultipleChoiceQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, List<String> choices) {
        super(id, title, description);
        this.choices = choices;
    }

    /**
     * Creates a new multiple-choice question.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param choices The choices that can be chosen from.
     */
    public MultipleChoiceQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, String... choices) {
        super(id, title, description);
        this.choices = List.of(choices);
    }

    @Override
    public void resetState() {
        cursorIndex = 0;
        choice = null;
    }

    /** Moves the cursor up one choice, if it's not already all the way up. */
    public void up() {
        if (cursorIndex > 0) cursorIndex--;
    }

    /** Moves the cursor down one choice, if it's not already at the bottom. */
    public void down() {
        if (cursorIndex < choices.size() - 1) cursorIndex++;
    }

    /**
     * Submits the question with a specific choice.
     * @param choice The index of the choice to be submitted.
     */
    public void submit(int choice) {
        this.choice = choices.get(choice);
        submit();
    }

    /**
     * Submits the question with a specific choice.
     * @param choice The choice to be submitted.
     */
    public void submit(String choice) {
        this.choice = choice;
        submit();
    }

    @Override
    public void submit() {
        if (submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);
        if (choice == null)
            throw new QuestionException("Cannot submit, choice is not set!", this);

        submitted = true;
        form.nextQuestion();
    }

    @Override
    public String getInput() {
        if (choice == null)
            throw new QuestionException("Cannot get choice, choice is not set!", this);
        return choice;
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.MultipleChoiceResult(id, choice);
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * Gives the user a list of all choices, which can be navigated using W for up, S for down and Space for toggling.
     */
    @Override
    public synchronized void cliRender() {
        if (form instanceof CLIFormular cliForm) {
            while (true) {
                if (invalid()) {
                    form.nextQuestion();
                    form.render();
                    return;
                }

                cliForm.clearOutput();

                System.out.println(Ansi.formattedString("-> " + title + " <-", cliForm.ansiTheme, Ansi.BOLD));
                for (String line : Formatter.lineWrap(description, Math.max(50, title.length() * 2))) {
                    System.out.println(Ansi.formattedString("|", cliForm.ansiTheme) + " " + line);
                }
                System.out.println(Ansi.gray("\n|| [W]: Up || [S]: Down || [ENTER]: Submit ||\n"));

                for (String option : choices) {
                    System.out.println("-> " + (choices.indexOf(option) == cursorIndex ? "\033[30m\033[47m" : "") + option + "\033[0m");
                }
                try {
                    switch (cliForm.input.read(true)) {
                        case 119, 87 -> up();
                        case 115, 83 -> down();
                        case 10, 13 -> {
                            submit(choices.get(cursorIndex));
                            form.render();
                            return;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            throw new IllegalStateException("Cannot use CLI rendering in a non-cli formular!");
        }
    }
}
