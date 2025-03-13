package io.minepkg.companion.common;

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

import java.util.*;
import java.util.regex.Pattern;

/** Applies the correct mixin for the given Minecraft version. */
@SuppressWarnings("RedundantCollectionOperation")
public class GlueMixinPlugin implements IMixinConfigPlugin {
	public static final Logger LOGGER = LogManager.getLogger("glue");

	public static boolean testMinecraft(String versionRange) {
		return test("minecraft", versionRange);
	}

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
		// pattern: io.minepkg.companion.<project>.mixin.<mixin>
		List<String> parts = Arrays.asList(mixinClassName.split(Pattern.quote(".")));
		String projectName = parts.get(3);
		String mixinName = parts.get(5);

		Boolean shouldApply = null;

		switch (projectName) {
			case "common" -> {
				if (List.of("ServerMetadataMixin").contains(mixinName)) {
					shouldApply = true;
				}
			}
			case "mc1_17" -> {
				if (List.of("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.17 <1.19");
				}

				if (List.of("MixinClientQueryResponseS2CPacket", "MixinServerQueryResponseS2CPacket", "ServerMetadataDeserializerMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.17 <1.19.4");
				}
			}
			case "mc1_19" -> {
				if (List.of("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft("~1.19");
				}
			}
			case "common1_19_4" -> {
				if (List.of("ServerMetadataAccessor").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.19.4");
				}
			}
			case "mc1_19_4" -> {
				if (List.of("ServerMetadataMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.19.4-pre1 <1.20.3") || testMinecraft("23w07a");
				}

				if (List.of("QueryResponseS2CPacketMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.19.4-pre1") || testMinecraft("23w07a");
				}
			}
			case "mc1_20" -> {
				if (List.of("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20 <1.20.3");
				}
			}
			case "mc1_20_3" -> {
				if (List.of("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20.3 <1.20.5");
				}

				if (List.of("ServerMetadataMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20.3");
				}
			}
			case "mc1_20_5" -> {
				if (List.of("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20.5");
				}
			}
			default -> throw new AssertionError("mixin plugin: project %s has no case!".formatted(projectName));
		}

		if (shouldApply == null) {
			throw new AssertionError("mixin %s from %s has no set version range!".formatted(mixinName, projectName));
		}

		LOGGER.debug("{} {} {}", shouldApply ? "applying" : "skipping", projectName, mixinName);

		return shouldApply;
	}

	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() { return null; }

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() { return null; }

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
