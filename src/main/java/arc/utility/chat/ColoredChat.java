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

    private final StringBuilder builder = new StringBuilder();

    private ColoredChat(CommandSender sender) {
        this.sender = sender;
    }

    public ColoredChat setMainColor(ChatColor color) {
        this.mainColor = color;
        return this;
    }

    public ColoredChat setParameterColor(ChatColor color) {
        this.parameterColor = color;
        return this;
    }

    public ColoredChat message(String message) {
        builder.append(mainColor).append(message);
        return this;
    }

    public ColoredChat message(String message, ChatColor... colors) {
        for (ChatColor color : colors) {
            builder.append(color);
        }
        builder.append(message);
        return this;
    }

    public ColoredChat messageIf(boolean condition, String message) {
        if (condition) builder.append(mainColor).append(message);
        return this;
    }

    public ColoredChat parameter(String parameter) {
        builder.append(parameterColor).append(parameter);
        return this;
    }

    public ColoredChat parameterIf(boolean condition, String parameter) {
        if (condition) builder.append(parameterColor).append(parameter);
        return this;
    }

    public void send() {
        ChatUtil.sendMessage(sender, builder.toString());
    }

}
