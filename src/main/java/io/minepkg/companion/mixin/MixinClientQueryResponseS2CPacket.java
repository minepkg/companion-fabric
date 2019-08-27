package io.minepkg.companion.mixin;

import com.google.gson.*;
import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.Modpack;
import io.minepkg.companion.events.EventServerQueryResponse;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

// Modifies the server response packet class on the client's side
@Mixin(QueryResponseS2CPacket.class)
public class MixinClientQueryResponseS2CPacket {
    // Copies this property from the original class
    @Shadow
    private ServerMetadata metadata;

    private CustomServerMetadata customMetadata = null;

    // Registering all of the (de)serializers to convert JSON to objects and vice versa
    private static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(Modpack.class, new Modpack.Serializer())
            .registerTypeAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
            .registerTypeAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
            .registerTypeAdapter(CustomServerMetadata.class, new CustomServerMetadata.Deserializer())
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory()).create();

    /**
     * Reads JSON from the packet buffer and sets the appropriate properties of the packet
     * @author minepkg-companion
     * @reason imperative major changes to function
     */
    @Overwrite
    public void read(PacketByteBuf buf) throws IOException {
        // Read the stringified JSON from the buffer
        String str = buf.readString(32767);

        // Parse the JSON
        JsonElement element = new JsonParser().parse(str);
        JsonObject obj = element.getAsJsonObject();

        // Assign the metadata field for use in original MC code
        metadata = JsonHelper.deserialize(GSON, str, ServerMetadata.class);

        // If the data came from a vanilla/non-minepkg-modpack server
        if (!obj.has("modpack")) {
            // Fire the vanilla/non-minepkg-modpack event (server might have a modpack, but it's not from the minepkg site)
            EventServerQueryResponse.onServerQueryResponse(metadata);
            return;
        }

        // Assign the custom metadata field for use in our code
        customMetadata = JsonHelper.deserialize(GSON, str, CustomServerMetadata.class);
        // Fire the minepkg-modpack event (server has a modpack from the minepkg site)
        EventServerQueryResponse.onServerQueryResponse(customMetadata);
    }
}
