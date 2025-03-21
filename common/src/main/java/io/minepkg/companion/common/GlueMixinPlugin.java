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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/** Applies the correct mixin for the given Minecraft version. */
@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument"})
public class GlueMixinPlugin implements IMixinConfigPlugin {
	public static final Logger LOGGER = LogManager.getLogger("glue");

	public static boolean testMinecraft(String versionRange) {
		return test("minecraft", versionRange);
	}

	public static boolean testJava(String versionRange) {
		return test("java", versionRange);
	}

	public static boolean test(String modId, String versionRange) {
		try {
			Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
			if (!container.isPresent())
				return false;

			VersionPredicate pred = VersionPredicate.parse(versionRange);
			Version version = container.get().getMetadata().getVersion();

			return pred.test(version);
		} catch (VersionParsingException e) {
			LOGGER.error("version matching failed!", e);
			return false;
		}
	}

	@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		// pattern: io.minepkg.companion.<project>.mixin.<mixin>
		List<String> parts = asList(mixinClassName.split(Pattern.quote(".")));
		String projectName = parts.get(3);
		String mixinName = parts.get(5);

		Boolean shouldApply = null;

		// 1.12: Java 8
		// 1.17: Java 16
		// 1.18: Java 17
		// 1.20.5: Java 21

		switch (projectName) {
			case "common":
				if (asList("ServerMetadataMixin").contains(mixinName)) {
					shouldApply = true;
				}
				break;
			case "mc1_16":
				if (asList("MixinClientTitleScreen").contains(mixinName) ||
					asList("MixinClientQueryResponseS2CPacket", "MixinServerQueryResponseS2CPacket", "ServerMetadataDeserializerMixin").contains(mixinName)) {
					shouldApply = testMinecraft("~1.16");
				}

				break;
			case "mc1_17":
				if (asList("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.17 <1.19");
				}

				if (asList("MixinClientQueryResponseS2CPacket", "MixinServerQueryResponseS2CPacket", "ServerMetadataDeserializerMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.17 <1.19.4");
				}
				break;
			case "mc1_19":
				if (asList("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft("~1.19");
				}
				break;
			case "common1_19_4":
				if (asList("ServerMetadataAccessor").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.19.4");
				}
				break;
			case "mc1_19_4":
				if (asList("ServerMetadataMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.19.4-pre1 <1.20.3") || testMinecraft("23w07a");
				}

				if (asList("QueryResponseS2CPacketMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.19.4-pre1") || testMinecraft("23w07a");
				}
				break;
			case "mc1_20":
				if (asList("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20 <1.20.3");
				}
				break;
			case "mc1_20_3":
				if (asList("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20.3 <1.20.5");
				}

				if (asList("ServerMetadataMixin").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20.3");
				}
				break;
			case "mc1_20_5":
				if (asList("MixinClientTitleScreen").contains(mixinName)) {
					shouldApply = testMinecraft(">=1.20.5");
				}
				break;
			default:
				throw new AssertionError(String.format("mixin plugin: project %s has no case!", projectName));
		}

		if (shouldApply == null) {
			throw new AssertionError(String.format("mixin %s from %s has no set version range!", mixinName, projectName));
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
