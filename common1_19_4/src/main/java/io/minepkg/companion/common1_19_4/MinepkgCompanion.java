package io.minepkg.companion.common1_19_4;

import io.minepkg.companion.common1_19_4.mixin.ServerMetadataAccessor;

import java.util.Optional;

public class MinepkgCompanion {
	public static void init() {
		io.minepkg.companion.common.MinepkgCompanion.INSTANCE.versionSetter = (metadata, version) -> {
			((ServerMetadataAccessor) (Object) metadata).setVersion(Optional.of(version));
		};
	}
}
