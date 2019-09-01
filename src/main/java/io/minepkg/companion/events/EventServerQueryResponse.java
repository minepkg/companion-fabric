package io.minepkg.companion.events;

import io.minepkg.companion.CustomServerMetadata;
import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.Modpack;
import net.minecraft.server.ServerMetadata;

public class EventServerQueryResponse {
    /**
     * Called whenever a vanilla/non-minepkg-modpack server responds with information about the server.
     */
    public static void onServerQueryResponse (ServerMetadata metadata) {
        // There is no modpack. Currently doing nothing here, might be useful in the future.
    }

    /**
     * Called whenever a minepkg-modded server responds with information about the server and modpack.
     */
    public static void onCustomServerQueryResponse (CustomServerMetadata customMetadata, ServerMetadata metadata) {
        Modpack modpack = MinepkgCompanion.getModpack();

        if (modpack == null) {
            // We aren't running a minepkg modpack
            return;
        }

        String modpackName = customMetadata.minepkgModpack.getName();
        String modpackVersion = customMetadata.minepkgModpack.getVersion();

        if (!modpack.getName().equalsIgnoreCase(modpackName) ||
            !modpack.getVersion().equalsIgnoreCase(modpackVersion)) {
            // Modpacks are different
            // Might render an 'X' by the server name here in the future
            return;
        }

        // Modpacks match!
        // Might render a check mark by the server name here in the future
    }
}
