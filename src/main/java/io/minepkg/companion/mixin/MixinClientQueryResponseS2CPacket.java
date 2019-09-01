package io.minepkg.companion.mixin;

import com.google.gson.*;
import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.Modpack;
import io.minepkg.companion.events.EventServerQueryResponse;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

// Modifies the ClientQueryResponse packet class on the client's side
// To parse minepkg metadata from ClientQueryResponses (Server List Ping)
@Mixin(QueryResponseS2CPacket.class)
public class MixinClientQueryResponseS2CPacket {
    // Copies this property from the original class
    @Shadow
    private ServerMetadata metadata;

    // Whether or not we've modified the GSON object
    private boolean modifiedGson = false;

    // Copies this final property from the original class
    @Shadow
    @Final
    private static Gson GSON;

    /**
     * Reads JSON from the packet buffer and sets the appropriate properties of the packet
     * @author minepkg-companion
     * @reason imperative major changes to function
     */
    @Overwrite
    public void read(PacketByteBuf buf) throws IOException {
        // If we haven't modified the GSON object already
        if (!modifiedGson) {
            try {
                // Add the type adapters to the GSON object
                MinepkgCompanion.addTypeAdapterToGson(GSON, CustomServerMetadata.Deserializer.class);
                MinepkgCompanion.addTypeAdapterToGson(GSON, Modpack.Serializer.class);
            } catch (IllegalAccessException | NoSuchFieldException | InstantiationException e) {
                // Give up
                buf.writeString(GSON.toJson(metadata));
                return;
            }

            modifiedGson = true;
        }

        // Read the stringified JSON from the buffer
        // 32767 is considered the maximum length of the JSON response from the server and 16-bit integers
        String str = buf.readString(32767);

        // Parse the JSON
        JsonElement element = new JsonParser().parse(str);
        JsonObject obj = element.getAsJsonObject();

        // Assign the metadata field for use in original MC code
        metadata = JsonHelper.deserialize(GSON, str, ServerMetadata.class);

        // If the data came from a vanilla/non-minepkg-modpack server
        if (!obj.has("minepkgModpack")) {
            // Fire the vanilla/non-minepkg-modpack event (server might have a modpack, but it's not from the minepkg site)
            EventServerQueryResponse.onServerQueryResponse(metadata);
            return;
        }

        // Assign the custom metadata field for use in our code
        CustomServerMetadata customMetadata = JsonHelper.deserialize(GSON, str, CustomServerMetadata.class);
        // Fire the minepkg-modpack event (server has a modpack from the minepkg site)
        EventServerQueryResponse.onCustomServerQueryResponse(customMetadata, metadata);
    }
}
