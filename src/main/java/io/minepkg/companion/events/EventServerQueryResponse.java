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
    public static void onServerQueryResponse (CustomServerMetadata metadata) {
        Modpack modpack = MinepkgCompanion.getModpack();

        if (modpack == null) {
            // We aren't running a minepkg modpack
            return;
        }

        if (!modpack.getName().equalsIgnoreCase(metadata.modpack.getName()) ||
            !modpack.getVersion().equalsIgnoreCase(metadata.modpack.getVersion())) {
            // Modpacks are different
            // Might render an 'X' by the server name here in the future
            return;
        }

        // Modpacks match!
        // Might render a check mark by the server name here in the future
        System.out.println(metadata.modpack.getName() + " - " + metadata.modpack.getVersion());
    }
}
