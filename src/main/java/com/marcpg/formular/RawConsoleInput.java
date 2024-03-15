package com.marcpg.formular;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.List;

/**
 * <b>Should not be used outside of the LibPG library, as it's not tested for other use!</b><br>
 * <br>
 * All credits for this code go to <a href="http://www.source-code.biz/snippets/java/RawConsoleInput">source-code.biz</a>!
 * All I did, was reformatting the code, to be more up-to-date.<br>
 * <br>
 * A JNA based driver for reading single characters from the console. <br>
 * This class is used for console mode programs.
 * It supports non-blocking reads of single key strokes without echo.
 * @since 0.0.8
 * @author Christian d'Heureuse (<a href="http://www.source-code.biz/snippets/java/RawConsoleInput">source-code.biz</a>)
 */
@ApiStatus.Internal
@SuppressWarnings("SpellCheckingInspection")
public final class RawConsoleInput {
    private final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    private final int invalidKey = 0xFFFE;
    private final String invalidKeyStr = String.valueOf((char) invalidKey);
    private boolean initDone;
    private boolean stdinIsConsole;
    private boolean consoleModeAltered;

    /**
     * <b>Should not be used outside of the LibPG library, as it's not tested for other use!</b><br>
     * <br>
     * Reads a character from the console without echo.
     * If the character decimal is 3, which is ^C, it will use {@link System#exit(int)}.
     * @param wait <code>true</code> to wait until an input character is available,
     *             <code>false</code> to return immediately if no character is available.
     * @return -2 if <code>wait</code> is <code>false</code> and no character is available. -1 on EOF.
     * Otherwise a Unicode/ASCII character code within the range 0 to 0xFFFF.
     * @throws IOException if there was an issue while reading from the console.
     */
    public int read(boolean wait) throws IOException {
        int c = isWindows ? readWindows(wait) : readUnix(wait);
        if (c == 3 || c == 24) {
            System.exit(0);
            return 0;
        } else {
            return c;
        }
    }

