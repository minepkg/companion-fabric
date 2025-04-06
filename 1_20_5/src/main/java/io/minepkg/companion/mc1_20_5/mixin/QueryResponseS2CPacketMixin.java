package io.minepkg.companion.mc1_20_5.mixin;

import io.minepkg.companion.common.MinepkgCompanion;
import io.minepkg.companion.common.MinepkgServerMetadata;
import io.minepkg.companion.common.events.EventServerQueryResponse;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// "write" method intermediary name changed from method_11052 to method_56026 in 1.20.5
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
