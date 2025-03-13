package io.minepkg.companion.common1_19_4.mixin;

import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ServerMetadata.class)
public interface ServerMetadataAccessor {
	@Accessor(value = "comp_1275", remap = false) @Mutable
	void setVersion(Optional<ServerMetadata.Version> version);
}
