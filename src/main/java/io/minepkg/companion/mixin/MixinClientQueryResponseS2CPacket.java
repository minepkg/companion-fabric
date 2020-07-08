package io.minepkg.companion.mixin;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.events.EventServerQueryResponse;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

// Modifies the ClientQueryResponse packet class on the client's side
// To parse minepkg metadata from ClientQueryResponses (Server List Ping)
@Mixin(QueryResponseS2CPacket.class)
public class MixinClientQueryResponseS2CPacket {
    // Copies this property from the original class
    @Shadow
    private ServerMetadata metadata;

    // A GSON object used to serialize the custom metadata.
    private final Gson CustomGson = (new GsonBuilder())
            .registerTypeAdapter(CustomServerMetadata.class, new CustomServerMetadata.Serializer())
            .registerTypeHierarchyAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
            .registerTypeHierarchyAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
            .create();

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
        CustomServerMetadata customMetadata = JsonHelper.deserialize(CustomGson, str, CustomServerMetadata.class);
        // Fire the minepkg-modpack event (server has a modpack from the minepkg site)
        EventServerQueryResponse.onCustomServerQueryResponse(customMetadata, metadata);
    }
}
