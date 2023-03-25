package io.minepkg.companion;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Applies the correct mixin for the given Minecraft version. */
public class GlueMixinPlugin implements IMixinConfigPlugin {
	public static final Logger LOGGER = LogManager.getLogger("glue");

	public static boolean test(String modId, String versionRange) {
		try {
			Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
			if (container.isEmpty())
				return false;

			VersionPredicate pred = VersionPredicate.parse(versionRange);
			Version version = container.get().getMetadata().getVersion();

			return pred.test(version);
		} catch (VersionParsingException e) {
			LOGGER.error("version matching failed!", e);
			return false;
		}
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		LOGGER.info("trying to apply {}", mixinClassName);

		// common mixins, not version specific
		if (mixinClassName.endsWith("common")) {
			return true;
		}

		if (test("minecraft", ">=1.17 <1.19") && mixinClassName.endsWith("1_17")) {
			return true;
		}

		if (test("minecraft", ">=1.19 <1.19.4") && mixinClassName.endsWith("1_19")) {
			return true;
		}

		if (test("minecraft", ">=1.19.4") && mixinClassName.endsWith("1_19_4")) {
			return true;
		}

		LOGGER.warn("unhandled mixin {}", mixinClassName);
		return false;
	}

	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
