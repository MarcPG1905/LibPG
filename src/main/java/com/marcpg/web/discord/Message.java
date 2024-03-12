package com.marcpg.web.discord;

import java.net.URL;
import java.util.List;

/**
 * Represents a full Discord webhook message that can be converted into a raw JSON.
 * @see Webhook
 * @see Embed
 * @since 0.0.4
 * @author MarcPG1905
 */
public class Message {
    /** A completely empty {@link Message} object. Same as {@link #Message() new Message()}. */
    public static final Message EMPTY = new Message();

    private String username;
    private URL avatarUrl;
    private String threadName;
    private boolean tts;
    private String content;
    private List<Embed> embeds;
    private boolean silent;

    /**
     * Creates a new message with all settings.
     * @param threadName The channel's thread name. Only valid in forums or in text channels containing threads.
     * @param tts If the message should be sent as a text-to-speech message. Might run into issues with some other settings.
     * @param content The raw message content.
     * @param embeds A list of all embeds that should be appended to the message.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String threadName, boolean tts, String content, List<Embed> embeds, boolean silent) {
        this.threadName = threadName;
        this.tts = tts;
        this.content = content;
        this.embeds = embeds;
        this.silent = silent;
    }

    /**
     * Creates a new message with embeds.
     * @param content The raw message content.
     * @param embeds A list of all embeds that should be appended to the message.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String content, List<Embed> embeds, boolean silent) {
        this.content = content;
        this.embeds = embeds;
        this.silent = silent;
    }

    /**
     * Creates a new plaintext message.
     * @param content The raw message content.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String content, boolean silent) {
        this.content = content;
        this.silent = silent;
    }

    /**
     * Creates a new plaintext message.
     * @param content The raw message content.
     * @param tts If the message should be sent as text-to-speech.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String content, boolean tts, boolean silent) {
        this.content = content;
        this.tts = tts;
        this.silent = silent;
    }

    /**
     * Creates a new message with all settings and a custom avatar.
     * @param username The custom avatar's username.
     * @param avatarUrl The custom avatar's profile picture URL.
     * @param threadName The channel's thread name. Only valid in forums or in text channels containing threads.
     * @param tts If the message should be sent as a text-to-speech message. Might run into issues with some other settings.
     * @param content The raw message content.
     * @param embeds A list of all embeds that should be appended to the message.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String username, URL avatarUrl, String threadName, boolean tts, String content, List<Embed> embeds, boolean silent) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.threadName = threadName;
        this.tts = tts;
        this.content = content;
        this.embeds = embeds;
        this.silent = silent;
    }

    /**
     * Creates a new message with embeds and a custom avatar.
     * @param username The custom avatar's username.
     * @param avatarUrl The custom avatar's profile picture URL.
     * @param content The raw message content.
     * @param embeds A list of all embeds that should be appended to the message.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String username, URL avatarUrl, String content, List<Embed> embeds, boolean silent) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.embeds = embeds;
        this.silent = silent;
    }

    /**
     * Creates a new plaintext message with a custom avatar.
     * @param username The custom avatar's username.
     * @param avatarUrl The custom avatar's profile picture URL.
     * @param content The raw message content.
     * @param silent If the message should be silent, which means that no one will be notified.
     */
    public Message(String username, URL avatarUrl, String content, boolean silent) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.silent = silent;
    }

    /** Creates a completely empty message. Same as {@link #EMPTY Message.EMPTY}. */
    public Message() {}

    /**
     * Formats the message into a compact representation in JSON, which is required for sending it to the Discord API.
     * @return The formatted JSON representation.
     */
    public String build() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot build an empty message!");
        } else if (this.content != null && this.content.length() > 2000) {
            throw new IllegalStateException("Cannot build a message with content longer than 2000 characters!");
        } else {
            StringBuilder builder = new StringBuilder("{\"content\":");

            if (this.content != null && !this.content.isEmpty())
                builder.append("\"").append(this.content).append("\",");
            else
                builder.append("null,");

            if (this.username != null && !this.username.isEmpty())
                builder.append("\"username\":\"").append(this.username).append("\",");

            if (this.avatarUrl != null)
                builder.append("\"avatar_url\":\"").append(this.avatarUrl).append("\",");

            if (this.threadName != null)
                builder.append("\"thread_name\":\"").append(this.threadName).append("\",");

            if (this.tts)
                builder.append("\"tts\":true,");

            if (this.silent)
                builder.append("\"flags\":4096,");

            builder.append("\"embeds\":");
            if (this.embeds != null && !this.embeds.isEmpty())
                builder.append("[").append(String.join(",", this.embeds.stream().map(Embed::build).toList())).append("]");
            else
                builder.append("null");

            return builder.append("}").toString();
        }
    }

    /**
     * Clears the whole message by setting all values to null.
     * @return A reference to this object.
     */
    public Message clear() {
        this.content = null;
        this.embeds = null;
        return this;
    }

    /**
     * Clears the whole message while trying to avoid settings values to null.
     * @return A reference to this object.
     */
    public Message clearNullAvoiding() {
        this.content = "";
        this.embeds = List.of();
        return this;
    }

    /**
     * Checks if the message is empty. This doesn't only include if the values are {@code null}, but also if they are empty/blank.
     * @return {@code true} if the message is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return (this.content == null || this.content.isBlank()) && (this.embeds == null || this.embeds.isEmpty() || this.embeds.stream().allMatch(Embed::isEmpty));
    }

    /**
     * Gets the custom avatar's username.
     * @return The custom avatar's username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the custom avatar's username.
     * @param username The custom avatar's username.
     * @return A reference to this object.
     */
    public Message setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Gets the custom avatar's profile picture URL.
     * @return The custom avatar's profile picture URL.
     */
    public URL getAvatarUrl() {
        return this.avatarUrl;
    }

    /**
     * Sets the custom avatar's profile picture URL.
     * @param avatarUrl The custom avatar's profile picture URL.
     * @return A reference to this object.
     */
    public Message setAvatarUrl(URL avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    /**
     * Gets the targeted thread name.
     * @return The name of the channel's thread to post in.
     */
    public String getThreadName() {
        return this.threadName;
    }

    /**
     * Sets the targeted thread name.
     * @param threadName The name of the channel's thread to post in.
     * @return A reference to this object.
     */
    public Message setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    /**
     * Gets if the message is tts.
     * @return If the message is sent as a text-to-speech message.
     */
    public boolean isTTS() {
        return this.tts;
    }

    /**
     * Sets if the message is tts.
     * @param tts If the message is sent as a text-to-speech message.
     * @return A reference to this object.
     */
    public Message setTTS(boolean tts) {
        this.tts = tts;
        return this;
    }

    /**
     * Gets the message's content.
     * @return The message's raw content.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Sets the message's content.
     * @param content The raw message content.
     * @return A reference to this object.
     */
    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Gets the attached embeds.
     * @return A list of all embeds.
     */
    public List<Embed> getEmbeds() {
        return this.embeds;
    }

    /**
     * Sets the attached embeds.
     * @param embeds A list of all embeds.
     * @return A reference to this object.
     */
    public Message setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
        return this;
    }

    /**
     * Gets the attached embeds.
     * @param embeds All embeds.
     * @return A reference to this object.
     */
    public Message setEmbeds(Embed... embeds) {
        this.embeds = List.of(embeds);
        return this;
    }

    /**
     * Appends a new embed to the message.
     * @param embeds All embeds to be appended.
     * @return A reference to this object.
     */
    public Message addEmbeds(Embed... embeds) {
        this.embeds.addAll(List.of(embeds));
        return this;
    }

    /**
     * Appends a new embed to the message.
     * @param embeds A list of all embeds to be appended.
     * @return A reference to this object.
     */
    public Message addEmbeds(List<Embed> embeds) {
        this.embeds.addAll(embeds);
        return this;
    }

    /**
     * Gets if the message is a silent message or not.
     * @return If the message is a silent message.
     */
    public boolean isSilent() {
        return this.silent;
    }

    /**
     * Sets if the message is a silent message or not.
     * @param silent If the message is a silent message.
     * @return A reference to this object.
     */
    public Message setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }
}
