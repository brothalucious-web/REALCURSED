package com.cursedcraft;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CursedCraft implements ModInitializer {
    public static final String MOD_ID = "cursedcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        EatItems.register();
        EatMobs.register();
        CursedEvents.register();
        LOGGER.info("Cursed Craft loaded. Good luck.");
    }
}
