package arc.utility.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Colored chat utility.
 */
public final class ColoredChat {

    public static ColoredChat forRecipient(CommandSender sender) {
        return new ColoredChat(sender);
    }

    /**
     * The recipient
     */
    private final CommandSender sender;

    /**
     * Main chat color.
     */
    private ChatColor mainColor, parameterColor;

    /**
     * The builder
     */
    private final StringBuilder builder = new StringBuilder();

    private ColoredChat(CommandSender sender) {
        this.sender = sender;
    }

    /**
     * Set the main text color
     *
     * @param color the color
     * @return this
     */
    public ColoredChat setMainColor(ChatColor color) {
        this.mainColor = color;
        return this;
    }

    /**
     * Set the color to use when parameters are added
     *
     * @param color the color
     * @return this
     */
    public ColoredChat setParameterColor(ChatColor color) {
        this.parameterColor = color;
        return this;
    }

    /**
     * Append a message
     *
     * @param message the message
     * @return this
     */
    public ColoredChat message(String message) {
        builder.append(mainColor).append(message);
        return this;
    }

    /**
     * Append a message with multiple colors
     *
     * @param message the message
     * @param colors  the colors
     * @return this
     */
    public ColoredChat message(String message, ChatColor... colors) {
        for (ChatColor color : colors) {
            builder.append(color);
        }
        builder.append(message);
        return this;
    }

    /**
     * Append a message if the provided {@code condition} is {@code true}
     *
     * @param condition the condition
     * @param message   the message
     * @return this
     */
    public ColoredChat messageIf(boolean condition, String message) {
        if (condition) builder.append(mainColor).append(message);
        return this;
    }

    /**
     * Append a parameter
     *
     * @param parameter the parameter
     * @return this
     */
    public ColoredChat parameter(String parameter) {
        builder.append(parameterColor).append(parameter);
        return this;
    }

    /**
     * Append a parameter if the provided {@code condition} is {@code true}
     *
     * @param condition the condition
     * @param parameter the parameter
     * @return this
     */
    public ColoredChat parameterIf(boolean condition, String parameter) {
        if (condition) builder.append(parameterColor).append(parameter);
        return this;
    }

    /**
     * Send the message.
     */
    public void send() {
        sender.sendMessage(builder.toString());
    }

}
