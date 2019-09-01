package io.minepkg.companion.mixin;

import com.google.gson.*;
import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.Modpack;
import net.minecraft.client.network.packet.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

// Modifies the ClientQueryResponse packet class on the server's side
// To send extra data containing modpack information
@Mixin(QueryResponseS2CPacket.class)
public class MixinServerQueryResponseS2CPacket {
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
     * Prepares & converts the custom metadata to JSON and writes it to the packet buffer
     * @author minepkg-companion
     * @reason imperative major changes to function
     */
    @Overwrite
    @SuppressWarnings("unchecked")
    public void write (PacketByteBuf buf) throws IOException {
        // Get the modpack the server is running.
        Modpack modpack = MinepkgCompanion.getModpack();

        // If we haven't modified the GSON object already
        if (!modifiedGson) {
            try {
                // Add the type adapters to the GSON object
                MinepkgCompanion.addTypeAdapterToGson(GSON, CustomServerMetadata.Deserializer.class);
                MinepkgCompanion.addTypeAdapterToGson(GSON, Modpack.Serializer.class);
            } catch (InstantiationException | NoSuchFieldException | IllegalAccessException e) {
                // Give up
                buf.writeString(GSON.toJson(metadata));
                return;
            }

            modifiedGson = true;
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
        buf.writeString(GSON.toJson(customMetadata));
    }
}
