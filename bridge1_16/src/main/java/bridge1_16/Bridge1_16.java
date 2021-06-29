package bridge1_16;

import bridge.Bridge;
import bridge.Version;
import bridge1_16.api.BukkitAccess;
import bridge1_16.block.BlockAccess;
import bridge1_16.materials.MaterialAccess;

/**
 * Current bridge for 1.16
 */
public final class Bridge1_16 implements Bridge {

    private static final bridge.material.MaterialAccess MATERIAL_ACCESS = new MaterialAccess();
    private static final bridge.block.BlockAccess BLOCK_ACCESS = new BlockAccess();
    private static final bridge.api.BukkitAccess BUKKIT_ACCESS = new BukkitAccess();

    @Override
    public Version getVersion() {
        return Version.VERSION_1_12;
    }

    @Override
    public bridge.block.BlockAccess getBlockAccess() {
        return BLOCK_ACCESS;
    }

    @Override
    public bridge.material.MaterialAccess getMaterialAccess() {
        return MATERIAL_ACCESS;
    }

    @Override
    public bridge.api.BukkitAccess getBukkitAccess() {
        return BUKKIT_ACCESS;
    }

}
