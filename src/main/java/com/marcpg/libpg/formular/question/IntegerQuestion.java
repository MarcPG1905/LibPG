package com.marcpg.libpg.formular.question;

import com.marcpg.libpg.color.Ansi;
import com.marcpg.libpg.formular.CLIFormular;
import com.marcpg.libpg.formular.FormularResult;
import com.marcpg.libpg.text.Formatter;
import org.intellij.lang.annotations.Pattern;

import java.io.IOException;

/**
 * A question with a number, that can be negative and supports minimum and maximum values. <br>
 * <strong>Note that most features will require you to manually implement the JNA dependency (>5.14.0)!</strong>
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
        negative = false;
        input = 0;
    }

    /**
     * Sets the input, if the question wasn't already submitted
     * @param number The new input.
     */
    public void setInput(int number) {
        if (!inBounds(number))
            throw new QuestionException("Cannot set input, input is not in set bounds (" + min + "-" + max + ").", this);

        negative = number < 0;
        input = negative ? -number : number;
    }

    /**
     * Submits the question with a specific input.
     * @param number The input to be submitted.
     */
    public void submit(int number) {
        setInput(number);
        submit();
    }

    @Override
    public void submit() {
        if (submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);
        if (!inBounds(negative ? -input : input))
            throw new QuestionException("Cannot submit, input is not in set bounds (" + min + "-" + max + ").", this);

        submitted = true;
        form.nextQuestion();
    }

    @Override
    public Long getInput() {
        return negative ? -Math.abs(input) : input;
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.IntegerResult(id, negative ? -input : input);
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * If there's a min and max choice, it will ask this: {@code Enter a number from -10 to 10: }
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
                System.out.println(Ansi.gray("\n|| [ENTER]: Submit ||\n"));

                System.out.print(Ansi.gray("Enter a number" +
                        (min == Long.MIN_VALUE ? "" : " from " + min) +
                        (max == Long.MAX_VALUE ? "" : " to " + max) +
                        ": " + (inBounds(negative ? -input : input) ? "" : Ansi.RED) + (negative ? "-" : " ") + input + (inBounds(negative ? -input : input) ? "" : Ansi.RESET)));

                try {
                    int c = cliForm.input.read(true);
                    if (c >= '0' && c <= '9') {
                        try {
                            input = Long.parseLong(input + "" + (char) c);
                        } catch (NumberFormatException e) {
                            input = max;
                        }
                    } else if (c == '-') {
                        negative = true;
                    } else if (c == 8 || c == 127) {
                        if (input == 0)
                            negative = false;
                        else
                            input = Math.round((double) (input - 5) / 10);
                    } else if ((c == 10 || c == 13) && inBounds(negative ? -input : input)) {
                        submit();
                        form.render();
                        return;
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
        return number <= max && number >= min;
    }
}
