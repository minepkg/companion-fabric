package me.jasonhaxstuff.minepkgcompanion.mixin;

import me.jasonhaxstuff.minepkgcompanion.MinePkgCompanion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// This lets us work around the title screen
@Mixin(TitleScreen.class)
public class MinePkgCompanionPluginMixin {
	// Here, we add our code onto the end of the render method of the title screen (called every tick [or so])
	@Inject(method = "render", at = @At("RETURN"))
	private void onRenderTitleScreen (CallbackInfo ci) {
		// Get the env var
		String var = System.getenv("MINEPKG_COMPANION_PLAY");

		// If we haven't opened the title screen yet and the var exists
		if (!MinePkgCompanion.INSTANCE.opened && var != null) {
			// We opened the title screen
			MinePkgCompanion.INSTANCE.opened = true;

			// If it's a local world
			if (var.toLowerCase().startsWith("singleplayer:")) {
				// Remove the local world identifier
				var = var.substring("singleplayer:".length()).toLowerCase();

				// If we succeeded in joining the local world
				if (joinLocalWorld(var)) {
					return;
				}
			}

			// Otherwise join the server
			joinServer(var);
		}
	}

	private boolean joinLocalWorld (String name) {
		// Get all of the levels
		LevelStorage levels = MinecraftClient.getInstance().getLevelStorage();
		List<LevelSummary> levelSummaries;

		try {
			levelSummaries = levels.getLevelList();

			for (LevelSummary level : levelSummaries) {
				// Check if the level is the one that we want to join
				if (level.getName().toLowerCase().equals(name)) {
					// Start the integrated server on this level
					MinecraftClient.getInstance().startIntegratedServer(level.getName(),
							level.getDisplayName(), null);
					return true;
				}
			}
		} catch (LevelStorageException e) {
			// Any exceptions and we try to join a server
			return false;
		}

		// If we didn't find the world, try to join a server.
		return false;
	}

	private void joinServer (String hostname) {
		// Create a server entry
		ServerEntry entry = new ServerEntry(hostname, hostname, true);
		// Join the server
		MinecraftClient.getInstance().openScreen(new ConnectScreen(null, MinecraftClient.getInstance(), entry));
	}
}