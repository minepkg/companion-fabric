package io.minepkg.companion;

import com.moandjiezana.toml.Toml;
import net.fabricmc.api.ModInitializer;
import java.io.File;

public class MinepkgCompanion implements ModInitializer {
	public static final int COMPATIBLE_MANIFEST_VERSION = 0;

	public boolean opened = false;
	public static MinepkgCompanion INSTANCE;

	public String fallbackManifestPath = "./minepkg.toml";
	private Modpack modpack;
	private boolean modpackCached = false;

	@Override
	public void onInitialize () {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		INSTANCE = this;
		System.out.println("minepkg companion ready");
	}

	/**
	 * Checks for and parses the minepkg.toml file.
	 * @return The parsed TOML from the minepkg.toml file. Returns null when the file doesn't exist.
	 */
	public static Toml getToml (String path) {
		File file = new File(path);

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

		// only read the manifest once
		if (INSTANCE.modpackCached) {
			return INSTANCE.modpack;
		}

		String manifestPath = System.getenv("MINEPKG_COMPANION_MANIFEST_PATH");
		if (manifestPath == null) manifestPath = INSTANCE.fallbackManifestPath;
		Toml manifest = MinepkgCompanion.getToml(manifestPath);
		// as a last resort, let's check if the manifest is in the parent directory
		if (manifest == null) manifest = MinepkgCompanion.getToml("../minepkg.toml");

		// reading done, we don't want to read again – even if the toml is not parsable/readable
		INSTANCE.modpackCached = true;

		if (manifest == null) {
			// If the manifest doesn't exist, we don't have a modpack
			return null;
		}

		Long manifestVersion = manifest.getLong("manifestVersion");
		String type = manifest.getString("package.type");
		String modpackName = manifest.getString("package.basedOn");
		// fallback to package.name (will almost never result in a working setup as that modpack has to be published in that version)
		if (modpackName == null) modpackName = manifest.getString("package.name");
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

		INSTANCE.modpack = new Modpack(modpackName, modpackVersion, "fabric");
		return INSTANCE.modpack;
	}
}
