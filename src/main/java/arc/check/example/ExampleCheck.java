package arc.check.example;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckCallback;
import arc.check.result.CheckResult;
import arc.configuration.check.CheckConfigurationWriter;
import org.bukkit.entity.Player;

/**
 * A basic example/test check!
 */
public final class ExampleCheck extends Check {

    /**
     * Example value
     */
    private final double defaultExampleCheckValue;

    /**
     * An example check
     */
    public ExampleCheck() {
        super("Example-Check");

        new CheckConfigurationWriter(Arc.plugin().getConfig())
                .name(getName())
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(true)
                .banLevel(100)
                .kick(true)
                .kickLevel(50)
                .finish();

        addConfigurationValue("some-value", 1.0);
        addConfigurationValue("do-something", false);
        defaultExampleCheckValue = getValueDouble("some-value");
    }

    /**
     * Check
     *
     * @param player the player
     */
    public void check(Player player, CheckCallback callback) {
        final var result = new CheckResult();

        if (defaultExampleCheckValue == 1.0) {
            result.information("Example check value is 1.0");
            result.setFailed();
        }

        callback.onResult(result, processResult(player, result));
    }

}