    /**
     * <b>Should not be used outside of the LibPG library, as it's not tested for other use!</b><br>
     * <br>
     * Resets console mode to normal line mode with echo. <br>
     * On Windows this method re-enables Ctrl-C processing. <br>
     * On Unix this method switches the console back to echo mode.<br>
     * {@link #read(boolean)} leaves the console in non-echo mode.
     * @throws IOException if there was an issue while switching to the normal console mode.
     */
    public void resetConsoleMode() throws IOException {
        if (isWindows) resetConsoleModeWindows();
        else resetConsoleModeUnix();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownHook));
    }

    private void shutdownHook() {
        try {
            resetConsoleMode();
        } catch (Exception ignored) {}
    }


    // =============================================================================================
    // ========================================== WINDOWS ==========================================
    // ============================= The following code is for Windows =============================
    // =============================================================================================
    private Msvcrt msvcrt;
    private Kernel32 kernel32;
    private Pointer consoleHandle;
    private int originalConsoleMode;

    private int readWindows(boolean wait) throws IOException {
        initWindows();
        if (!stdinIsConsole) {
            int c = msvcrt.getwchar();
            if (c == 0xFFFF) c = -1;
            return c;
        }
        consoleModeAltered = true;
        setConsoleMode(consoleHandle, originalConsoleMode & ~Kernel32Defs.ENABLE_PROCESSED_INPUT);

        if (!wait && msvcrt._kbhit() == 0) return -2;
        return getwch();
    }

    private int getwch() {
        int c = msvcrt._getwch();
        if (c == 0 || c == 0xE0) {
            c = msvcrt._getwch();
            if (c >= 0 && c <= 0x18FF) return 0xE000 + c;
            return invalidKey;
        }
        if (c < 0 || c > 0xFFFF) {
            return invalidKey;
        }
        return c;
    }

    private synchronized void initWindows() {
        if (initDone) return;

        msvcrt = Native.load(Msvcrt.class);
        kernel32 = Native.load(Kernel32.class);
        try {
            consoleHandle = getStdInputHandle();
            originalConsoleMode = getConsoleMode(consoleHandle);
            stdinIsConsole = true;
        } catch (IOException e) {
            stdinIsConsole = false;
        }
        if (stdinIsConsole) registerShutdownHook();

        initDone = true;
    }

    private Pointer getStdInputHandle() throws IOException {
        Pointer handle = kernel32.GetStdHandle(Kernel32Defs.STD_INPUT_HANDLE);
        if (Pointer.nativeValue(handle) == 0 || Pointer.nativeValue(handle) == Kernel32Defs.INVALID_HANDLE_VALUE) {
            throw new IOException("GetStdHandle(STD_INPUT_HANDLE) failed.");
        }
        return handle;
    }

    private int getConsoleMode(Pointer handle) throws IOException {
        IntByReference mode = new IntByReference();
        int rc = kernel32.GetConsoleMode(handle, mode);
        if (rc == 0) throw new IOException("GetConsoleMode() failed.");
        return mode.getValue();
    }

    private void setConsoleMode(Pointer handle, int mode) throws IOException {
        int rc = kernel32.SetConsoleMode(handle, mode);
        if (rc == 0) throw new IOException("SetConsoleMode() failed.");
    }

    private void resetConsoleModeWindows() throws IOException {
        if (!initDone || !stdinIsConsole || !consoleModeAltered) return;
        setConsoleMode(consoleHandle, originalConsoleMode);
        consoleModeAltered = false;
    }

    private interface Msvcrt extends Library {
        int _kbhit();

        int _getwch();

        int getwchar();
    }

    private static class Kernel32Defs {
        static final int STD_INPUT_HANDLE = -10;
        static final long INVALID_HANDLE_VALUE = (Native.POINTER_SIZE == 8) ? -1 : 0xFFFFFFFFL;
        static final int ENABLE_PROCESSED_INPUT = 0x0001;
    }

    private interface Kernel32 extends Library {
        int GetConsoleMode(Pointer hConsoleHandle, IntByReference lpMode);

        int SetConsoleMode(Pointer hConsoleHandle, int dwMode);

        Pointer GetStdHandle(int nStdHandle);
    }


    // ============================================================================================
    // =========================================== UNIX ===========================================
    // ================== The following code is for UNIX-based operating systems ==================
    // ============================================================================================
    private final int stdinFd = 0;
    private Libc libc;
    private CharsetDecoder charsetDecoder;
    private Termios originalTermios;
    private Termios rawTermios;
    private Termios intermediateTermios;

    private int readUnix(boolean wait) throws IOException {
        initUnix();
        if (!stdinIsConsole) return readSingleCharFromByteStream();
        consoleModeAltered = true;
        setTerminalAttrs(rawTermios);
        try {
            if (!wait && System.in.available() == 0) return -2;
            return readSingleCharFromByteStream();
        } finally {
            setTerminalAttrs(intermediateTermios);
        }
    }

    private @NotNull Termios getTerminalAttrs() throws IOException {
        Termios termios = new Termios();
        try {
            int rc = libc.tcgetattr(stdinFd, termios);
            if (rc != 0) throw new RuntimeException("tcgetattr() failed.");
        } catch (LastErrorException e) {
            throw new IOException("tcgetattr() failed.", e);
        }
        return termios;
    }

    private void setTerminalAttrs(Termios termios) throws IOException {
        try {
            int rc = libc.tcsetattr(stdinFd, LibcDefs.TCSANOW, termios);
            if (rc != 0) throw new RuntimeException("tcsetattr() failed.");
        } catch (LastErrorException e) {
            throw new IOException("tcsetattr() failed.", e);
        }
    }

    private int readSingleCharFromByteStream() throws IOException {
        byte[] inBuf = new byte[4];
        int inLen = 0;
        while (true) {
            if (inLen >= inBuf.length) return invalidKey;
            int b = System.in.read();
            if (b == -1) return -1;
            inBuf[inLen++] = (byte) b;
            int c = decodeCharFromBytes(inBuf, inLen);
            if (c != -1) return c;
        }
    }

    private synchronized int decodeCharFromBytes(byte[] inBytes, int inLen) {
        charsetDecoder.reset();
        charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
        charsetDecoder.replaceWith(invalidKeyStr);
        ByteBuffer in = ByteBuffer.wrap(inBytes, 0, inLen);
        CharBuffer out = CharBuffer.allocate(1);
        charsetDecoder.decode(in, out, false);
        if (out.position() == 0) return -1;
        return out.get(0);
    }

    private synchronized void initUnix() throws IOException {
        if (initDone) return;
        libc = Native.load("c", Libc.class);
        stdinIsConsole = libc.isatty(stdinFd) == 1;
        charsetDecoder = Charset.defaultCharset().newDecoder();
        if (stdinIsConsole) {
            originalTermios = getTerminalAttrs();
            rawTermios = new Termios(originalTermios);
            rawTermios.c_lflag &= ~(LibcDefs.ICANON | LibcDefs.ECHO | LibcDefs.ECHONL | LibcDefs.ISIG);
            intermediateTermios = new Termios(rawTermios);
            intermediateTermios.c_lflag |= LibcDefs.ICANON;
            registerShutdownHook();
        }
        initDone = true;
    }

    private void resetConsoleModeUnix() throws IOException {
        if (!initDone || !stdinIsConsole || !consoleModeAltered) return;
        setTerminalAttrs(originalTermios);
        consoleModeAltered = false;
    }

    /** Internal - Should not be used outside of this class! */
    protected static class Termios extends Structure {
        /** Internal - Should not be used outside of this class! */
        public int c_iflag;

        /** Internal - Should not be used outside of this class! */
        public int c_oflag;

        /** Internal - Should not be used outside of this class! */
        public int c_cflag;
        
        /** Internal - Should not be used outside of this class! */
        public int c_lflag;

        /** Internal - Should not be used outside of this class! */
        public byte c_line;

        /** Internal - Should not be used outside of this class! */
        public byte[] filler = new byte[64];

        @Override
        protected @NotNull List<String> getFieldOrder() {
            return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "filler");
        }

        Termios() {}

        Termios(@NotNull Termios t) {
            c_iflag = t.c_iflag;
            c_oflag = t.c_oflag;
            c_cflag = t.c_cflag;
            c_lflag = t.c_lflag;
            c_line = t.c_line;
            filler = t.filler.clone();
        }
    }

    private static class LibcDefs {
        static final int ISIG = 1;
        static final int ICANON = 2;
        static final int ECHO = 8;
        static final int ECHONL = 64;
        static final int TCSANOW = 0;
    }

    private interface Libc extends Library {
        int tcgetattr(int fd, Termios termios) throws LastErrorException;

        int tcsetattr(int fd, int opt, Termios termios) throws LastErrorException;

        int isatty(int fd);
    }
}
