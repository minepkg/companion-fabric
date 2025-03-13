package io.minepkg.companion.common1_19_4;

import io.minepkg.companion.common.GlueMixinPlugin;
import io.minepkg.companion.common1_19_4.mixin.ServerMetadataAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

import java.util.Optional;

public class MinepkgCompanion implements ClientModInitializer, DedicatedServerModInitializer {
	public void init() {
		if (GlueMixinPlugin.testMinecraft(">=1.19.4")) {
			io.minepkg.companion.common.MinepkgCompanion.INSTANCE.versionSetter = (metadata, version) -> {
				io.minepkg.companion.common.MinepkgCompanion.LOGGER.info("minepkg companion for Minecraft >= 1.19.4 ready");
				((ServerMetadataAccessor) (Object) metadata).setVersion(Optional.of(version));
			};
		}
	}

	// Client/DedicatedServerModInitializer run after all ModInitializer, guaranteeing that MinepkgCompanion.INSTANCE is already set

	@Override
	public void onInitializeClient() {
		init();
	}

	@Override
	public void onInitializeServer() {
		init();
	}
}
