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
@SuppressWarnings("RedundantCollectionOperation")
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
		String mixinName = mixinClassName.substring(mixinClassName.lastIndexOf(".mixin") + ".mixin".length() + 1);
		boolean shouldApply = false;

		// common
		if (List.of("ServerMetadataMixin").contains(mixinName)) {
			shouldApply = true;
		}

		// 1_17
		if (List.of("MixinClientTitleScreen1_17").contains(mixinName)) {
			shouldApply = test("minecraft", ">=1.17 <1.19");
		}

		// 1_19
		if (List.of("MixinClientTitleScreen").contains(mixinName)) {
			shouldApply = test("minecraft", "~1.19");
		}

		// 1_19_3
		if (List.of("MixinClientQueryResponseS2CPacket", "MixinServerQueryResponseS2CPacket", "ServerMetadataDeserializerMixin").contains(mixinName)) {
			shouldApply = test("minecraft", "<1.19.4");
		}

		// 1_19_4
		if (List.of("ServerMetadataMixin2").contains(mixinName)) {
			shouldApply = test("minecraft", ">=1.19.4-pre1 < 1.20.3") || test("minecraft", "23w07a");
		}
		if (List.of("QueryResponseS2CPacketMixin", "ServerMetadataAccessor").contains(mixinName)) {
			shouldApply = test("minecraft", ">=1.19.4-pre1") || test("minecraft", "23w07a");
		}
	

		// 1_20
		if (List.of("MixinClientTitleScreen1_20").contains(mixinName)) { // Add condition for 1.20 client mixin
			shouldApply = test("minecraft", ">=1.20.0") && test("minecraft", "<1.20.3");
		}

		// 1_20_3
		if (List.of("MixinClientTitleScreen1_20_3").contains(mixinName)) { // Add condition for 1.20.3 client mixin
			shouldApply = test("minecraft", ">=1.20.3") && test("minecraft", "<1.20.5");
		}
		if (List.of("ServerMetadataMixin3").contains(mixinName)) { // Add condition for 1.20.3 client mixin
			shouldApply = test("minecraft", ">=1.20.3");
		}

		
		// 1_20_5
		if (List.of("MixinClientTitleScreen1_20_5").contains(mixinName)) { // Add condition for 1.20.5 client mixin
			shouldApply = test("minecraft", ">=1.20.5");
		}

		final String RESET = "\u001B[0m";
		final String GREEN = "\u001B[32m";
		final String RED = "\u001B[31m";
		
		LOGGER.info("{} {}", shouldApply ? GREEN + "loading" : RED + "skipping", mixinName + RESET);

		return shouldApply;
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