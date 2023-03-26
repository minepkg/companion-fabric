package io.minepkg.companion.events;

import io.minepkg.companion.MinepkgCompanion;
import io.minepkg.companion.MinepkgServerMetadata;
import io.minepkg.companion.Modpack;
import net.minecraft.server.ServerMetadata;

public class EventServerQueryResponse {
    /**
     * Called whenever a server responds with information about the server.
     */
    public static void onServerQueryResponse(MinepkgServerMetadata customMetadata, ServerMetadata metadata) {
        Modpack serverModpack = customMetadata.getModpack();

        if (serverModpack == null)
            return;

        Modpack modpack = MinepkgCompanion.getModpack();

        if (modpack == null || !modpack.name().equalsIgnoreCase(serverModpack.name()) || !modpack.version().equalsIgnoreCase(serverModpack.version())) {
            // We aren't running the same modpack as the server (or none at all)
            // Make Minecraft think that the server is out of date with a protocol version of -1
            // It will then display the modpack in red by the server name
            String gameVersion = "Modpack: " + serverModpack.name() + "@" + serverModpack.version();
            ServerMetadata.Version version = new ServerMetadata.Version(gameVersion, -1);

            MinepkgCompanion.INSTANCE.versionSetter.set(metadata, version);

            return;
        }

        // Modpacks match!
        // Might render a check mark by the server name here in the future
    }
}
