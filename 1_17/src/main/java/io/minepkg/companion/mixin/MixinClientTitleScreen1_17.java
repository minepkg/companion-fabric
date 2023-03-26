package io.minepkg.companion.mixin;

import io.minepkg.companion.MinepkgCompanion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinClientTitleScreen1_17 {
	// Here, we add our code onto the end of the render method of the title screen (called many times per second)
	@Inject(method = "render", at = @At("RETURN"))
	private void tryJoinServerOrWorld(CallbackInfo ci) {
		// (Prefixed) address of the server or world to join
		String address = System.getenv("MINEPKG_COMPANION_PLAY");

		// If we haven't opened the title screen yet and the var exists
		if (!MinepkgCompanion.INSTANCE.opened && address != null && !address.trim().isEmpty()) {
			// We opened the title screen
			MinepkgCompanion.INSTANCE.opened = true;

			// If it's an explicit local world
			if (address.startsWith("local://")) {
				String worldName = address.substring("local://".length());

				joinLocalWorld(worldName);
				return;
			}

			// If it's an explicit server
			if (address.startsWith("server://")) {
				// Get the hostname
				address = address.substring("server://".length());
			}

			// Join the server (implicit or explicit)
			joinServer(address);
		}
	}

	private void joinLocalWorld (String worldName) {
		MinecraftClient client = MinecraftClient.getInstance();

		try {
			for (LevelSummary level : client.getLevelStorage().getLevelList()) {
				// Check if the level is the one that we want to join
				if (level.getName().equalsIgnoreCase(worldName)) {
					// Start the integrated server on this level
					client.startIntegratedServer(level.getName());
					return;
				}
			}

			MinepkgCompanion.LOGGER.warn("couldn't find local world {}", worldName);
		} catch (LevelStorageException e) {
			MinepkgCompanion.LOGGER.error("couldn't load local world {}", worldName, e);
		}
	}

	private void joinServer (String hostname) {
		MinecraftClient client = MinecraftClient.getInstance();
		// Create a server entry
		ServerInfo entry = new ServerInfo(hostname, hostname, true);
		// Join the server
		ConnectScreen.connect(client.currentScreen, client, ServerAddress.parse(entry.address), entry);
	}
}
