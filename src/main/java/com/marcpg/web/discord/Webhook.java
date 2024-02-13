package com.marcpg.web.discord;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Can be used to interact with Discord webhooks and send messages and embeds to them.
 * @since 0.0.4
 * @see Message
 * @see Embed
 * @author MarcPG1905
 */
public class Webhook {
    private URL url;

    /**
     * Creates a new webhook to post messages to.
     * @param webhookUrl The Discord webhook URL to post all messages to.
     */
    public Webhook(URL webhookUrl) {
        url = webhookUrl;
    }

    /**
     * Posts a raw JSON to the webhook. Should not be used, as it's unreliable. Use the post() methods instead.
     * @param messageJson The message to send as a JSON.
     * @return The http response code.
     * @throws IOException if there was an error while posting the message.
     */
    public int postRaw(String messageJson) throws IOException {
        HttpURLConnection connection = createConnection(url);
        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(messageJson);
            out.flush();
        }
        return connection.getResponseCode();
    }

    /**
     * Posts a {@link Message} to the webhook, which can contain text, embeds and attachments.
     * @param message The message to send.
     * @return The http response code.
     * @throws IOException if there was an error while posting the message.
     */
    public int post(Message message) throws IOException {
        return postRaw(message.build());
    }

    /**
     * Posts a plaintext {@link Message} to the webhook.
     * @param content The plaintext to send.
     * @return The http response code.
     * @throws IOException if there was an error while posting the message.
     */
    public int post(String content) throws IOException {
        return postRaw(new Message(content, false).build());
    }

    /**
     * Posts an empty {@link Message} with only embeds to the webhook.
     * @param embeds All embeds to be posted.
     * @return The http response code.
     * @throws IOException if there was an error while posting the message.
     */
    public int post(Embed... embeds) throws IOException {
        return postRaw(new Message(null, List.of(embeds), false).build());
    }

    /**
     * Posts an empty {@link Message} with only embeds to the webhook.
     * @param embeds A list of all embeds to be posted.
     * @return The http response code.
     * @throws IOException if there was an error while posting the message.
     */
    public int post(List<Embed> embeds) throws IOException {
        return postRaw(new Message(null, embeds, false).build());
    }

    /**
     * Gets the URL that's posted to.
     * @return The Discord webhook URL that's posted to.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Change the URL that's posted to. <br>
     * Generally not recommended and has no intended use case.
     * @param url The new Discord webhook URL
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Formats a {@link String} to be compatible with JSON, by escaping backslashes, normal slashes and
     * quotation marks. Will run into issues if the input already has these characters escaped.
     * @param unescaped The string or json string without any escaped characters.
     * @return A fully escaped and JSON-compatible string.
     */
    public static String escapeJson(String unescaped) {
        return unescaped
                .replace("\\", "\\\\") // Backslashes (\)
                .replace("/", "\\/") // Slashes (/)
                .replace("\"", "\\\""); // Quotation Marks (")
    }

    private static HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }
}
