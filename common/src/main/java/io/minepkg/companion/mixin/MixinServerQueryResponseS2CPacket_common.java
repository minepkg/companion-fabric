package io.minepkg.companion.mixin;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.Modpack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

// Modifies the ClientQueryResponse packet class on the server's side
// To send extra data containing modpack information
@Mixin(QueryResponseS2CPacket.class)
public abstract class MixinServerQueryResponseS2CPacket_common {
    @Shadow
    @Final
    private ServerMetadata metadata;

    // A GSON object used to deserialize the custom metadata.
    private static final Gson CUSTOM_GSON = (new GsonBuilder())
            .registerTypeAdapter(CustomServerMetadata.class, new CustomServerMetadata.Serializer())
            .registerTypeHierarchyAdapter(Modpack.class, new Modpack.Serializer())
            .registerTypeHierarchyAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
            .registerTypeHierarchyAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
            .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
            .create();

    @Shadow
    @Final
    private static Gson GSON;

    /**
     * Prepares & converts the custom metadata to JSON and writes it to the packet buffer
     * @author minepkg-companion
     * @reason imperative major changes to function
     */
    @Overwrite
    public void write (PacketByteBuf buf) throws IOException {
        Modpack modpack = MinepkgCompanion.getModpack();

        if (metadata == null) {
            // The metadata is null, respond with an empty object
            buf.writeString("{}");
            return;
        }

        if (modpack == null) {
            // Server isn't running a minepkg modpack, just respond normally
            buf.writeString(GSON.toJson(metadata));
            return;
        }

        // Copy the original metadata to the custom metadata and initialize the extra fields of the custom metadata
        CustomServerMetadata customMetadata = new CustomServerMetadata(metadata);
        customMetadata.minepkgModpack = modpack;

        // Convert the custom metadata to JSON and write it to the buffer
        String metadataStr = CUSTOM_GSON.toJson(customMetadata);
        buf.writeString(metadataStr);
    }
}
