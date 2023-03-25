package io.minepkg.companion.mixin;

import com.google.gson.*;
import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.Modpack;
import io.minepkg.companion.events.EventServerQueryResponse;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Modifies the ClientQueryResponse packet class on the client's side
// To parse minepkg metadata from ClientQueryResponses (Server List Ping)
@Mixin(QueryResponseS2CPacket.class)
public abstract class MixinClientQueryResponseS2CPacket_common {
    @Shadow
    @Final
    private ServerMetadata metadata;

    // A GSON object used to serialize the custom metadata.
    private static final Gson CUSTOM_GSON = (new GsonBuilder())
            .registerTypeAdapter(CustomServerMetadata.class, new CustomServerMetadata.Serializer())
            .registerTypeHierarchyAdapter(Modpack.class, new Modpack.Serializer())
            .registerTypeHierarchyAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
            .registerTypeHierarchyAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
            .create();

    @Unique
    private String stringifiedJson;

    /** Captures the value of packetByteBuf.readString(32767) inside the constructor call */
    @ModifyArg(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;deserialize(Lcom/google/gson/Gson;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"),
        index = 1
    )
    public String captureJson(String str) {
        stringifiedJson = str;
        return str;
    }

    /** Executes after captureJSON and the constructor itself to parse the minepkg metadata */
    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("RETURN"))
    public void parseMinepkgMetadata(PacketByteBuf packetByteBuf, CallbackInfo ci) {
        // Parse the JSON
        JsonElement element = new JsonParser().parse(stringifiedJson);
        JsonObject obj = element.getAsJsonObject();

        // If the data came from a vanilla/non-minepkg-modpack server
        if (!obj.has("minepkgModpack")) {
            // Fire the vanilla/non-minepkg-modpack event (server might have a modpack, but it's not from the minepkg site)
            EventServerQueryResponse.onServerQueryResponse(metadata);
            return;
        }

        // Assign the custom metadata field for use in our code
        CustomServerMetadata customMetadata = JsonHelper.deserialize(CUSTOM_GSON, stringifiedJson, CustomServerMetadata.class);
        // Fire the minepkg-modpack event (server has a modpack from the minepkg site)
        EventServerQueryResponse.onCustomServerQueryResponse(customMetadata, metadata);

        stringifiedJson = null; // free memory
    }
}
