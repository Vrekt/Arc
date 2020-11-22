package arc.command;

import org.bukkit.command.CommandSender;

/**
 * Represents a sub command of {@link CommandArc}
 */
public abstract class ArcSubCommand {

    /**
     * The permission for this command
     */
    protected String permission;

    /**
     * Execute this sub command
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    protected abstract void execute(CommandSender sender, String[] arguments);

    /**
     * @return the permission
     */
    public String permission() {
        return permission;
    }

    /**
     * Set the permission
     *
     * @param permission permission
     */
    public void permission(String permission) {
        this.permission = permission;
    }
}
