package io.minepkg.companion;

import io.minepkg.companion.mixin.ServerMetadataAccessor;
import net.fabricmc.api.ModInitializer;

import java.util.Optional;

public class MinepkgCompanion1_20_3 implements ModInitializer {
	@Override
	public void onInitialize() {
		if (!GlueMixinPlugin.test("minecraft", ">=1.20.3"))
			return;

		MinepkgCompanion.INSTANCE.versionSetter = (metadata, version) -> {
			((ServerMetadataAccessor) (Object) metadata).setVersion(Optional.of(version));
		};
	}
}
