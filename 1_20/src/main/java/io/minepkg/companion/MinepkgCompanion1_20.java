package io.minepkg.companion;

import io.minepkg.companion.mixin.ServerMetadataAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import java.util.Optional;

public class MinepkgCompanion1_20 implements ModInitializer {
	@Override
	public void onInitialize() {
		if (GlueMixinPlugin.test("minecraft", "<1.19.4"))
			return;
		if (!FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString().startsWith("1.20")) return;
		MinepkgCompanion.INSTANCE.versionSetter = (metadata, version) -> {
			((ServerMetadataAccessor) (Object) metadata).setVersion(Optional.of(version));
		};
	}
}