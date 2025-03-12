package io.minepkg.companion.mixin;

	import io.minepkg.companion.MinepkgCompanion;
	import net.minecraft.client.MinecraftClient;
	import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
	import net.minecraft.client.gui.screen.TitleScreen;
	import net.minecraft.client.network.ServerAddress;
	import net.minecraft.client.network.ServerInfo;
	import net.minecraft.client.network.ServerInfo.ServerType;
	import net.minecraft.world.level.storage.LevelStorage;
	import net.minecraft.world.level.storage.LevelStorageException;
	import net.minecraft.world.level.storage.LevelSummary;
	import org.spongepowered.asm.mixin.Mixin;
	import org.spongepowered.asm.mixin.injection.At;
	import org.spongepowered.asm.mixin.injection.Inject;
	import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletionException;

@Mixin(TitleScreen.class)
public abstract class MixinClientTitleScreen1_20_3 {
	// Here, we add our code onto the end of the render method of the title screen (called many times per second)
	@Inject(method = "render", at = @At("RETURN"))
	private void tryJoinServerOrWorld(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) { // Modified parameters
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
		LevelStorage levelStorage = client.getLevelStorage();

		try {
			List<LevelSummary> levels = levelStorage.loadSummaries(levelStorage.getLevelList()).join();

			for (LevelSummary level : levels) {
				// Check if the level is the one that we want to join
				if (level.getName().equalsIgnoreCase(worldName)) {
					// Start the integrated server on this level
					client.createIntegratedServerLoader().start(level.getName(), () -> {}); // Passing empty Runnable - required for 1.20.3+ See https://maven.fabricmc.net/docs/yarn-1.20.3+build.1/net/minecraft/server/integrated/IntegratedServerLoader.html#start(net.minecraft.world.level.storage.LevelStorage.Session,java.lang.Runnable)
					return;
				}
			}

			MinepkgCompanion.LOGGER.warn("couldn't find local world {}", worldName);
		} catch (CompletionException | LevelStorageException e) {
			MinepkgCompanion.LOGGER.error("couldn't load local world {}", worldName, e);
		}
	}

	private void joinServer (String hostname) {
    MinecraftClient client = MinecraftClient.getInstance();
    // Create a server entry
    ServerInfo entry = new ServerInfo(hostname, hostname, ServerType.OTHER); // Added ServerType.OTHER - required since 1.20.3
		// See https://maven.fabricmc.net/docs/yarn-1.20.3+build.1/net/minecraft/client/network/ServerInfo.html
    
		// Join the server
    ConnectScreen.connect(client.currentScreen, client, ServerAddress.parse(entry.address), entry, false); 
	}
}