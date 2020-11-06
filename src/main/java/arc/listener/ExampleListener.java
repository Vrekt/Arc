package arc.listener;

import arc.check.example.ExampleCheck;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

/**
 * Basic example listener
 */
public final class ExampleListener implements Listener {

    /**
     * The example check.
     */
    private final ExampleCheck check = new ExampleCheck();

    @EventHandler
    private void onBed(PlayerBedEnterEvent event) {
        check.check(event.getPlayer(), (checkResult, violationResult) -> {
            if (checkResult.failed()) {
                // Do something
            }
            if (violationResult.cancel() && checkResult.failed()) {
                // Do something
            }
        });
    }

}
