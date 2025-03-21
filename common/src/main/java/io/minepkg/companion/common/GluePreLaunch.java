package io.minepkg.companion.common;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;

import static io.minepkg.companion.common.GlueMixinPlugin.testJava;

public class GluePreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		// pretty much the earliest point where Fabric Loader and Mixin are set but the game is not yet started
		// do not load any Minecraft classes and classes loading Minecraft classes here! there be dragons if you do!

		// here we load each mixin config only when the minimum required Java version satisfies the config's compatibilityLevel

		GlueMixinPlugin.LOGGER.debug("adding mixin configs...");

		if (testJava(">=21")) {
			addMixinConfig("minepkg-companion.1_20_5.mixins.json");
		}

		if (testJava(">=17")) {
			addMixinConfig("minepkg-companion.1_20_3.mixins.json");
			addMixinConfig("minepkg-companion.1_20.mixins.json");
			addMixinConfig("minepkg-companion.1_19_4.mixins.json");
			addMixinConfig("minepkg-companion.1_19.mixins.json");
			addMixinConfig("minepkg-companion.common1_19_4.mixins.json");
		}

		if (testJava(">=16")) {
			addMixinConfig("minepkg-companion.1_17.mixins.json");
		}

		if (testJava(">=8")) {
			addMixinConfig("minepkg-companion.1_16.mixins.json");
			addMixinConfig("minepkg-companion.common.mixins.json");
		}
	}

	private static void addMixinConfig(String configFile) {
		GlueMixinPlugin.LOGGER.debug("{}", configFile);
		Mixins.addConfiguration(configFile);
	}
}
