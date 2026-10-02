package com.cursedcraft;

/** Simple on/off switches. Flip these and rebuild (a real config file can come later). */
public final class CursedConfig {
    private CursedConfig() {}

    public static boolean EAT_ITEMS = true;
    public static boolean EAT_MOBS = true;
    public static boolean CURSED_DEATH_DROPS = true;
    /** Every block you break drops a random item instead of itself. Destructive, so off by default. */
    public static boolean CHAOS_BLOCK_DROPS = false;
}
