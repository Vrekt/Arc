package arc.command;

import org.bukkit.command.CommandSender;

/**
 * Represents a sub command of {@link CommandArc}
 */
public abstract class ArcSubCommand {

    /**
     * Execute this sub command
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    protected abstract void execute(CommandSender sender, String[] arguments);

}
