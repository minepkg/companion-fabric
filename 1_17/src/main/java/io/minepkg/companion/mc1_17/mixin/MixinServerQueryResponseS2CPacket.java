package io.minepkg.companion.mc1_17.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.minepkg.companion.common.MinepkgCompanion;
import io.minepkg.companion.common.MinepkgServerMetadata;
import io.minepkg.companion.common.Modpack;
import io.minepkg.companion.mc1_17.ModpackDeserializer;
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

@Mixin(QueryResponseS2CPacket.class)
public abstract class MixinServerQueryResponseS2CPacket {
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

    @Inject(method = "write", at = @At("HEAD"))
    public void addMinepkgMetadata (PacketByteBuf packetByteBuf, CallbackInfo ci) {
        MinepkgServerMetadata customMetadata = (MinepkgServerMetadata) metadata;
        customMetadata.setModpack(MinepkgCompanion.getModpack());
    }
}
