package io.minepkg.companion.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.spongepowered.asm.mixin.Mixins;

import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Map;

import static io.minepkg.companion.common.GlueMixinPlugin.testJava;

public class GluePreLaunch implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		// pretty much the earliest point where Fabric Loader and Mixin are set but the game is not yet started
		// do not load any Minecraft classes and classes loading Minecraft classes here! there be dragons if you do!

		// here we load each mixin config only when the minimum required Java version satisfies the config's compatibilityLevel

		GlueMixinPlugin.LOGGER.debug("adding mixin configs...");

		CustomValue.CvArray mixinConfigs;
		FileSystem fileSystem;

		try {
			ModContainer modContainer = FabricLoader.getInstance().getModContainer("minepkg-companion").get();
			mixinConfigs = modContainer.getMetadata().getCustomValue("glue").getAsObject().get("mixins").getAsArray();

			fileSystem = modContainer.getRootPaths().get(0).getFileSystem();
		} catch (Exception e) {
			throw new AssertionError("couldn't get mixin configs", e);
		}

		for (CustomValue customValue : mixinConfigs) {
			String mixinConfig;

			try {
				mixinConfig = customValue.getAsString();
			} catch (ClassCastException e) {
				throw new AssertionError("mixin config not defined as string but as " + customValue.getType(), e);
			}

			try (Reader reader = Files.newBufferedReader(fileSystem.getPath(mixinConfig))) {
				Map<String, Object> config = new Gson().fromJson(reader, TypeToken.getParameterized(Map.class, String.class, Object.class).getType());

				String compatibilityLevel = (String) config.get("compatibilityLevel");
				int javaVersion = Integer.parseInt(compatibilityLevel.substring("JAVA_".length()));

				boolean shouldAdd = testJava(">=" + javaVersion);

				GlueMixinPlugin.LOGGER.debug("{} config {} (Java >= {})", shouldAdd ? "adding  " : "skipping", mixinConfig, javaVersion);

				if (shouldAdd) {
					Mixins.addConfiguration(mixinConfig);
				}
			} catch (Exception e) {
				throw new AssertionError("couldn't parse mixin config " + mixinConfig, e);
			}
		}
	}
}
