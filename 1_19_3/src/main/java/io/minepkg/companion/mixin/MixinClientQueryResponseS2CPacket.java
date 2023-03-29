package io.minepkg.companion.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.minepkg.companion.MinepkgServerMetadata;
import io.minepkg.companion.Modpack;
import io.minepkg.companion.ModpackDeserializer;
import io.minepkg.companion.events.EventServerQueryResponse;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Modifies the ClientQueryResponse packet class on the client's side
// To parse minepkg metadata from ClientQueryResponses (Server List Ping)
@Mixin(QueryResponseS2CPacket.class)
public abstract class MixinClientQueryResponseS2CPacket {
    // Replace vanilla GSON with one that can handle the custom metadata
    @Shadow
    private static final Gson GSON = new GsonBuilder()
            // vanilla
            .registerTypeAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
            .registerTypeAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
            .registerTypeAdapter(ServerMetadata.class, new ServerMetadata.Deserializer())
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
            // custom
            .registerTypeHierarchyAdapter(Modpack.class, new ModpackDeserializer())
            .create();

    @Shadow @Final
    private ServerMetadata metadata;

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
    public void parseMinepkgMetadata(PacketByteBuf packetByteBuf, CallbackInfo ci) {
        EventServerQueryResponse.onServerQueryResponse((MinepkgServerMetadata) metadata, metadata);
    }
}
