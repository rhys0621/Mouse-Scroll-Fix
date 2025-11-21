package me.rhys.fix;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Scroll Fix");

    @Override
    public void onInitialize() {
        LOGGER.info("Mod Initialized!");
    }
}
