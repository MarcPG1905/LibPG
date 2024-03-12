package com.marcpg.web.discord;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents a Discord embed that can be converted into a raw JSON.
 * @see Webhook
 * @see Message
 * @since 0.0.4
 * @author MarcPG1905
 */
public class Embed {
    /** A completely empty {@link Embed} object. Same as {@link #Embed() new Embed()}. */
    public static final Embed EMPTY = new Embed();

    /** The {@link DateTimeFormatter} that's used for converting dates to embed timestamp dates. */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.MSS'Z'");

    private Author author;
    private String title;
    private URL titleLink;
    private String description;
    private Color color;
    private List<Field> fields;
    private URL thumbnailUrl;
    private OffsetDateTime timestamp;
    private Footer footer;

    /**
     * Creates a new embed with all values as parameters.
     * @param author The embed's {@link Author author information}.
     * @param title The embed's title.
     * @param titleLink The embed's link, which gets opened when clicking the title.
     * @param description The embed's plain text description, which appears under the title.
     * @param color The embed's color, which appears as the bar on the left side.
     * @param fields A list of all {@link Field}s that are in the embed.
     * @param thumbnailUrl The small image on the top right corner or bottom.
     * @param timestamp The time shown besides the footer, but is still a separate setting.
     * @param footer Tge embed's {@link Footer footer information}.
     */
    public Embed(Author author, String title, URL titleLink, String description, Color color, List<Field> fields, URL thumbnailUrl, OffsetDateTime timestamp, Footer footer) {
        this.author = author;
        this.title = title;
        this.titleLink = titleLink;
        this.description = description;
        this.color = color;
        this.fields = fields;
        this.thumbnailUrl = thumbnailUrl;
        this.timestamp = timestamp;
        this.footer = footer;
    }

