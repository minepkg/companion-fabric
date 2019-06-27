package io.minepkg.companion;

import net.fabricmc.api.ClientModInitializer;

public class MinepkgCompanion implements ClientModInitializer {
	public boolean opened = false;
	public static MinepkgCompanion INSTANCE;

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		INSTANCE = this;
		System.out.println("Started the minepkg companion.");
	}
}
