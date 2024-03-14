package com.marcpg.formular.question;

import com.marcpg.color.Ansi;
import com.marcpg.formular.CLIFormular;
import com.marcpg.formular.FormularResult;
import com.marcpg.text.Formatter;
import org.intellij.lang.annotations.Pattern;

import java.io.IOException;

/**
 * A question with a number, that can be negative and supports minimum and maximum values.
 * @see Question
 * @since 0.0.8
 * @author MarcPG1905
 */
public class IntegerQuestion extends Question {
    private final long min;
    private final long max;
    private boolean negative;
    private long input;

    /**
     * Creates a new integer question with a minimum and maximum value.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param min The minimum value that can be set.
     * @param max The maximum value that can be set.
     */
    public IntegerQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, long min, long max) {
        super(id, title, description);
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new integer question with minimum value of 0 and a set maximum value.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param max The maximum value that can be set.
     */
    public IntegerQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, int max) {
        super(id, title, description);
        this.min = 0;
        this.max = max;
    }

    /**
     * Creates a new integer question with no bounds.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     */
    public IntegerQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description) {
        super(id, title, description);
        this.min = Long.MIN_VALUE;
        this.max = Long.MAX_VALUE;
    }

    @Override
    public void resetState() {
        this.negative = false;
        this.input = 0;
    }

    /**
     * Sets the input, if the question wasn't already submitted
     * @param number The new input.
     */
    public void setInput(int number) {
        if (!this.inBounds(number))
            throw new QuestionException("Cannot set input, input is not in set bounds (" + this.min + "-" + this.max + ").", this);

        this.negative = number < 0;
        this.input = this.negative ? -number : number;
    }

    /**
     * Submits the question with a specific input.
     * @param number The input to be submitted.
     */
    public void submit(int number) {
        this.setInput(number);
        this.submit();
    }

    @Override
    public void submit() {
        if (this.submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);
        if (!this.inBounds(this.negative ? -this.input : this.input))
            throw new QuestionException("Cannot submit, input is not in set bounds (" + this.min + "-" + this.max + ").", this);

        this.submitted = true;
        this.form.nextQuestion();
    }

    @Override
    public Long getInput() {
        return this.negative ? -Math.abs(this.input) : this.input;
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.IntegerResult(this.id, this.negative ? -this.input : this.input);
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * If there's a min and max choice, it will ask this: {@code Enter a number from -10 to 10: }
     */
    @Override
    public synchronized void cliRender() {
        if (this.form instanceof CLIFormular cliForm) {
            while (true) {
                if (this.invalid()) {
                    this.form.nextQuestion();
                    this.form.render();
                    return;
                }

                cliForm.clearOutput();

                System.out.println(Ansi.formattedString("-> " + this.title + " <-", cliForm.ansiTheme, Ansi.BOLD));
                for (String line : Formatter.lineWrap(this.description, Math.max(50, this.title.length() * 2))) {
                    System.out.println(Ansi.formattedString("|", cliForm.ansiTheme) + " " + line);
                }
                System.out.println(Ansi.gray("\n|| [ENTER]: Submit ||\n"));

                System.out.print(Ansi.gray("Enter a number" +
                        (this.min == Long.MIN_VALUE ? "" : " from " + this.min) +
                        (this.max == Long.MAX_VALUE ? "" : " to " + this.max) +
                        ": " + (this.inBounds(this.negative ? -this.input : this.input) ? "" : Ansi.RED) + (this.negative ? "-" : " ") + this.input + (this.inBounds(this.negative ? -this.input : this.input) ? "" : Ansi.RESET)));
                try {
                    int number = cliForm.input.read(true);
                    if (number >= 48 && number <= 57) {
                        try {
                            this.input = Long.parseLong(this.input + "" + (number - 48));
                        } catch (NumberFormatException e) {
                            this.input = Long.MAX_VALUE;
                        }
                    } else if (number == 45 && this.input == 0) {
                        this.negative = true;
                    } else if ((number == 8 || number == 127)) {
                        if (this.input == 0) {
                            this.negative = false;
                        } else {
                            String s = String.valueOf(this.input);
                            if (s.length() == 1) {
                                this.input = 0;
                            } else {
                                this.input = Long.parseLong(s.substring(0, s.length() - 1));
                            }
                        }
                    } else if ((number == 10 || number == 13) && this.inBounds(this.negative ? -this.input : this.input)) {
                        this.submit();
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

    private boolean inBounds(long number) {
        return this.input <= this.max && this.input >= this.min;
    }
}