    /**
     * Creates a minimal embed with only the most basic stuff, so title, description, color and fields.
     * @param title The embed's title.
     * @param description The embed's plain text description, which appears under the title.
     * @param color The embed's color, which appears as the bar on the left side.
     * @param fields A list of all {@link Field}s that are in the embed.
     */
    public Embed(String title, String description, Color color, List<Field> fields) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.fields = fields;
    }

    /**
     * Creates a very minimal and plain embed with only fields.
     * @param fields A list of all {@link Field}s that are in the embed.
     */
    public Embed(Field... fields) {
        this.fields = List.of(fields);
    }

    /** Creates a completely empty embed. Same as {@link #EMPTY Embed.EMPTY}. */
    public Embed() {}

    /**
     * Formats the embed into a compact representation in JSON, which is required for sending it to the Discord API.
     * @return The formatted JSON representation.
     */
    public String build() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Cannot build an empty embed!");
        } else if (this.description != null && this.description.length() > 4096) {
            throw new IllegalStateException("Cannot build an embed with a description longer than 4096 characters!");
        } else if (this.fields != null && this.fields.size() > 25) {
            throw new IllegalStateException("Cannot build an embed with more than 25 fields!");
        } else {
            StringBuilder builder = new StringBuilder("{");

            if (this.title != null && !this.title.isEmpty())
                builder.append("\"title\":\"").append(this.title).append("\",");

            if (this.description != null && !this.description.isEmpty())
                builder.append("\"description\":\"").append(this.description).append("\",");

            if (this.titleLink != null)
                builder.append("\"url\":\"").append(this.titleLink).append("\",");

            builder.append("\"color\":").append(this.color == null ? "null" : colorToDecimal(this.color)).append(",");

            if (this.fields != null && !this.fields.isEmpty())
                builder.append("\"fields\":[").append(String.join(",", this.fields.stream().map(Field::build).toList())).append("],");

            if (this.author != null && !this.author.isEmpty())
                builder.append("\"author\":").append(this.author.build()).append(",");

            if (this.footer != null && !this.footer.isEmpty())
                builder.append("\"footer\":").append(this.footer.build()).append(",");

            if (this.timestamp != null)
                builder.append("\"timestamp\":\"").append(this.timestamp.format(TIMESTAMP_FORMATTER)).append("\",");

            if (this.thumbnailUrl != null)
                builder.append("\"thumbnail\":{\"url\":\"").append(this.thumbnailUrl).append("\"},");

            builder.deleteCharAt(builder.lastIndexOf(",")).append("}");
            return builder.toString();
        }
    }

    /**
     * Clears the whole embed by setting all values to null.
     * @return A reference to this object.
     */
    public Embed clear() {
        this.author = null;
        this.title = null;
        this.titleLink = null;
        this.description = null;
        this.color = null;
        this.fields = null;
        this.thumbnailUrl = null;
        this.timestamp = null;
        this.footer = null;
        return this;
    }

    /**
     * Clears the whole embed while trying to avoid settings values to null.
     * @return A reference to this object.
     */
    public Embed clearNullAvoiding() {
        this.author = new Author("", null, null);
        this.title = "";
        this.titleLink = null;
        this.description = "";
        this.color = null;
        this.fields = List.of();
        this.thumbnailUrl = null;
        this.timestamp = null;
        this.footer = new Footer("", null);
        return this;
    }

    /**
     * Checks if the embed is empty. This doesn't only include if the values are {@code null}, but also if they are empty/blank.
     * @return {@code true} if the embed is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return (this.author == null || this.author.isEmpty()) &&
                (this.title == null || this.title.isBlank()) && this.titleLink == null &&
                (this.description == null || this.description.isBlank()) && this.color == null &&
                (this.fields == null || this.fields.isEmpty() || this.fields.stream().allMatch(Field::isEmpty)) && this.thumbnailUrl == null && this.timestamp == null &&
                (this.footer == null || this.footer.isEmpty());
    }

    /**
     * Gets the embed's author.
     * @return The embed's {@link Author}.
     */
    public Author getAuthor() {
        return this.author;
    }

    /**
     * Sets the embed's author.
     * @param author The embed's author information.
     * @return A reference to this object.
     */
    public Embed setAuthor(Author author) {
        this.author = author;
        return this;
    }

    /**
     * Gets the embed's title.
     * @return The embed's title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the embed's title.
     * @param title The embed's title.
     * @return A reference to this object.
     */
    public Embed setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Gets the embed's title link.
     * @return The embed's title link that opens when clicking the title.
     */
    public URL getTitleLink() {
        return this.titleLink;
    }

    /**
     * Sets the embed's title link.
     * @param titleLink The embed's title link that opens when clicking the title.
     * @return A reference to this object.
     */
    public Embed setTitleLink(URL titleLink) {
        this.titleLink = titleLink;
        return this;
    }

    /**
     * Sets the embed's title and the title link.
     * @param title The embed's title link that opens when clicking the title.
     * @param titleLink The embed's title.
     * @return A reference to this object.
     */
    public Embed setTitleWithLink(String title, URL titleLink) {
        this.title = title;
        this.titleLink = titleLink;
        return this;
    }

    /**
     * Gets the embed's description, which is the big text below the title.
     * @return The embed's description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the embed's description, which is the big text below the title. <br>
     * Can use most of the markdown formatting.
     * @param description The embed's description.
     * @return A reference to this object.
     */
    public Embed setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Gets the embed's color.
     * @return The embed's color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the embed's color.
     * @param color The embed's color.
     * @return A reference to this object.
     */
    public Embed setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Gets all of the embed's field.
     * @return A list of all fields.
     */
    public List<Field> getFields() {
        return this.fields;
    }

    /**
     * Sets all of the embed's field.
     * @param fields All new fields for this embed.
     * @return A reference to this object.
     */
    public Embed setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    /**
     * Appends a new field to the end of the fields.
     * @param field The new fields for this embed.
     * @return A reference to this object.
     */
    public Embed addField(Field... field) {
        this.fields.addAll(List.of(field));
        return this;
    }

    /**
     * Appends a new field to the end of the fields.
     * @param name The name/title of the field.
     * @param value The plaintext that appears below the name and holds the main information.
     * @param inline If the field should be inline with other fields.
     * @return A reference to this object.
     */
    public Embed addField(String name, String value, boolean inline) {
        this.fields.add(new Field(name, value, inline));
        return this;
    }

    /**
     * Removes a field based on the object.
     * @param field The removed fields from this embed.
     * @return A reference to this object.
     */
    public Embed removeField(Field... field) {
        this.fields.removeAll(List.of(field));
        return this;
    }

    /**
     * Removes a field based on the field's name.
     * @param fieldName The removed field's name from this embed.
     * @return A reference to this object.
     */
    public Embed removeField(String fieldName) {
        this.fields.removeIf(field -> field.name.equals(fieldName));
        return this;
    }

    /**
     * Gets the embed's thumbnail image URL.
     * @return The URL of the thumbnail, which is the small image at the upper right corner or the bottom.
     */
    public URL getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    /**
     * Sets the embed's thumbnail image URL.
     * @param thumbnailUrl The embed's thumbnail image URL.
     * @return A reference to this object.
     */
    public Embed setThumbnailUrl(URL thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    /**
     * Gets the embed's footer.
     * @return The embed's {@link Footer footer information}.
     */
    public Footer getFooter() {
        return this.footer;
    }

    /**
     * Sets the embed's footer.
     * @param footer The embed's footer.
     * @return A reference to this object.
     */
    public Embed setFooter(Footer footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Converts a {@link Color AWT Color} to a decimal integer, using some simple binary shifting.
     * @param color The color to convert.
     * @return The converted decimal integer.
     */
    public static int colorToDecimal(@NotNull Color color) {
        return (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue();
    }

    /**
     * Represents the author information of an embed.
     * @param name The author's name in plaintext.
     * @param iconUrl The author's little icon or profile picture.
     * @param url The author URL.
     */
    public record Author(String name, URL iconUrl, URL url) {
        /**
         * Checks if the author is empty. This doesn't only include if the values are {@code null}, but also if they are empty/blank.
         * @return {@code true} if the author is empty, {@code false} otherwise.
         */
        public boolean isEmpty() {
            return this.name.isBlank() && this.iconUrl == null && this.url == null;
        }

        /**
         * Formats the author into a compact representation in JSON, which is required for sending it to the Discord API.
         * @return The formatted JSON representation.
         */
        public String build() {
            if (this.isEmpty()) return "";
            StringBuilder builder = new StringBuilder("{");

            if (!this.name.isEmpty())
                builder.append("\"name\":\"").append(this.name).append("\",");

            if (this.url != null)
                builder.append("\"url\":\"").append(this.url).append("\",");

            if (this.iconUrl != null)
                builder.append("\"icon_url\":\"").append(this.iconUrl).append("\",");

            builder.deleteCharAt(builder.lastIndexOf(",")).append("}");
            return builder.toString();
        }
    }

    /**
     * Represents the footer information of an embed.
     * @param text The plaintext that on the footer. Can be called the main component of the footer.
     * @param iconUrl The little mini-icon that appears besides the footer.
     */
    public record Footer(String text, URL iconUrl) {
        /**
         * Checks if the footer is empty. This doesn't only include if the values are {@code null}, but also if they are empty/blank.
         * @return {@code true} if the footer is empty, {@code false} otherwise.
         */
        public boolean isEmpty() {
            return (this.text == null || this.text.isBlank()) && this.iconUrl == null;
        }

        /**
         * Formats the footer into a compact representation in JSON, which is required for sending it to the Discord API.
         * @return The formatted JSON representation.
         */
        public String build() {
            if (this.isEmpty()) return "";
            StringBuilder builder = new StringBuilder("{");

            if (this.text != null && !this.text.isEmpty())
                builder.append("\"text\":\"").append(this.text).append("\",");

            if (this.iconUrl != null)
                builder.append("\"icon_url\":\"").append(this.iconUrl).append("\",");

            builder.deleteCharAt(builder.lastIndexOf(",")).append("}");
            return builder.toString();
        }
    }

    /**
     * Represents a field of an embed.
     * @param name The name/title of the field.
     * @param value The plaintext that appears below the name and holds the main information.
     * @param inline If the field should be inline with other fields.
     */
    public record Field(String name, String value, boolean inline) {
        /**
         * Checks if the field's strings are blank.
         * @return {@code true} if the field is blank, {@code false} otherwise.
         */
        public boolean isEmpty() {
            return this.name.isBlank() && this.value.isBlank();
        }

        /**
         * Formats the field into a compact representation in JSON, which is required for sending it to the Discord API.
         * @return The formatted JSON representation.
         */
        public @NotNull String build() {
            return this.isEmpty() ? "" : "{\"name\":\"" + this.name + "\",\"value\":\"" + this.value + "\",\"inline\":" + this.inline + "}";
        }
    }
}


