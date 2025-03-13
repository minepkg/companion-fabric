package io.minepkg.companion.common.mixin;

import io.minepkg.companion.common.MinepkgServerMetadata;
import io.minepkg.companion.common.Modpack;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerMetadata.class)
public abstract class ServerMetadataMixin implements MinepkgServerMetadata {
	public Modpack minepkgModpack;

	public void setModpack(Modpack modpack) {
		minepkgModpack = modpack;
	}

	public Modpack getModpack() {
		return minepkgModpack;
	}
}
