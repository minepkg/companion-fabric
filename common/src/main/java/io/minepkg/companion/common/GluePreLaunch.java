package io.minepkg.companion.common;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;

import static io.minepkg.companion.common.GlueMixinPlugin.testMinecraft;

public class GluePreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		// pretty much the earliest point where Fabric Loader and Mixin are set but the game is not yet started
		// do not load any Minecraft classes and classes loading Minecraft classes here! there be dragons if you do!

		// here we load each mixin config only when the minimum required Java version satisfies the config's compatibilityLevel

		if (testMinecraft(">=1.20.5")) {
			// Java 21
			Mixins.addConfiguration("minepkg-companion.1_20_5.mixins.json");
		}

		if (testMinecraft(">=1.18")) {
			// Java 17
			Mixins.addConfiguration("minepkg-companion.1_20_3.mixins.json");
			Mixins.addConfiguration("minepkg-companion.1_20.mixins.json");
			Mixins.addConfiguration("minepkg-companion.1_19_4.mixins.json");
			Mixins.addConfiguration("minepkg-companion.1_19.mixins.json");
			Mixins.addConfiguration("minepkg-companion.common1_19_4.mixins.json");
		}

		if (testMinecraft(">=1.17")) {
			// Java 16
			Mixins.addConfiguration("minepkg-companion.1_17.mixins.json");
		}

		if (testMinecraft(">=1.12")) {
			// Java 8
			Mixins.addConfiguration("minepkg-companion.1_16.mixins.json");
			Mixins.addConfiguration("minepkg-companion.common.mixins.json");
		}

		GlueMixinPlugin.LOGGER.debug("added configurations");
	}
}
