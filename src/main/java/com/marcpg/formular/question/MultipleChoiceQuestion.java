package com.marcpg.formular.question;

import com.marcpg.color.Ansi;
import com.marcpg.formular.CLIFormular;
import com.marcpg.formular.FormularResult;
import com.marcpg.text.Formatter;
import jdk.jfr.Experimental;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.List;

/**
 * A question with multiple choices. Only one choice can be checked.
 * @see Question
 * @see CheckboxesQuestion
 * @since 0.0.8
 * @author MarcPG1905
 */
@ApiStatus.Experimental
@Experimental
public class MultipleChoiceQuestion extends Question {
    private final List<String> choices;
    private int cursorIndex = 0;
    private String choice;

    /**
     * Creates a new multiple-choice question.
     * @param title The question's title.
     * @param description The question's description.
     * @param choices A list of the choices that can be chosen from.
     */
    public MultipleChoiceQuestion(String title, String description, List<String> choices) {
        super(title, description);
        this.choices = choices;
    }

    /**
     * Creates a new multiple-choice question.
     * @param title The question's title.
     * @param description The question's description.
     * @param choices The choices that can be chosen from.
     */
    public MultipleChoiceQuestion(String title, String description, String... choices) {
        super(title, description);
        this.choices = List.of(choices);
    }

    @Override
    public void resetState() {
        this.cursorIndex = 0;
        this.choice = null;
    }

    /** Moves the cursor up one choice, if it's not already all the way up. */
    public void up() {
        if (this.cursorIndex > 0) this.cursorIndex--;
    }

    /** Moves the cursor down one choice, if it's not already at the bottom. */
    public void down() {
        if (this.cursorIndex < this.choices.size() - 1) this.cursorIndex++;
    }

    /**
     * Submits the question with a specific choice.
     * @param choice The index of the choice to be submitted.
     */
    public void submit(int choice) {
        this.choice = this.choices.get(choice);
        this.submit();
    }

    /**
     * Submits the question with a specific choice.
     * @param choice The choice to be submitted.
     */
    public void submit(String choice) {
        this.choice = choice;
        this.submit();
    }

    @Override
    public void submit() {
        if (this.submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);
        if (this.choice == null)
            throw new QuestionException("Cannot submit, choice is not set!", this);

        this.submitted = true;
        this.form.nextQuestion();
    }

    @Override
    public String getInput() {
        if (this.choice == null)
            throw new QuestionException("Cannot get choice, choice is not set!", this);
        return this.choice;
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.MultipleChoiceResult(this.title, this.choice);
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * Gives the user a list of all choices, which can be navigated using W for up, S for down and Space for toggling.
     */
    @Override
    public synchronized void cliRender() {
        if (this.form instanceof CLIFormular cliForm) {
            while (true) {
                cliForm.clearOutput();

                System.out.println(Ansi.formattedString("-> " + this.title + " <-", cliForm.ansiTheme, Ansi.BOLD));
                for (String line : Formatter.lineWrap(this.description, Math.max(50, this.title.length() * 2))) {
                    System.out.println(Ansi.formattedString("|", cliForm.ansiTheme) + " " + line);
                }
                System.out.println(Ansi.gray("\n|| [W]: Up || [S]: Down || [ENTER]: Submit ||\n"));

                for (String option : this.choices) {
                    System.out.println("-> " + (this.choices.indexOf(option) == this.cursorIndex ? "\033[30m\033[47m" : "") + option + "\033[0m");
                }
                try {
                    CLIFormular.NavigationButton button = CLIFormular.NavigationButton.getButton(cliForm.input.read(true));
                    switch (button) {
                        case UP -> this.up();
                        case DOWN -> this.down();
                        case SUBMIT -> this.submit(this.choices.get(this.cursorIndex));
                    }
                    if (this.submitted) {
                        this.form.render();
                        break;
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
