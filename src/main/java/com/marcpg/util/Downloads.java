package com.marcpg.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Provides utility methods for downloading files in various ways. <br>
 * Please keep in mind that these methods won't reach the speeds of
 * very optimized download methods that web browsers for example use.
 * @since 0.0.5
 * @author MarcPG1905
 */
public class Downloads {
    /**
     * The default buffer size that's used in {@link #download(URL, File)},
     * if the buffer size isn't specified.
     * @see #download(URL, File)
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Downloads a file from a specified URL and saves it to a system file
     * using byte buffers, for improved speed and reliability. When the
     * connection is reset while the download is still running, it will
     * stop and throw an IO Exception.
     * @param url The file's path as a URL.
     * @param destination Where the downloaded file should be stored.
     * @throws IOException if there is an error while downloading the file, such as a connection reset.
     * @see #download(URL, File, int)
     */
    public static void download(URL url, File destination) throws IOException {
        download(url, destination, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Downloads a file from a specified URL and buffer size and saves it to
     * a system file using byte buffers, for improved speed and reliability.
     * When the connection is reset while the download is still running, it
     * will stop and throw an IO Exception. <br>
     * The buffer size impacts performance and memory usage. A higher buffer
     * size is faster in most cases, but also uses more memory.
     * @param url The file's path as a URL.
     * @param destination Where the downloaded file should be stored.
     * @param bufferSize The size of the buffer to use. Bigger buffers may be faster, but also use more memory.
     * @throws IOException if there is an error while downloading the file, such as a connection reset.
     * @see #download(URL, File)
     */
    public static void download(URL url, File destination, int bufferSize) throws IOException {
        try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[bufferSize];
            int readBytes;
            while ((readBytes = channel.read(ByteBuffer.wrap(buffer))) != -1) {
                out.write(buffer, 0, readBytes);
            }
        }
    }

    /**
     * Downloads a file from a specified URL and saves it to a system file
     * using a simple output stream without any buffers. In some cases, this
     * can turn out faster than {@link #download(URL, File)}, but in most,
     * it will not be faster, as it doesn't use buffers.
     * @param url The file's path as a URL.
     * @param destination Where the downloaded file should be stored.
     * @throws IOException if there is an error while downloading the file, such as a connection reset.
     */
    public static void simpleDownload(URL url, File destination) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        try (FileOutputStream out = new FileOutputStream(destination)) {
            out.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
