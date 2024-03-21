package com.marcpg.libpg.formular.question;

import com.marcpg.libpg.color.Ansi;
import com.marcpg.libpg.formular.CLIFormular;
import com.marcpg.libpg.formular.FormularResult;
import com.marcpg.libpg.text.Formatter;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A question with multiple choices, that can each be checked or unchecked. Supports multiple checked choices.
 * @see Question
 * @see MultipleChoiceQuestion
 * @since 0.0.8
 * @author MarcPG1905
 */
public class CheckboxesQuestion extends Question {
    private final Map<String, Boolean> input;
    private int cursorIndex = 0;

    /**
     * Creates a new checkboxes question.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param choices A list of all choices that can be checked.
     */
    public CheckboxesQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, @NotNull List<String> choices) {
        super(id, title, description);
        this.input = choices.stream().collect(Collectors.toMap(s -> s, s -> false));
    }

    /**
     * Creates a new checkboxes question.
     * @param id The question's <b>unique</b> identifier.
     * @param title The question's title.
     * @param description The question's description.
     * @param choices All choices that can be checked.
     */
    public CheckboxesQuestion(@Pattern("[a-z0-9_-]+") String id, String title, String description, String... choices) {
        super(id, title, description);
        this.input = Arrays.stream(choices).collect(Collectors.toMap(s -> s, s -> false));
    }

    @Override
    public void resetState() {
        cursorIndex = 0;
        input.replaceAll((s, v) -> false);
    }

    /** Moves the cursor up one choice, if it's not already all the way up. */
    public void up() {
        if (cursorIndex > 0) cursorIndex--;
    }

    /** Moves the cursor down one choice, if it's not already at the bottom. */
    public void down() {
        if (cursorIndex < input.size() - 1) cursorIndex++;
    }

    /**
     * Toggles a choice based on its name, if the question wasn't already submitted.
     * @param choice The choice to toggle.
     */
    public void toggle(String choice) {
        if (!input.containsKey(choice))
            throw new QuestionException("Cannot toggle choice, valid choices don't contain specified choice: \"" + choice + "\"", this);
        input.put(choice, !input.get(choice));
    }

    @Override
    public void submit() {
        if (submitted)
            throw new QuestionException("Cannot submit, the question was already submitted!", this);

        submitted = true;
        form.nextQuestion();
    }

    @Override
    public String[] getInput() {
        return input.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }

    @Override
    public FormularResult.Result toResult() {
        return new FormularResult.CheckboxesResult(id, input.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList());
    }

    /**
     * Renders the current question to {@link System#out}. Will only work for {@link CLIFormular}s. <br>
     * Gives the user a list of checkboxes ({@code [ ] Name}/{@code [x] Name}),
     * which can be navigated using W for up, S for down and Space for toggling.
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
                System.out.println(Ansi.gray("\n|| [W]: Up || [S]: Down || [SPACE]: Toggle || [ENTER]: Submit ||\n"));

                int index = 0;
                for (Map.Entry<String, Boolean> option : input.entrySet()) {
                    System.out.println("[" + (option.getValue() ? "x" : " ") + "] " + (index++ == cursorIndex ? "\033[30m\033[47m" : "") + option.getKey() + "\033[0m");
                }
                try {
                    switch (cliForm.input.read(true)) {
                        case 119, 87 -> up();
                        case 115, 83 -> down();
                        case 32 -> toggle(input.keySet().toArray(String[]::new)[cursorIndex]);
                        case 10, 13 -> {
                            submit();
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
