package io.minepkg.companion;

import com.moandjiezana.toml.Toml;
import net.fabricmc.api.ClientModInitializer;

import java.io.File;

public class MinepkgCompanion implements ClientModInitializer {
	public static final int COMPATIBLE_MANIFEST_VERSION = 0;

	public boolean opened = false;
	public static MinepkgCompanion INSTANCE;

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		INSTANCE = this;
		System.out.println("Started the minepkg companion.");
	}

	/**
	 * Checks for and parses the minepkg.toml file.
	 * @return The parsed TOML from the minepkg.toml file. Returns null when the file doesn't exist.
	 */
	public static Toml getToml () {
		File file = new File("./minepkg.toml");

		if (!file.exists()) {
			// We're not playing a modpack
			return null;
		}

		return new Toml().read(file);
	}

	/**
	 * Gets the modpack that's being ran.
	 * @return The modpack. Returns null when we aren't running a minepkg modpack.
	 */
	public static Modpack getModpack () {
		Toml manifest = MinepkgCompanion.getToml();

		if (manifest == null) {
			// If the manifest doesn't exist, we don't have a modpack
			return null;
		}

		Long manifestVersion = manifest.getLong("manifestVersion");
		String type = manifest.getString("package.type");
		String modpackName = manifest.getString("package.name");
		String modpackVersion = manifest.getString("package.version");

		if (manifestVersion == null || manifestVersion != COMPATIBLE_MANIFEST_VERSION) {
			// Invalid manifest version
			return null;
		}

		if (type == null || !type.equalsIgnoreCase("modpack") ||
			modpackName == null || modpackVersion == null) {
			// Invalid manifest
			return null;
		}

		return new Modpack(modpackName, modpackVersion, "fabric");
	}
}
