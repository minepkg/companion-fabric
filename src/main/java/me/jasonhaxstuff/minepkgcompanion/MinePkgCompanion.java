package me.jasonhaxstuff.minepkgcompanion;

import net.fabricmc.api.ClientModInitializer;

public class MinePkgCompanion implements ClientModInitializer {
	public boolean opened = false;
	public static MinePkgCompanion INSTANCE;

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		INSTANCE = this;
		System.out.println("Started MinePkgCompanion.");
	}
}
