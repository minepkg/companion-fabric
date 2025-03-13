package io.minepkg.companion.common;

import net.minecraft.server.ServerMetadata;

public interface ServerMetadataVersionSetter {
	void set(ServerMetadata metadata, ServerMetadata.Version version);
}
