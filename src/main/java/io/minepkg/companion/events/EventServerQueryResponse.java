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

        String modpackName = customMetadata.minepkgModpack.name();
        String modpackVersion = customMetadata.minepkgModpack.version();

        if (!modpack.name().equalsIgnoreCase(modpackName) ||
            !modpack.version().equalsIgnoreCase(modpackVersion)) {
            // Modpacks are different
            // Make Minecraft think that the server is out of date with a protocol version of -1
            // It will then display the game version in red by the server name
            String gameVersion = "Modpack: " + modpackName + "@" + modpackVersion;
            ServerMetadata.Version version = new ServerMetadata.Version(gameVersion, -1);
            metadata.setVersion(version);

            return;
        }

        // Modpacks match!
        // Might render a check mark by the server name here in the future
    }
}
