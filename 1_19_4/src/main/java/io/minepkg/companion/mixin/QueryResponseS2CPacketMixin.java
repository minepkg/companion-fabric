package io.minepkg.companion.mixin;

import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.MinepkgServerMetadata;
import io.minepkg.companion.events.EventServerQueryResponse;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QueryResponseS2CPacket.class)
public abstract class QueryResponseS2CPacketMixin {
	@Shadow @Final
	private ServerMetadata metadata;

	@Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
	private void parseMinepkgMetadata(PacketByteBuf buf, CallbackInfo ci) {
		EventServerQueryResponse.onServerQueryResponse((MinepkgServerMetadata) (Object) metadata, metadata);
	}

	@Inject(method = "write", at = @At("HEAD"))
	public void addMinepkgMetadata(PacketByteBuf packetByteBuf, CallbackInfo ci) {
		((MinepkgServerMetadata) (Object) metadata).setModpack(MinepkgCompanion.getModpack());
	}
}
