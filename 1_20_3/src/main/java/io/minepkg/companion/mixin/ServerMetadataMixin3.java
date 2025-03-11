package io.minepkg.companion.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.minepkg.companion.MinepkgServerMetadata;
import io.minepkg.companion.ModpackCodec;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.TextCodecs; // Import TextCodecs!
// import net.minecraft.util.dynamic.Codecs; // You can likely remove this import
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ServerMetadata.class)
public abstract class ServerMetadataMixin3 {
	// replace vanilla CODEC to add custom metadata
	@Shadow
	public static final Codec<ServerMetadata> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
				// vanilla

				TextCodecs.CODEC.optionalFieldOf("description", ScreenTexts.EMPTY).forGetter(ServerMetadata::comp_1273), // description - Using TextCodecs.CODEC!
				ServerMetadata.Players.CODEC.optionalFieldOf("players").forGetter(ServerMetadata::comp_1274), // players
				ServerMetadata.Version.CODEC.optionalFieldOf("version").forGetter(ServerMetadata::comp_1275), // version
				ServerMetadata.Favicon.CODEC.optionalFieldOf("favicon").forGetter(ServerMetadata::comp_1276), // favicon
				Codec.BOOL.optionalFieldOf("enforcesSecureChat", Boolean.valueOf(false)).forGetter(ServerMetadata::secureChatEnforced),

				// custom
				ModpackCodec.CODEC.optionalFieldOf("minepkgModpack").forGetter(metadata -> {
					MinepkgServerMetadata minepkgMetadata = (MinepkgServerMetadata) (Object) metadata;
					return Optional.ofNullable(minepkgMetadata.getModpack());
				})
			)
			.apply(instance, (description, players, version, favicon, enforcesSecureChat, minepkgModpack) -> {
				ServerMetadata metadata = new ServerMetadata(description, players, version, favicon, enforcesSecureChat);
				MinepkgServerMetadata customMetadata = (MinepkgServerMetadata) (Object) metadata;

				minepkgModpack.ifPresent(customMetadata::setModpack);
				return metadata;
			})
	);
}