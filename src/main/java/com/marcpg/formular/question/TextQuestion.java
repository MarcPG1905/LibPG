package com.marcpg.formular.question;

import com.marcpg.color.Ansi;
import com.marcpg.formular.CLIFormular;
import com.marcpg.formular.FormularResult;
import com.marcpg.text.Formatter;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A question with text, that supports any characters between ASCII 32 (' ') and 126 ('~').
 * @see Question
 * @see IntegerQuestion
 * @since 0.0.8
 * @author MarcPG1905
 */
public class TextQuestion extends Question {
    private final int characterLimit;
    private StringBuilder input = new StringBuilder();

    /**
     * Creates a new text question with a specific character limit.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param characterLimit How long the text can be.
     */
    public TextQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, int characterLimit) {
        super(id, title, description);
        this.characterLimit = characterLimit;
    }

    /**
     * Creates a new text question with the default character limit of 128.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     */
    public TextQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description) {
        super(id, title, description);
        this.characterLimit = 128;
    }

    @Override
    public void resetState() {
        this.input = new StringBuilder();
    }

    /**
     * Gets the text question's character limit, which is 128 by default.
     * @return The character limit.
     */
    public int getCharacterLimit() {
        return this.characterLimit;
    }

    /**
     * Sets the input, if the question isn't already submitted.
     * @param input The input.
     */
    public void setInput(CharSequence input) {
        if (this.tooLong(input))
            throw new QuestionException("Cannot set input, input exceeds the character limit.", this);
        this.input = new StringBuilder(input);
    }

    /**
     * Submits the question with a specific input.
     * @param input The input to be submitted.
     */
    public void submit(CharSequence input) {
        this.setInput(input);
        this.submit();
    }

    @Override
    public void submit() {
        if (this.submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);
        if (this.input == null)
            throw new QuestionException("Cannot submit, input is not set!", this);
        if (this.input.toString().isBlank())
            throw new QuestionException("Cannot submit, input is empty or blank!", this);
        if (this.tooLong(this.input))
            throw new QuestionException("Cannot submit, input exceeds the character limit!", this);

        this.submitted = true;
        this.form.nextQuestion();
    }

    @Override
    public String getInput() {
        return this.input.toString();
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.TextResult(this.id, this.input.toString());
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * If the character limit is set to 128, it will ask this: {@code Enter Text (0/128): }
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

                System.out.print(Ansi.gray("Enter Text (" + this.input.length() + "/" + this.characterLimit + "): " + this.input));
                try {
                    int character = cliForm.input.read(true);
                    if (character >= 32 && character <= 126 && this.input.length() < this.characterLimit) { // 32 = ' ' | 126 = '~'
                        this.input.append((char) character);
                    } else if ((character == 8 || character == 127) && !this.input.isEmpty()) {
                        this.input.deleteCharAt(this.input.length() - 1);
                    } else if (character == 22) {
                        this.pasteFromClipboard();
                    } else if ((character == 10 || character == 13) && !this.input.toString().isBlank()) {
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

    private void pasteFromClipboard() {
        try {
            this.input.append(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
            if (this.input.length() > this.characterLimit)
                this.input.setLength(this.characterLimit);
        } catch (IllegalStateException | UnsupportedFlavorException | IOException ignored) {}
    }

    private boolean tooLong(@NotNull CharSequence str) {
        return str.length() > this.characterLimit;
    }
}
