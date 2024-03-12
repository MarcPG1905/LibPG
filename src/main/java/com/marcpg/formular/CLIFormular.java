package com.marcpg.formular;

import com.marcpg.color.Ansi;
import com.marcpg.formular.question.Question;
import com.marcpg.text.Formatter;
import jdk.jfr.Experimental;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a CLI (Command Line Interface) {@link Formular}, that can be displayed inside of consoles. <br>
 * The console needs to support ANSI formatting and needs to run on Windows or any UNIX system.
 * @see Formular
 * @since 0.0.8
 * @author MarcPG1905
 */
@ApiStatus.Experimental
@Experimental
public class CLIFormular extends Formular {
    /**
     * Represents simple button bindings for the console-based input.
     * Uses ASCII character decimals, that may vary between Windows and UNIX.
     */
    public enum NavigationButton {
        /** w/W = Up */ UP,
        /** s/S = Down */ DOWN,
        /** SPACE = Toggle */ TOGGLE,
        /** ENTER = Submit */ SUBMIT,
        /** BS/DEL = Backspace */ BACKSPACE,
        /** 0-9 = Numeral */ NUMERAL,
        /** Ctrl+X/C = Exit/Cancel */ EXIT,
        /** Anything Else */ INVALID;

        /**
         * Gets a button based on the ASCII character code.
         * @param code The ASCII character code in the decimal format.
         * @return The {@link NavigationButton} corresponding to the character.
         */
        public static NavigationButton getButton(int code) {
            if (code >= 48 && code <= 57)
                return NUMERAL;

            return switch (code) {
                case 119, 87 -> UP;
                case 115, 83 -> DOWN;
                case 32 -> TOGGLE;
                case 10, 13 -> SUBMIT;
                case 24, 3 -> EXIT;
                case 8, 127 -> BACKSPACE;
                default -> INVALID;
            };
        }
    }

    /** The {@link RawConsoleInput} manager used for getting input. */
    public final RawConsoleInput input = new RawConsoleInput();

    /** The theme of the formular converted to an {@link Ansi ANSI color}. */
    public final Ansi ansiTheme;

    /**
     * Creates a new console-based formular with the basic required options. <br>
     * The input and output will be the {@link System#out system output} and {@link System#in system input}.
     * @param title The formular's title.
     * @param description A detailed description of the formular. (displayed on first page)
     * @param theme The theme color. Will be used for decorations and as an accent color.
     * @param callback What will be executed once the formular is fully filled out.
     * @param questions A list of all questions in this formular.
     */
    public CLIFormular(String title, String description, @Nullable Color theme, Consumer<FormularResult> callback, @NotNull List<Question> questions) {
        super(title, description, theme, callback, questions);
        this.ansiTheme = Ansi.fromColor(this.theme);
    }

    /**
     * Creates a new console-based formular with the basic required options. <br>
     * The input and output will be the {@link System#out system output} and {@link System#in system input}.
     * @param title The formular's title.
     * @param description A detailed description of the formular. (displayed on first page)
     * @param theme The theme color. Will be used for decorations and as an accent color.
     * @param callback What will be executed once the formular is fully filled out.
     * @param questions All questions in this formular.
     */
    public CLIFormular(String title, String description, @Nullable Color theme, Consumer<FormularResult> callback, Question... questions) {
        super(title, description, theme, callback, questions);
        this.ansiTheme = Ansi.fromColor(this.theme);
    }

    /**
     * Renders the current page/question into {@link System#out}. <br>
     * For this to properly work, the runtime needs to have a valid input/output with a console, which means that it can't run on IDEs for example.
     * You can test that by checking if {@link System#console()} returns something. If it returns null, it means this method won't work.
     */
    @Override
    public void render() {
        if (this.getPage() == -1) {
            System.out.println(Ansi.formattedString("=== " + this.title + " ===", this.ansiTheme, Ansi.BOLD));
            for (String line : Formatter.lineWrap(this.description, Math.max(50, this.title.length() * 2))) {
                System.out.println(Ansi.formattedString("|", this.ansiTheme) + " " + line);
            }
            System.out.println(Ansi.gray("\nPress any key to continue!"));
            try {
                this.input.read(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.nextQuestion();
            this.render();
        } else {
            try {
                this.currentQuestion().cliRender();
            } catch (FormularException ignored) {} // Let it break out of the rendering loop.
        }
    }

    /**
     * Clears {@link System#out}, using either "\033[H\033[2J", if ANSI is supported, or else printing 100 new lines.
     * @see #supportsANSI()
     */
    public void clearOutput() {
        if (this.supportsANSI()) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } else {
            System.out.println("\n".repeat(100));
        }
    }

    /**
     * Checks if the current environment supports ANSI. It does that the following way:<br>
     * If the operating system is not windows, it will always return true. <br>
     * If it's windows, it will return if the system environmental variable "TERM" is set to xterm.
     * @return If the current environment supports ANSI or not.
     */
    public boolean supportsANSI() {
        if (System.getProperty("os.name").startsWith("Win")) {
            String term = System.getenv("TERM");
            return term != null && term.contains("xterm");
        } else {
            return true;
        }
    }
}
